package com.mmorpg.qx.module.worldMap.service;

import com.haipaite.common.utility.SelectRandom;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.spawn.MapCreatureManager;
import com.mmorpg.qx.module.roundFight.packet.RemoveFromMapResp;
import com.mmorpg.qx.module.roundFight.packet.UpdatePositionResp;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.manager.MapResourceManager;
import com.mmorpg.qx.module.worldMap.model.WorldMap;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 地图场景相关
 * @since 20:07 2020-07-30
 */
@Component
public class WorldMapService {
    private static final Logger log = SysLoggerFactory.getLogger(WorldMapService.class);

    private static WorldMapService instance;

    @Autowired
    private MapResourceManager mapResourceManager;

    @PostConstruct
    public void init() throws Exception {
        instance = this;
    }

    public static WorldMapService getInstance() {
        return instance;
    }

    /**
     * 创建地图实例并添加到缓存
     *
     * @param worldMap
     * @return
     */
    public WorldMapInstance createWorldMapInstance(WorldMap worldMap, boolean genMonster) {
        WorldMapInstance worldMapInstance = new WorldMapInstance(worldMap, worldMap.getNextInstanceId());
        //判断是否添加怪物
        if (genMonster) {
            MapCreatureManager.getInstance().spawnAllObject(worldMapInstance);
        }
        worldMap.addInstance(worldMapInstance.getInstanceId(), worldMapInstance);
        return worldMapInstance;
    }

    /**
     * 移除地图实例
     *
     * @param instance
     */
    public void removeWorldMapInstance(WorldMapInstance instance) {
        mapResourceManager.removeMapInstance(instance.getParent().getMapId(), instance.getInstanceId());
    }

    /**
     * 更新对象位置
     *
     * @param object
     * @param newPosition
     * @param dir
     * @param updateKnownList
     */
    public boolean updatePosition(AbstractCreature object, WorldPosition newPosition, DirType dir, boolean updateKnownList) {
        //判断对象是否出生地图上
//        if (!object.isSpawned()) {
//            return false;
//        }
        //判断对象能否移动
        if (!object.canPerformMove()) {
            return false;
        }
        //newPosition.setIsSpawned(true);
        object.setPosition(newPosition);
        //设置方向
        object.setDir(dir);
        if (updateKnownList) {
            //广播视野，由于棋盘游戏视野特殊，服务端全广播
            UpdatePositionResp resp = UpdatePositionResp.valueOf(object.getObjectId(), newPosition.getMapId(), newPosition.getGridId());
            broadcastInWorldMap(getWorldMapInstance(newPosition.getMapId(), newPosition.getInstanceId()), resp, null);
        }
        return true;
    }

    public void broadcastInWorldMap(WorldMapInstance worldMapInstance, final Object packet, Collection<AbstractVisibleObject> filters) {
        Collection<AbstractTrainerCreature> players = worldMapInstance.getTrainers();
        if (CollectionUtils.isEmpty(players)) {
            return;
        }
        players = players.stream().filter(RelationshipUtils::isPlayerTrainer).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(filters)) {
            players = players.stream().filter(player -> !filters.contains(player)).collect(Collectors.toList());
        }
        //给所有场景玩家发送消息
        if (!CollectionUtils.isEmpty(players)) {
            players.stream().forEach(player -> PacketSendUtility.sendPacket(player, packet));
        }
    }

    public WorldMapInstance getWorldMapInstance(int mapId, int instanceId) {
        WorldMap worldMap = mapResourceManager.getWorldMap(mapId);
        if (worldMap == null) {
            return null;
        }
        return worldMap.getWorldMapInstanceById(instanceId);
    }

    /**
     * 地图格子上移除对象
     */
    public void removeObject(AbstractVisibleObject creature) {
        //地图是否能对上
        if (Objects.isNull(creature.getPosition()) || creature.getInstanceId() == 0) {
            return;
        }
        WorldMapInstance mapInstanceById = mapResourceManager.getWorldMap(creature.getPosition().getMapId()).getWorldMapInstanceById(creature.getInstanceId());
        if (mapInstanceById == null) {
            return;
        }
        if (mapInstanceById.findObject(creature.getObjectId()) == null) {
            return;
        }
        //将生物从地图上移除
        mapInstanceById.removeObject(creature);
        System.err.println(String.format("生物 【%s】 从格子 【%s】 移除", creature.getObjectId() + "|" + creature.getName(), creature.getGridId()));
        creature.setPosition(null);
        //做视野广播
        broadcastInWorldMap(mapInstanceById, RemoveFromMapResp.valueOf(creature.getObjectId()), null);
    }

    /***
     *  对象进入地图,视野同步
     * @param object
     */
    public void spawn(AbstractVisibleObject object) {
        // 只有在角色没有处于出生状态下，才可以进行这个操作
//        if (object.getPosition().isSpawned()) {
//            return;
//        }
        MapCreatureManager.getInstance().bringIntoWorld(object, object.getWorldMapInstance());
        //object.getPosition().setIsSpawned(true);
        //object.updateKnownlist();
        object.getController().onSpawn(object.getMapId(), object.getInstanceId());
    }

    /***
     * 对象移除
     * @param object
     */
    public void despawn(AbstractVisibleObject object) {
        // 只有在角色处于出生状态下的时候，才可以进行重生调用
        if (object.getPosition() == null) {
            return;
        }
//		if (!object.getPosition().isSpawned()) {
//			return;
//		}
        //object.clearKnownlist();

        object.getController().onDespawn();
        //object.getPosition().setIsSpawned(false);
    }

    public WorldPosition createWorldPosition(WorldMapInstance worldMapInstance, int gridId) {
        WorldPosition worldPosition = worldMapInstance.getPosition(gridId);
        if (Objects.nonNull(worldPosition)) {
            return worldPosition;
        }
        worldPosition = WorldPosition.buildWorldPosition(worldMapInstance, gridId);
        worldMapInstance.addPosition(worldPosition);
        return worldPosition;
    }

    public WorldPosition randomEmptyGrid(WorldMapInstance worldMapInstance) {
        WorldMap worldMap = worldMapInstance.getParent();
        Collection<Integer> gridIds = worldMap.getMapGrids().keySet().stream().collect(Collectors.toList());
        Iterator<AbstractVisibleObject> objectIterator = worldMapInstance.objectIterator();
        while (objectIterator.hasNext()) {
            gridIds.remove(objectIterator.next().getGridId());
        }
        if (CollectionUtils.isEmpty(gridIds)) {
            return null;
        }
        SelectRandom<Integer> selectRandom = new SelectRandom<>();
        gridIds.stream().forEach(gridId -> selectRandom.addElement(gridId, 1));
        Integer randomGridId = selectRandom.run();
        WorldPosition position = worldMapInstance.getPosition(randomGridId);
        if (Objects.isNull(position)) {
            position = createWorldPosition(worldMapInstance, randomGridId);
            worldMapInstance.addPosition(position);
        }
        return position;
    }

    /***
     * 生物死亡，整个地图受影响
     * @param die
     */
    public void handleDie(AbstractCreature killer, AbstractCreature die) {
        if (Objects.isNull(killer) || Objects.isNull(die)) {
            return;
        }
        WorldMapInstance mapInstance = killer.getWorldMapInstance();
        if (Objects.isNull(mapInstance)) {
            return;
        }
        Collection<AbstractTrainerCreature> trainers = mapInstance.getTrainers();
        if (CollectionUtils.isEmpty(trainers)) {
            return;
        }
        trainers.stream().forEach(trainer -> {
            trainer.getEffectController().handleEffectorAfterDie(die);
            Collection<MWNCreature> aliveMwns = trainer.getMWN(true);
            if (!CollectionUtils.isEmpty(aliveMwns)) {
                aliveMwns.stream().forEach(mwnCreature -> mwnCreature.getEffectController().handleEffectorAfterDie(die));
            }
        });
    }
}
