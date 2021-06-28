package com.mmorpg.qx.module.object.controllers.stats;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.packet.LifeChangeResp;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生物血量,状态等业务
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class CreatureLifeStats<T extends AbstractCreature> {
    protected static final Logger log = SysLoggerFactory.getLogger(CreatureLifeStats.class);

    //protected int currentHp;
    protected int currentMp;
    protected int currentGold;
    /**
     * 装备附加血量
     */
    protected int equipAddHp;
    /**
     * 效果附加血量
     */
    protected int effectAddHp;
    /**
     * 基础生命
     */
    protected int baseHp;

    private AtomicBoolean isDead = new AtomicBoolean(false);

    protected AbstractCreature owner;

    protected final ReentrantLock hpLock = new ReentrantLock();
    private final ReentrantLock mpLock = new ReentrantLock();
    private final ReentrantLock goldLock = new ReentrantLock();

    protected Future<?> lifeRestoreTask;

    public CreatureLifeStats(AbstractCreature owner, int currentHp, int currentMp) {
        super();
        this.owner = owner;
        //this.currentHp = currentHp;
        alterHp(currentHp);
        this.currentMp = currentMp;
        this.baseHp = currentHp;
    }

    /**
     * @return the owner
     */
    public AbstractCreature getOwner() {
        return owner;
    }

    /**
     * @return the currentHp
     */
//    public int getCurrentHp() {
//        return currentHp;
//    }
    public int getCurrentHp() {
        return baseHp + effectAddHp + equipAddHp;
    }

    /**
     * @return the currentMp
     */
    public int getCurrentMp() {
        return currentMp;
    }

    /**
     * @return maxHp of creature according to stats
     */
    public int getMaxHp() {
        return getMaxAttr(AttrType.Max_Hp);
    }

    /**
     * @return maxMp of creature according to stats
     */
    public int getMaxMp() {
        return getMaxAttr(AttrType.Max_Mp);
    }

    public int getMaxAttr(AttrType maxAttrType) {
        return owner.getAttrController().getCurrentAttr(maxAttrType);
    }

    /**
     * 根据属性模块ID及属性类型获取属性值
     *
     * @param attrEffectId
     * @param attrType
     * @return
     */
    public int getMaxAttrByAttrEffectId(AttrEffectId attrEffectId, AttrType attrType) {
        return owner.getAttrController().getAttrByAttrEffectId(attrEffectId, attrType);
    }

    /**
     * @return the alreadyDead There is no setter method cause life stats should be
     * completely renewed on revive
     */
    public boolean isAlreadyDead() {
        return isDead.get();
    }

    public boolean isAlive() {
        return isAlreadyDead() == false;
    }

    /**
     * 技能或效果直接扣血，不做广播，由技能效果同步伤害
     *
     * @param value
     * @param attacker
     * @return currentHp
     */
    public int reduceHp(int value, AbstractCreature attacker, int skillId, int effectId) {
        hpLock.lock();
        int oldHp = getCurrentHp();
        try {
            if (isDead.get()) {
                return 0;
            }
//            int newHp = this.currentHp - value;
//            if (newHp <= 0) {
//                newHp = 0;
//            }
//            this.currentHp = newHp;
            alterHp(-value);
            onReduceHp(attacker, skillId, effectId);
        } finally {
            hpLock.unlock();
        }
        return getCurrentHp() - oldHp;
    }

    /**
     * 由各种原因扣除血量
     *
     * @param value
     * @param attacker
     * @return currentHp
     */
    public int reduceHp(int value, AbstractCreature attacker, Reason reason, boolean needSync) {
        hpLock.lock();
        int oldHp = getCurrentHp();
        try {
            if (isDead.get()) {
                return 0;
            }
//            int newHp = this.currentHp - value;
//            if (newHp < 0) {
//                newHp = 0;
//            }
//            this.currentHp = newHp;
            alterHp(-value);
        } finally {
            hpLock.unlock();
        }
        if (needSync) {
            sendLifeChange(reason);
        }
        onReduceHp(attacker, 0, 0);
        return getCurrentHp() - oldHp;
    }

    /**
     * 由于装备效果移除或者添加导致血量属性变化
     *
     * @param value
     * @return currentHp
     */
    public int reduceHpByRemoveAttr(int value, AttrEffectId attrEffect, boolean broadcast, boolean needSync) {
        hpLock.lock();
        int oldHp = getCurrentHp();
        try {
            if (isDead.get()) {
                return 0;
            }
//            int newHp = this.currentHp - value;
//            if (newHp < 0) {
//                newHp = 0;
//            }
//            this.currentHp = newHp;
            alterHp(-value, attrEffect);
        } finally {
            hpLock.unlock();
        }
        if (needSync) {
            sendLifeChange(Reason.Attr_Change);
        }
        onReduceHp(null, 0, 0);
        return getCurrentHp() - oldHp;
    }

    public void sendLifeChange(Reason reason) {
        LifeChangeResp lifeChangeResp = LifeChangeResp.valueOf(reason, owner);
        PacketSendUtility.broadcastInWorldMap(owner, lifeChangeResp, true);
        System.err.println("同步生命特征: " + lifeChangeResp.getId() + " 当前血量： " + getCurrentHp()+"  reason: "+reason);
    }

    /**
     * 业务导致掉血，非技能战斗掉血
     *
     * @param value
     * @return currentMp
     */
    public int reduceMp(int value, Reason reason, boolean broadcast, boolean needSync) {
        mpLock.lock();
        int oldMp = getCurrentMp();
        try {
            int newMp = this.currentMp - value;
            if (newMp < 0) {
                newMp = 0;
            }
            this.currentMp = newMp;
        } finally {
            mpLock.unlock();
        }
        onReduceMp();
        if (needSync) {
            sendLifeChange(reason);
        }
        return currentMp - oldMp;
    }

    /**
     * 由于各种原因导致血量恢复
     *
     * @param value
     * @return currentHp
     */
    public int increaseHp(int value, Reason reason, boolean broadcast, boolean needSync) {
        hpLock.lock();
        int alterHp = 0;
        try {
            if (isAlreadyDead()) {
                return 0;
            }
            if (value < 0) {
                throw new RuntimeException("加血出现负值!value[" + value + "]");
            }
//            int newHp = this.currentHp + value;
//            if (newHp > getMaxHp()) {
//                newHp = getMaxHp();
//            }
//            this.currentHp = newHp;
            alterHp = alterHp(value);
        } finally {
            hpLock.unlock();
        }
        onIncreaseHp(value);
        if (needSync) {
            sendLifeChange(reason);
        }
        return alterHp;
    }

    /**
     * 上调血量属性导致血量上涨
     *
     * @param value
     * @param attrEffect
     * @return
     */
    public int increaseHpByAddAttr(int value, AttrEffectId attrEffect, boolean broadcast, boolean needSync) {
        hpLock.lock();
        int alterHp = 0;
        try {
            if (isAlreadyDead()) {
                return 0;
            }
            if (value <= 0) {
                throw new RuntimeException("加血出现负值!value[" + value + "]");
            }
//            int newHp = this.currentHp + value;
//            if (newHp > getMaxHp()) {
//                newHp = getMaxHp();
//            }
//            this.currentHp = newHp;
            alterHp = alterHp(value, attrEffect);
        } finally {
            hpLock.unlock();
        }
        onIncreaseHp(value);
        if (needSync) {
            sendLifeChange(Reason.Attr_Change);
        }
        return alterHp;
    }


    /**
     * This method is called whenever caller wants to restore creatures's MP
     *
     * @param value
     * @return currentMp
     */
    public int increaseMp(int value, Reason reason, boolean broadcast, boolean needSync) {
        mpLock.lock();
        int oldMp = getCurrentMp();
        try {
            if (isAlreadyDead()) {
                return 0;
            }
            int newMp = this.currentMp + value;
            if (newMp > getMaxMp()) {
                newMp = getMaxMp();
            }
            if (currentMp != newMp) {
                this.currentMp = newMp;
                onIncreaseMp(value);
            }
        } finally {
            mpLock.unlock();
        }
        if (needSync) {
            sendLifeChange(reason);
        }
        return currentMp - oldMp;
    }

    /**
     * Will trigger restore task if not already
     */
    protected void triggerRestoreTask() {
        // 暂时没有自动恢复功能
        // if (lifeRestoreTask == null && !alreadyDead) {
        // // 这里提交一个任务
        // lifeRestoreTask =
        // LifeStatsRestoreService.getInstance().scheduleRestoreTask(this);
        // }
    }

    /**
     * @return true or false
     */
    public boolean isFullyRestoredHpMp() {
        return getMaxHp() == getCurrentHp() && getMaxMp() == currentMp;
    }

    /**
     * @return
     */
    public boolean isFullyRestoredHp() {
        return getMaxHp() <= getCurrentHp();
    }

    public boolean isFullyRestoredMp() {
        return getMaxMp() <= currentMp;
    }

    /**
     * 基础血量可恢复生命值
     *
     * @return
     */
    public int getBaseHpRestore() {
        return getMaxAttrByAttrEffectId(AttrEffectId.valueOf(AttrEffectType.Level_Base), AttrType.Max_Hp) - baseHp;
    }

    /**
     * 装备血量可恢复生命值
     *
     * @return
     */
    public int getEquipAddHpResotre() {
        return getMaxAttrByAttrEffectId(AttrEffectId.valueOf(AttrEffectType.Equip_Effect), AttrType.Max_Hp) - equipAddHp;
    }

    /**
     * 效果属性血量可恢复生命值
     *
     * @return
     */
    public int getEffectAddHpRestore() {
        return getMaxAttrByAttrEffectId(AttrEffectId.valueOf(AttrEffectType.Buff_Effect), AttrType.Max_Hp) - effectAddHp;
    }


    /**
     * The purpose of this method is synchronize current HP and MP with updated
     * MAXHP and MAXMP stats This method should be called only on creature load to
     * game or player level up
     */
    public void synchronizeWithMaxStats() {
        hpLock.lock();
        try {
            int maxHp = getMaxHp();
            if (getCurrentHp() != maxHp) {
                alterHp(maxHp);
            }
        } catch (Exception e) {

        } finally {
            hpLock.unlock();
        }

        mpLock.lock();
        try {
            int maxMp = getMaxMp();
            if (currentMp != maxMp) {
                currentMp = maxMp;
            }
        } catch (Exception e) {
        } finally {
            mpLock.unlock();
        }

    }


    /**
     * The purpose of this method is synchronize current HP and MP with MAXHP and
     * MAXMP when max stats were decreased below current level
     */
    public void updateCurrentStats() {
        int maxHp = getMaxHp();
        if (maxHp < getCurrentHp()) {
            alterHp(maxHp);
        }

        int maxMp = getMaxMp();
        if (maxMp < currentMp) {
            currentMp = maxMp;
        }

        if (!isFullyRestoredHpMp()) {
            triggerRestoreTask();
        }
    }

    protected void onIncreaseMp(long value) {
    }

    protected void onReduceMp() {

    }

    protected void onIncreaseHp(long value) {
    }

    protected void onReduceHp(AbstractCreature attacker, int skillId, int effectId) {
        if (getCurrentHp() <= 0 && isDead.compareAndSet(false, true)) {
            //魔物娘处于回合战斗，不能先把战死一方移除，待回合战斗完再移除
            if (RelationshipUtils.isMWN(attacker) && RelationshipUtils.isMWN(this.getOwner())) {
                MWNCreature mwnCreatureA = RelationshipUtils.toMWNCreature(this.getOwner());
                Room room = mwnCreatureA.getMaster().getRoom();
                if (room != null) {
                    if (!room.isAttackMWN(mwnCreatureA) && !room.isDefenseMWN(mwnCreatureA)) {
                        String info = String.format("扣血触发死亡【%s】 攻击方【%s】", attacker.getObjectId(), getOwner().getObjectId());
                        System.err.println(info);
                        log.info(info);
                        getOwner().getController().onDie(attacker, skillId, effectId);
                    }
                }
            } else {
                getOwner().getController().onDie(attacker, skillId, effectId);
            }
        }
    }

    /**
     * @param value
     * @return
     */
    public int increaseFp(int value) {
        return 0;
    }

    /**
     * This method can be used for Npc's to fully restore its HP and remove dead
     * state of lifestats
     *
     * @param hpPercent
     */
    public void setCurrentHpPercent(int hpPercent) {
        hpLock.lock();
        try {
            int maxHp = getMaxHp();
            int oldCurrentHp = this.getCurrentHp();
            int currentHp = Math.round(maxHp * GameUtil.toRatio10000(hpPercent));
            alterHp(currentHp - oldCurrentHp);
            if (currentHp > 0 && oldCurrentHp <= 0) {
                this.isDead.set(Boolean.FALSE);
                owner.getController().onRelive();
            }
        } finally {
            hpLock.unlock();
        }
    }

    /**
     * @param hp
     */
    public void setCurrentHp(int hp) {
        hpLock.lock();
        try {
            int oldHp = this.getCurrentHp();
            alterHp(hp - oldHp);
            if (hp > 0 && oldHp <= 0) {
                this.isDead.set(Boolean.FALSE);
                owner.getController().onRelive();
            }
        } finally {
            hpLock.unlock();
        }
    }

    public int setCurrentMp(int value) {
        mpLock.lock();
        try {
            int newMp = value;

            if (newMp < 0) {
                newMp = 0;
            }
            this.currentMp = newMp;
        } finally {
            mpLock.unlock();
        }
        return currentMp;
    }

    /**
     * This method can be used for Npc's to fully restore its MP
     *
     * @param mpPercent
     */
    public void setCurrentMpPercent(int mpPercent) {
        mpLock.lock();
        try {
            int maxMp = getMaxMp();
            this.currentMp = Math.round(maxMp * GameUtil.toRatio10000(mpPercent));
        } finally {
            mpLock.unlock();
        }
    }

    /**
     * 满血满蓝
     */
    protected void fullStoreHpAndMp(boolean isRelive) {
        this.setCurrentMpPercent(100);
        if (isRelive) {
            this.setCurrentHpPercent(100);
        } else {
            this.increaseHp(getMaxHp() - getCurrentHp(), null, true, true);
        }
    }

    public float getHpPercentage() {
        return (float) (100.0F * getCurrentHp() / getMaxHp());
    }

    public boolean isHpBlowPercentage(int percent) {
        if (getHpPercentage() <= percent) {
            return true;
        }
        return false;
    }


    public void triggerRestoreOnRevive() {
        this.triggerRestoreTask();
    }

    public int reduceGold(int value, Reason reason, boolean broadcast) {
        goldLock.lock();
        try {
            int gold = this.currentGold - value;

            if (gold < 0) {
                gold = 0;
            }
            this.currentGold = gold;
        } finally {
            goldLock.unlock();
        }
        sendLifeChange(reason);
        return currentGold;
    }


    public int increaseGold(int value, Reason reason, boolean broadcast) {
        goldLock.lock();
        try {
            System.err.println("当前 " + owner.getObjectId() + " 金币: " + currentGold + " 获得金币:" + value + "  Reason: " + reason);
            if (value < 0) {
                throw new RuntimeException("加金币出现负值!value[" + value + "]");
            }
            if (this.getOwner().getEffectController().isInStatus(EffectStatus.Inc_Gold)) {
                Effect effect = this.getOwner().getEffectController().getEffect(EffectType.Inc_Gold);
                if (Objects.nonNull(effect)) {
                    if (effect.getEffectResource().isValueRate()) {
                        value += Math.round(value * GameUtil.toRatio10000(effect.getValue()));
                    } else {
                        value += effect.getValue();
                    }
                }
            }
            this.currentGold += value;
        } finally {
            goldLock.unlock();
        }
        sendLifeChange(reason);
        return currentGold;
    }

    public int getCurrentGold() {
        return currentGold;
    }

    public AtomicBoolean getIsDead() {
        return isDead;
    }

    /**
     * 属性改变导致血量变化
     *
     * @param value
     * @return
     */
    private int alterHp(int value, AttrEffectId type) {
        if (value == 0) {
            return value;
        }
        if (Objects.isNull(type)) {
            return alterHp(value);
        }
        String format = String.format("生物【%s】 属性变化前 当前血量[%s] 基础数量[%s] 效果附加血量【%s】 装备附加血量[%s], 变化血量【%s】", getOwner().getObjectId(), getCurrentHp(), baseHp, effectAddHp, equipAddHp, value);
        System.err.println(format);
        log.info(format);
        int oldHp = getCurrentHp();
        try {
            switch (type.getType()) {
                case Equip_Effect:
                    equipAddHp += value;
                    int maxEquipAddHp = getMaxAttrByAttrEffectId(type, AttrType.Max_Hp);
                    if (equipAddHp > maxEquipAddHp) {
                        equipAddHp = maxEquipAddHp;
                    } else if (equipAddHp < 0) {
                        equipAddHp = 0;
                    }
                    break;
                case Buff_Effect:
                    effectAddHp += value;
                    int maxEffectAddHp = getMaxAttrByAttrEffectId(type, AttrType.Max_Hp);
                    if (effectAddHp > maxEffectAddHp) {
                        effectAddHp = maxEffectAddHp;
                    } else if (effectAddHp < 0) {
                        effectAddHp = 0;
                    }
                    break;
                case Level_Base:
                    baseHp += value;
                    int maxBaseAddHp = getMaxAttrByAttrEffectId(type, AttrType.Max_Hp);
                    if (baseHp > maxBaseAddHp) {
                        baseHp = maxBaseAddHp;
                    } else if (baseHp < 0) {
                        baseHp = 0;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("不支持血量变化：" + type);
            }
        } catch (Exception e) {
        } finally {
            String logInfo = String.format("生物【%s】属性变化后 当前血量[%s] 基础数量[%s] 效果附加血量【%s】 装备附加血量[%s]", getOwner().getObjectId(), getCurrentHp(), baseHp, effectAddHp, equipAddHp);
            System.err.println(logInfo);
            log.info(logInfo);
            return getCurrentHp() - oldHp;
        }

    }

    /**
     * 技能效果伤害改变血量
     *
     * @param value
     * @return
     */
    private int alterHp(int value) {
        String logInfo = String.format("生物【%s】 变化前 当前血量[%s] 基础数量[%s] 效果附加血量【%s】 装备附加血量[%s], 变化血量【%s】", getOwner().getObjectId(), getCurrentHp(), baseHp, effectAddHp, equipAddHp, value);
        log.info(logInfo);
        if (value == 0) {
            return value;
        }
        int oldHp = getCurrentHp();
        try {
            if (value > 0) {
                if (isFullyRestoredHp()) {
                    return 0;
                }
                //先给基础生命恢复
                int baseHpRestore = getBaseHpRestore();
                if (baseHpRestore < 0) {
                    throw new RuntimeException("基础血量值超过最大值");
                }
                if (value <= baseHpRestore) {
                    baseHp += value;
                    return value;
                }
                value -= baseHpRestore;
                baseHp += baseHpRestore;
                //其次恢复效果生命值
                int effectAddHpRestore = getEffectAddHpRestore();
                if (effectAddHpRestore < 0) {
                    throw new RuntimeException("效果属性血量值超过最大值");
                }
                if (value <= effectAddHpRestore) {
                    effectAddHp += value;
                    return getCurrentHp() - oldHp;
                }
                value -= effectAddHpRestore;
                effectAddHp += effectAddHpRestore;
                //最后恢复装备附加生命值
                int equipAddHpRestore = getEquipAddHpResotre();
                if (equipAddHpRestore < 0) {
                    throw new RuntimeException("装备属性血量值超过最大值");
                }
                if (value <= equipAddHpRestore) {
                    equipAddHp += value;
                    return getCurrentHp() - oldHp;
                }
                equipAddHp += equipAddHpRestore;
                return getCurrentHp() - oldHp;
            } else {
                if (oldHp <= 0) {
                    return 0;
                }
                value = Math.abs(value);

                //最先扣除装备附加生命值
                if (value <= equipAddHp) {
                    equipAddHp -= value;
                    return getCurrentHp() - oldHp;
                }
                value -= equipAddHp;
                equipAddHp = 0;

                //其次扣除效果附加血量
                if (value <= effectAddHp) {
                    effectAddHp -= value;
                    return getCurrentHp() - oldHp;
                }
                value -= effectAddHp;
                effectAddHp = 0;

                //最后扣除基础血量
                if (value <= baseHp) {
                    baseHp -= value;
                    return getCurrentHp() - oldHp;
                }
                value -= baseHp;
                baseHp = 0;
                return getCurrentHp() - oldHp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            String format = String.format("生物【%s】 变化后 当前血量[%s] 基础数量[%s] 效果附加血量【%s】 装备附加血量[%s]", getOwner().getObjectId(), getCurrentHp(), baseHp, effectAddHp, equipAddHp);
            System.err.println(format);
            log.info(format);
            return getCurrentHp() - oldHp;
        }
    }
}
