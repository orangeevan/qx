package com.mmorpg.qx.module.worldMap.model;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 地图实例对象
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class WorldMapInstance {
    private static final Logger logger = SysLoggerFactory.getLogger(WorldMapInstance.class);

    public static final int regionX = 40;

    public static final int regionY = 20;

    private static final int maxWorldSize = 1000;

    private static final int MAX_DEAD_REMOVE_TIME = 20000;

    private final WorldMap parent;

    /**
     * 地图上所有的物体 //TODO 后期全部移到gridCreature里
     */
    private final Map<Long, AbstractVisibleObject> worldMapObjects = new ConcurrentHashMap<>();

    /**
     * 地图上所有的玩家
     */
    private final Map<Long, AbstractTrainerCreature> trainers = new ConcurrentHashMap<>();

    private Future<?> emptyInstanceTask = null;


    private int instanceId;

    private AtomicInteger playerSize = new AtomicInteger(0);
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 是否开启安全区
     */
    private boolean openSafeArea = true;
    /**
     * 阻挡区
     */
    private Set<Integer> blocks = new HashSet<>();
    /**
     * 已经销毁
     */
    private volatile boolean destroy;

    /**
     * 地图上生成过格子
     */
    private ConcurrentHashMap<Integer, WorldPosition> positions = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, List<AbstractVisibleObject>> gridCreatures = new ConcurrentHashMap<>();

    /**
     * 出生点
     */
    private WorldPosition birthPosition;

    public WorldMapInstance(WorldMap parent, int instanceId) {
        this.parent = parent;
        this.instanceId = instanceId;
        this.createTime = System.currentTimeMillis();
        this.birthPosition = WorldPosition.buildWorldPosition(this, this.parent.getBirthGrid());
    }

    public WorldMap getParent() {
        return parent;
    }


    public boolean isEmpty(int gridId) {
        if (parent.getMapGrid(gridId) != null && !gridCreatures.containsKey(gridId)) {
            return true;
        }
        return CollectionUtils.isEmpty(gridCreatures.get(gridId));
    }

    public void addObject(AbstractVisibleObject object) {
        if (worldMapObjects.put(object.getObjectId(), object) != null) {
            return;
        }

        if (object instanceof AbstractTrainerCreature) {
            AbstractTrainerCreature trainerCreature = (AbstractTrainerCreature) object;
            trainers.put(object.getObjectId(), trainerCreature);
            if (RelationshipUtils.isPlayerTrainer(trainerCreature)) {
                playerSize.incrementAndGet();
            }
        }
        //把生物缓存到地格上

        List<AbstractVisibleObject> objects = gridCreatures.getOrDefault(object.getGridId(), new ArrayList<>(2));
        objects.add(object);
        gridCreatures.putIfAbsent(object.getGridId(), objects);
    }

    public void removeObject(AbstractVisibleObject object) {
        worldMapObjects.remove(object.getObjectId());
        if (object instanceof PlayerTrainerCreature) {
            if (trainers.remove(object.getObjectId()) != null) {
                playerSize.decrementAndGet();
            }
        }
        List<AbstractVisibleObject> orDefault = gridCreatures.getOrDefault(object.getGridId(), null);
        if (!CollectionUtils.isEmpty(orDefault)) {
            orDefault.remove(object);
        }
    }

    public AbstractVisibleObject findObject(long objId) {
        return worldMapObjects.get(objId);
    }


    public AbstractTrainerCreature findTrainer(long objId) {
        return trainers.get(objId);
    }

    public int getInstanceId() {
        return instanceId;
    }

    public Iterator<AbstractVisibleObject> objectIterator() {
        return worldMapObjects.values().iterator();
    }

    public Iterator<AbstractTrainerCreature> trainerIterator() {
        return trainers.values().iterator();
    }

    public int getPlayerCount() {
        return playerSize.get();
    }

    public boolean isFull() {
        if (parent.getMaxNum() <= 0) {
            return false;
        }
        return playerSize.get() >= parent.getMaxNum();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public boolean isOpenSafeArea() {
        return openSafeArea;
    }

    public void setOpenSafeArea(boolean openSafeArea) {
        this.openSafeArea = openSafeArea;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public Collection<AbstractTrainerCreature> getTrainers() {
        return trainers.values();
    }

    public boolean hasMwnInGrid(int gridId) {
        if (CollectionUtils.isEmpty(worldMapObjects)) {
            return false;
        }
        MWNCreature byGridId = getMWNByGridId(gridId);
        return Objects.nonNull(byGridId);
    }

    public List<AbstractVisibleObject> getCreaturesByGrid(int gridId) {
        return gridCreatures.getOrDefault(gridId, Collections.EMPTY_LIST);
    }

    public AbstractVisibleObject getCreature(int grid, ObjectType objectType) {
        List<AbstractVisibleObject> creaturesByGrid = getCreaturesByGrid(grid);
        if (CollectionUtils.isEmpty(creaturesByGrid)) {
            return null;
        }
        return creaturesByGrid.stream().filter(object -> object.getObjectType() == objectType).findAny().orElse(null);
    }

    public AbstractVisibleObject getItemByGridId(int gridId) {
        return getCreature(gridId, ObjectType.ITEM);
    }

    public Collection<AbstractTrainerCreature> getAllTrainer() {
        if (CollectionUtils.isEmpty(worldMapObjects)) {
            return null;
        }
        List<AbstractTrainerCreature> trainerList = new ArrayList<>();
        worldMapObjects.values().stream().filter(object -> object instanceof AbstractTrainerCreature).forEach(trainer -> trainerList.add((AbstractTrainerCreature) trainer));
        return trainerList;
    }

    /**
     * 获取指定格子上魔物娘
     *
     * @param gridId
     * @return
     */
    public MWNCreature getMWNByGridId(int gridId) {
        AbstractVisibleObject creature = getCreature(gridId, ObjectType.MWN);
        if (Objects.isNull(creature)) {
            return null;
        }
        return RelationshipUtils.toMWNCreature((AbstractCreature) creature);
    }

    public List<MWNCreature> getMWNList(int gridId) {
        List<AbstractVisibleObject> creaturesByGrid = getCreaturesByGrid(gridId);
        if (CollectionUtils.isEmpty(creaturesByGrid)) {
            return null;
        }
        return creaturesByGrid.stream().filter(creature -> creature.getObjectType() == ObjectType.MWN).map(creature -> RelationshipUtils.toMWNCreature((AbstractCreature) creature)).collect(Collectors.toList());
    }

    public List<AbstractCreature> getCreaturesAroundGrid(int centerGridId, boolean includeSelf) {
        List<AbstractCreature> creatures = new ArrayList<>();
        List<MapGrid> aroundGrid = GameUtil.findAroundGrid(this.parent, centerGridId, includeSelf);
        worldMapObjects.values().stream().filter(object -> object instanceof AbstractCreature && aroundGrid.contains(parent.getMapGrid(object.getGridId()))).forEach(object -> {
            creatures.add((AbstractCreature) object);
        });
        return creatures;
    }

    public List<MWNCreature> getMWNAroundGrid(int centerGridId, boolean includeCenter) {
        List<AbstractCreature> creaturesAroundGrid = getCreaturesAroundGrid(centerGridId, includeCenter);
        if (CollectionUtils.isEmpty(creaturesAroundGrid)) {
            return Collections.EMPTY_LIST;
        }
        List<MWNCreature> mwn = new ArrayList<>();
        creaturesAroundGrid.stream().filter(creature -> creature instanceof MWNCreature).forEach(creature -> {
            mwn.add((MWNCreature) creature);
        });
        return mwn;
    }

    public AbstractCreature getCreatureById(long objectId) {
        if (!worldMapObjects.containsKey(objectId)) {
            return null;
        }
        AbstractVisibleObject visibleObject = worldMapObjects.get(objectId);
        if (visibleObject instanceof AbstractCreature) {
            return (AbstractCreature) visibleObject;
        }
        return null;
    }

    public Collection<AbstractVisibleObject> findAbstractVisibleObject(Predicate<AbstractVisibleObject> predicate) {
        return worldMapObjects.values().stream().
                filter(p -> predicate.test(p)).
                collect(Collectors.toList());
    }

    public Collection<MWNCreature> findMWN() {
        return worldMapObjects.values().stream().filter(RelationshipUtils::isMWN).map(RelationshipUtils::toMWNCreature).collect(Collectors.toList());
    }

    public AbstractBuilding findBuildByGid(int gridId) {
        List<AbstractVisibleObject> creaturesByGrid = getCreaturesByGrid(gridId);
        if (CollectionUtils.isEmpty(creaturesByGrid)) {
            return null;
        }
        return creaturesByGrid.stream().filter(object -> GameUtil.isInArray(object.getObjectType(), ObjectType.getBuildType())).findFirst().map(object -> (AbstractBuilding) object).orElse(null);
    }

    public void addPosition(WorldPosition position) {
        positions.putIfAbsent(position.getGridId(), position);
    }

    public boolean hasPosition(int gridId) {
        return positions.containsKey(gridId);
    }

    public WorldPosition getPosition(int gridId) {
        return positions.get(gridId);
    }

    public Collection<AbstractVisibleObject> findObjects(int gridId) {
        return getCreaturesByGrid(gridId);
    }

    public Collection<FireCreature> findFireCreature() {
        return worldMapObjects.values().stream().filter(object -> object.getObjectType() == ObjectType.FIRE).map(object -> (FireCreature) object).collect(Collectors.toList());
    }

    public WorldPosition getBirthPosition() {
        return birthPosition;
    }

    public void setBirthPosition(WorldPosition birthPosition) {
        this.birthPosition = birthPosition;
    }

    /***
     * 地图心跳
     */
    public void heartBeat(Room room) {
        if (this.isDestroy()) {
            return;
        }
        //恶魔火有回合限制生命检查,燃烧伤害
        Collection<FireCreature> fireCreatures = findFireCreature();
        if (!CollectionUtils.isEmpty(fireCreatures)) {
            fireCreatures.forEach(creature -> {
                try {
                    MWNCreature mwn = getMWNByGridId(creature.getGridId());
                    if (!creature.hasEffected(room.getRound())) {
                        creature.setEffectRound(room.getRound());
                        if (RelationshipUtils.judgeRelationship(mwn, creature.getOwner(), RelationshipUtils.Relationships.ENEMY_MWN_MWN)) {
                            mwn.getLifeStats().reduceHp(creature.getDamage(), creature, 0, creature.getEffectId());
                        }
                    }
                    if (creature.getLifeEndRound() >= room.getRound()) {
                        WorldMapService.getInstance().removeObject(creature);
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            });
        }
        /** 处理当前对象buff*/

        //检查地图魔物娘死亡时间过期移除
        Collection<MWNCreature> mwnCreatures = findMWN();
        if (!CollectionUtils.isEmpty(mwnCreatures)) {
            mwnCreatures.stream().filter(mwnCreature -> mwnCreature.isAlreadyDead() && !mwnCreature.isRemoved() && GameUtil.elapsedTimeByNow(mwnCreature.getDeadTime()) > MAX_DEAD_REMOVE_TIME).forEach(
                    mwnCreature -> {
                        try {
                            mwnCreature.toRemove();
                            mwnCreature.getController().delete();
                        } catch (Exception e) {
                            logger.error("", e);
                        }
                    }
            );
        }
    }

    /**
     * 方便快速回收
     */
    public void onDestroy() {
        this.setDestroy(true);
        if (!CollectionUtils.isEmpty(this.worldMapObjects)) {
            this.worldMapObjects.clear();
        }
        this.trainers.clear();
        this.positions.clear();
        this.gridCreatures.clear();
    }
}
