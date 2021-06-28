package com.mmorpg.qx.module.object.gameobject;

import com.alibaba.fastjson.annotation.JSONField;
import com.haipaite.common.utility.New;
import com.mmorpg.qx.module.ai.AbstractAI;
import com.mmorpg.qx.module.object.manager.ObjectManager;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.NpcController;
import com.mmorpg.qx.module.object.controllers.attack.AggroList;
import com.mmorpg.qx.module.object.followpolicy.IFollowPolicy;
import com.mmorpg.qx.module.object.followpolicy.StateFollowPolicy;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.object.route.RouteRoad;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NPC
 *
 * @author wang ke
 * @since v1.0 2018年2月26日
 */
public class NpcCreature extends AbstractCreature {

    /**
     * 警戒范围
     */
    private int warnrange;
    /**
     * 离家最远距离
     **/
    private int homeRange;

    private RouteRoad routeRoad;

    private AbstractAI ai;

    /**
     * 生成时间
     */
    private long createTime;

    /**
     * 是否可见
     */
    private boolean show;

    private IFollowPolicy followPolicy;

    private AggroList aggroList;

    private Map<AbstractTrainerCreature, Long> damages = new ConcurrentHashMap<>();

    private Map<Long, Long> onAttackTime = new ConcurrentHashMap<Long, Long>();

    public NpcCreature(long objId, NpcController controller, WorldPosition position) {
        super(objId, controller, position);
        // TODO
        // setAi(new MonsterA());
        setFollowPolicy(new StateFollowPolicy(this));
        aggroList = new AggroList(this);
        createTime = System.currentTimeMillis();
    }

    public Map<Integer, AbstractTrainerCreature> getDamageRank() {
        Map<Integer, AbstractTrainerCreature> ranks = New.hashMap();
        if (getDamages() != null && (!getDamages().isEmpty())) {
            List<Entry<AbstractTrainerCreature, Long>> entrys = New.arrayList();
            for (Entry<AbstractTrainerCreature, Long> entry : getDamages().entrySet()) {
                entrys.add(entry);
            }
            Collections.sort(entrys, (o1, o2) -> {
                if (o2.getValue() > o1.getValue()) {
                    return 1;
                } else if (o2.getValue() < o1.getValue()) {
                    return -1;
                } else {
                    return 0;
                }
            });
            int i = 1;
            for (Entry<AbstractTrainerCreature, Long> entry : entrys) {
                ranks.put(i, entry.getKey());
                i++;
            }
        }
        return ranks;
    }

    public Collection<AbstractTrainerCreature> getMostDamagePlayers() {
        AbstractTrainerCreature mostPlayer = getDamageRank().get(1);
        if (mostPlayer == null) {
            return null;
        }
        return Arrays.asList(mostPlayer);
    }

    public void addDamage(AbstractCreature creature, long value) {
        AbstractTrainerCreature trainer = creature.getMaster();


        if (trainer != null) {
            if (getDamages().containsKey(trainer)) {
                Long oldValue = getDamages().get(trainer);
                if (oldValue == null) {
                    return;
                }
                getDamages().put(trainer, oldValue + value);
            } else {
                getDamages().put(trainer, Long.valueOf(value));
            }
        }
    }

    public ObjectResource getObjectResource() {
        return ObjectManager.getInstance().getObjectResource(getObjectKey());
    }


    public boolean hasRouteStep() {
        return routeRoad != null && (!routeRoad.isOver());
    }

//	public boolean canMove() {
//		return getGameAttrs().getCurrentAttr(AttrType.SPEED) > 0;
//	}

    public int getWarnrange() {
        return warnrange;
    }

    public void setWarnrange(int warnrange) {
        this.warnrange = warnrange;
    }

    public boolean isRestore() {
        return false;
    }

    public RouteRoad getRouteRoad() {
        return routeRoad;
    }

    public void setRouteRoad(RouteRoad routeRoad) {
        this.routeRoad = routeRoad;
    }

    @Override
    public String getName() {
        return getObjectResource().getName();
    }

    @Override
    public NpcController getController() {
        return (NpcController) super.getController();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void onDrop(Object reward, AbstractCreature lastAttacker, Player mostDamagePlayer) {

    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean outWarning(int tx, int ty) {
        return followPolicy.outWarning(tx, ty);
    }

    public boolean tooFarFromHome(int tx, int ty) {
        return followPolicy.tooFarFromHome(tx, ty);
    }

    public AbstractAI getAI() {
        return ai;
    }

    public void setAi(AbstractAI ai) {
        this.ai = ai;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.NPC;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public IFollowPolicy getFollowPolicy() {
        return followPolicy;
    }

    public void setFollowPolicy(IFollowPolicy followPolicy) {
        this.followPolicy = followPolicy;
    }

    public int getHomeRange() {
        return homeRange;
    }

    public void setHomeRange(int homeRange) {
        this.homeRange = homeRange;
    }

    public void onAtSpawnLocation() {
        getDamages().clear();
    }


    public AggroList getAggroList() {
        return this.aggroList;
    }

    @JSONField(serialize = false)
    public Map<AbstractTrainerCreature, Long> getDamages() {
        return damages;
    }

    @JSONField(serialize = false)
    public void setDamages(Map<AbstractTrainerCreature, Long> damages) {
        this.damages = damages;
    }

    @JSONField(serialize = false)
    public Map<Long, Long> getOnAttackTime() {
        return onAttackTime;
    }

    @JSONField(serialize = false)
    public void setOnAttackTime(Map<Long, Long> onAttackTime) {
        this.onAttackTime = onAttackTime;
    }

}
