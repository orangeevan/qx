package com.mmorpg.qx.module.object.gameobject.attr;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.packet.CreatureAttrChangeResp;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 生物属性管理器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class CreatureAttrController {

    protected static final Logger logger = SysLoggerFactory.getLogger(CreatureAttrController.class);
    /**
     * 总属性
     */
    protected ConcurrentHashMap<AttrType, Integer> attrs;
    /**
     * 属性分类
     */
    protected ConcurrentHashMap<AttrEffectId, List<Attr>> attrEffects;
    protected AbstractCreature owner;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * @param owner
     */
    public CreatureAttrController(AbstractCreature owner) {
        this.owner = owner;
        this.attrs = new ConcurrentHashMap<>();
        this.attrEffects = new ConcurrentHashMap<>();
    }

    /***
     * 返回属性副本
     * @return
     */
    public List<Attr> getAllAttr() {
        List<Attr> allStats = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Entry<AttrType, Integer> entry : attrs.entrySet()) {
                Attr newStat = new Attr(entry.getKey(), entry.getValue());
                allStats.add(newStat);
            }
        } finally {
            lock.readLock().unlock();
        }
        return allStats;
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            attrs.clear();
            attrEffects.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /***
     * 当前属性值
     * @param attrType
     * @return
     */
    public int getCurrentAttr(AttrType attrType) {
        int value = 0;
        if (attrs.containsKey(attrType)) {
            value = attrs.get(attrType);
        }
        return value;
    }

    /**
     * 根据模块及属性类型获取值
     *
     * @param attrEffectId
     * @param attrType
     * @return
     */
    public int getAttrByAttrEffectId(AttrEffectId attrEffectId, AttrType attrType) {
        lock.readLock().lock();
        try {
            if (!attrEffects.containsKey(attrEffectId)) {
                return 0;
            }
            int value = 0;
            List<Attr> attrs = attrEffects.get(attrEffectId);
            for (Attr attr : attrs) {
                if (attr.getType() == attrType) {
                    value += attr.getValue();
                }
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 返回属性列表不能修改
     *
     * @param attrEffectId
     * @return
     */
    public List<Attr> getAttrByAttrEffectId(AttrEffectId attrEffectId) {
        List<Attr> attrList = attrEffects.getOrDefault(attrEffectId, null);
        if (!CollectionUtils.isEmpty(attrList)) {
            return Collections.unmodifiableList(attrList);
        }
        return Collections.EMPTY_LIST;
    }

    protected void onAddModifiers(AttrEffectId id, List<Attr> modifiers, boolean needSync) {
        this.owner.handleEffect(TriggerType.ATTR, owner, modifiers);
        if (RelationshipUtils.isMWN(this.owner) && this.owner.getController().isSpawn() && GameUtil.hasJobOrEleAttr(modifiers)) {
            MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(this.owner);
            mwnCreature.getMaster().getController().onMwnJobOrEleChange(mwnCreature, Reason.Attr_Change);
        }
        /**当前血量比最大生命值还高，需修正*/
        if (!CollectionUtils.isEmpty(modifiers)) {
            Optional<Attr> hpAttr = modifiers.stream().filter(attr -> attr.getType() == AttrType.Max_Hp).findAny();
            if (hpAttr.isPresent()) {
                if (hpAttr.get().getValue() < 0) {
                    owner.getLifeStats().reduceHpByRemoveAttr(-hpAttr.get().getValue(), id, false, needSync);
                }
                if (hpAttr.get().getValue() > 0 && owner.getCurrentHp() < attrs.get(AttrType.Max_Hp)) {
                    owner.getLifeStats().increaseHpByAddAttr(hpAttr.get().getValue(), id, false, needSync);
                }
            }
            Optional<Attr> mpAttr = modifiers.stream().filter(attr -> attr.getType() == AttrType.Max_Mp).findAny();
            if (mpAttr.isPresent()) {
                if (owner.getCurrentMp() > attrs.get(AttrType.Max_Mp) && mpAttr.get().getValue() < 0) {
                    owner.getLifeStats().reduceMp(owner.getCurrentMp() - attrs.get(AttrType.Max_Mp), Reason.Attr_Change, true, needSync);
                }
                if (owner.getCurrentMp() < attrs.get(AttrType.Max_Mp) && mpAttr.get().getValue() > 0) {
                    owner.getLifeStats().increaseMp(owner.getCurrentHp() + mpAttr.get().getValue(), Reason.Attr_Change, true, needSync);
                }
            }
        }
    }

    /***
     * 修改某个模块属性，整块替换,一般来说每次添加新属性模块或者替换，都需要重新计算，一次添加多个可以延迟最后一个才计算
     * @param id
     * @param modifiers
     * @param recompute
     */
    public void addModifiers(AttrEffectId id, List<Attr> modifiers, boolean recompute, boolean needSync) {
        if (CollectionUtils.isEmpty(modifiers) && !attrEffects.containsKey(id)) {
            return;
        }
        lock.writeLock().lock();
        try {
            //同一个模块属性合并
            attrEffects.put(id, modifiers);
            if (recompute) {
                recomputeAttrs(needSync);
            }
            onAddModifiers(id, modifiers, needSync);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addModifiers(AttrEffectId id, Attr modifiers, boolean recomputeStats) {
        addModifiers(id, Lists.newArrayList(modifiers), recomputeStats, true);
    }

    /**
     * @param id
     * @param modifiers
     */
    public void addModifiers(AttrEffectId id, List<Attr> modifiers) {
        this.addModifiers(id, modifiers, true, true);
    }

    public void addModifiers(AttrEffectId id, Attr[] modifiers) {
        this.addModifiers(id, Arrays.asList(modifiers), true, true);
    }

    public void addModifiers(AttrEffectId id, Attr[] modifiers, boolean recomputeStats) {
        this.addModifiers(id, Arrays.asList(modifiers), recomputeStats, true);
    }

    public void replaceModifiers(AttrEffectId id, Attr[] modifiers) {
        attrEffects.remove(id);
        this.addModifiers(id, Arrays.asList(modifiers), true, true);
    }

    public void replaceModifiers(AttrEffectId id, List<Attr> stats) {
        attrEffects.remove(id);
        if (stats == null || stats.isEmpty()) {
            return;
        }
        this.addModifiers(id, stats, true, true);
    }

    public boolean isAdd(AttrEffectId id) {
        return attrEffects.containsKey(id);
    }

    private Map<AttrType, Integer> doRecomputeAttrs(boolean useNoBattleScoreStats) {
        Map<AttrType, Integer> newStats = new HashMap<>();
        for (List<Attr> stats : attrEffects.values()) {
            for (Attr stat : stats) {
                if (!newStats.containsKey(stat.getType())) {
                    newStats.put(stat.getType(), stat.getValue());
                } else {
                    newStats.put(stat.getType(), newStats.get(stat.getType()) + stat.getValue());
                }

            }
        }
        return newStats;
    }

    public List<AttrVo> getAttrVos() {
        List<AttrVo> statVos = new ArrayList<>();
        for (Entry<AttrType, Integer> entry : attrs.entrySet()) {
            statVos.add(AttrVo.valueOf(entry.getKey().value(), entry.getValue()));
        }
        return statVos;
    }

    /**
     * 重新计算所有属性，变化属性同步
     */
    private void recomputeAttrs(boolean needSync) {
        Map<AttrType, Integer> newAttrs = doRecomputeAttrs(true);
        MapDifference<AttrType, Integer> mapDifference = Maps.difference(attrs, newAttrs);
        //如总属性没有变化无需重新计算同步
        if (mapDifference.areEqual()) {
            return;
        }
        Map<AttrType, ValueDifference<Integer>> difference = new HashMap<>(mapDifference.entriesDiffering());
        Map<AttrType, Integer> onlyOnRight = new HashMap<>(mapDifference.entriesOnlyOnRight());
        Map<AttrType, Integer> onlyOnLeft = new HashMap<>(mapDifference.entriesOnlyOnLeft());
        if (!CollectionUtils.isEmpty(attrs)) {
            attrs.clear();
        }
        attrs.putAll(newAttrs);
        if (!owner.isInWorldMap()) {
            return;
        }
        if (!needSync) {
            return;
        }
        /** 修改当前血量，可能触发死亡，需要及时同步*/
        for (AttrType attrType : AttrType.NEED_QUICK_SYNC_ATTR) {
            if (difference.containsKey(attrType) || onlyOnLeft.containsKey(attrType) || onlyOnRight.containsKey(attrType)) {
                CreatureAttrChangeResp resp = new CreatureAttrChangeResp();
                resp.getAttrs().add(AttrVo.valueOf(attrType.value(), attrs.get(attrType)));
                resp.setObjectType(owner.getObjectType().getValue());
                resp.setId(owner.getObjectId());
                PacketSendUtility.broadcastInWorldMap(owner, resp, true);
                difference.remove(attrType);
                onlyOnRight.remove(attrType);
                onlyOnLeft.remove(attrType);
            }
        }
        /** 除了需要快速同步属性外没有其他属性变化返回*/
        if (difference.isEmpty() && onlyOnLeft.isEmpty() && onlyOnRight.isEmpty()) {
            return;
        }
        if (owner.getObjectType() == ObjectType.PLAYER_TRAINER || owner.getObjectType() == ObjectType.ROBOT_TRAINER) {
            owner.getCreatureUpdateTask().addUpdateTaskDelaySecond(CreatureUpdateType.ATTR, new AbstractDispatcherHashCodeRunable() {
                @Override
                public String name() {
                    return "recomputeAttrs";
                }

                @Override
                public int getDispatcherHashCode() {
                    return owner.getDispatcherHashCode();
                }

                @Override
                public void doRun() {
                    CreatureAttrChangeResp resp = new CreatureAttrChangeResp();
                    resp.setAttrs(getAttrVos());
                    resp.setId(owner.getObjectId());
                    resp.setObjectType(owner.getObjectType().getValue());
                    PacketSendUtility.broadcastInWorldMap(owner, resp, true);
                }
            }, 1);
        }
    }

    /**
     * 移除对于模块添加属性入口，不能直接调用addModifiers方法
     *
     * @param id
     */
    public void removeModifiers(AttrEffectId id) {
        this.removeModifiers(id, true);
    }

    protected void onEndModifiers(AttrEffectId id) {

    }

    public void removeModifiers(AttrEffectId id, boolean recomputeStats) {
        if (attrEffects.remove(id) == null) {
            return;
        }
        if (recomputeStats) {
            recomputeAttrs(true);
        }
        onEndModifiers(id);
    }

    public int attrSize() {
        int count = 0;
        lock.readLock().lock();
        try {
            for (List<Attr> stats : this.attrEffects.values()) {
                count += stats.size();
            }
        } finally {
            lock.readLock().unlock();
        }
        return count;
    }


    @Override
    public String toString() {
        TreeMap<AttrEffectId, List<Attr>> treeMap = new TreeMap<>();
        treeMap.putAll(this.attrEffects);
        return JsonUtils.object2String(treeMap);
    }

    public boolean hasAttrEffect(AttrEffectId effectId) {
        return attrEffects.containsKey(effectId);
    }

    /***
     * 是否有属性
     * @param attrType
     * @return
     */
    public boolean hasAttr(AttrType attrType) {
        return attrs.containsKey(attrType) && attrs.get(attrType) > 0;
    }

    public boolean hasAnyAttr() {
        return attrs.size() > 0;
    }

}