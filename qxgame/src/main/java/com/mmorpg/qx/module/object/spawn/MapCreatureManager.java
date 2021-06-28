package com.mmorpg.qx.module.object.spawn;

import com.alibaba.fastjson.JSON;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.building.manager.BuildingResourceManager;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.creater.AbstractBuildingCreater;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.manager.ObjectManager;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.worldMap.manager.MapResourceManager;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.resource.MapResource;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 出生引擎，这个引擎用来管理所有的非玩家生物的出生
 *
 * @author wang ke
 * @since v1.0 2020年7月31日
 */
@Component
public class MapCreatureManager implements ApplicationContextAware {
    private static final Logger log = SysLoggerFactory.getLogger(MapCreatureManager.class);

    private Map<ObjectType, Integer> objectMap = new HashMap<ObjectType, Integer>();

    @Autowired
    private ObjectManager objectManager;

    /**
     * 怪物出生配置表
     */
    private List<MapCreatureResource> spawnGroupResourceStorage = new ArrayList<>();

    @Static
    private Storage<Integer, MapResource> mapResources;

    private ApplicationContext applicationContext;

    private static MapCreatureManager self;

    public static MapCreatureManager getInstance() {
        return self;
    }

    @Autowired
    private MapResourceManager mapResourceManager;

    @Autowired
    private ServerConfigValue serverConfigValue;

    @PostConstruct
    protected void init() throws IOException {
        // 解析怪物出生json配置表
        createSpawnResource();
        self = this;
    }

    /**
     * 初始化地图默认生物
     * @throws IOException
     */
    private void createSpawnResource() throws IOException {
        for (MapResource mapResource : mapResources.getAll()) {
            String fileName = serverConfigValue.getSpawnResourcePath() + "creature_data_" + mapResource.getMapId() + ".json";
            Resource resource = applicationContext.getResource(fileName);
            if (resource.isReadable()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream inputStream = resource.getInputStream();
                try {
                    byte[] buf = new byte[inputStream.available()];
                    int hasRead = 0;
                    while (true) {
                        hasRead = inputStream.read(buf);
                        if (hasRead == -1) {
                            break;
                        }
                        baos.write(buf, 0, hasRead);
                    }
                    String msg = baos.toString();
                    List<MapCreatureResource> spawnGroupResources = JSON.parseArray(msg, MapCreatureResource.class);
                    if (CollectionUtils.isEmpty(spawnGroupResources)) {
                        continue;
                    }
                    for (MapCreatureResource s : spawnGroupResources) {
                        s.setMapId(mapResource.getMapId());
                    }
                    spawnGroupResourceStorage.addAll(spawnGroupResources);
                } catch (Exception e) {
                    throw e;
                } finally {
                    baos.close();
                    inputStream.close();
                }

            }
        }
    }

    public AbstractVisibleObject creatObject(int spawnKey, WorldMapInstance instance, Map<String, Object> args) {
        MapCreatureResource spawn = getSpawn(spawnKey);
        AbstractVisibleObject object = objectManager.createObject(spawn, instance, args);

        if (object != null) {
            object.setSpawnKey(spawn.getKey());
        }

        return object;
    }

    public AbstractVisibleObject spawnObject(MapCreatureResource spawn, WorldMapInstance instance, Map<String, Object> args) {
        AbstractVisibleObject object = null;
        if (GameUtil.isInArray(spawn.getType(), ObjectType.getBuildType())) {
            BuildingResource resource = BuildingResourceManager.getInstance().getResource(spawn.getObjectKey());
            object = AbstractBuildingCreater.getCreater(spawn.getType()).create(spawn, resource, instance, args);
        } else {
            object = objectManager.createObject(spawn, instance, args);
        }
        if (object != null) {
            object.setSpawnKey(spawn.getKey());
            bringIntoWorld(object, instance);
            ObjectType ot = object.getObjectType();
            if (!objectMap.containsKey(ot)) {
                objectMap.put(ot, 0);
            }
            objectMap.put(ot, objectMap.get(ot) + 1);
        }
        return object;
    }

    public void bringIntoWorld(AbstractVisibleObject visibleObject, WorldMapInstance instance) {
        instance.addObject(visibleObject);
    }

    /**
     * 生成所有该场景上面所有的NPC
     *
     * @param instance
     */
    public void spawnAllObject(WorldMapInstance instance) {
        // 这里根据地图id获取对应的生物资源列表
        for (MapCreatureResource resource : spawnGroupResourceStorage) {
            if (resource.getMapId() == instance.getParent().getMapId()) {
                if (!resource.isNoAutoSpawn()) {
                    spawnObject(resource, instance, null);
                }
            }
        }
    }

    public MapCreatureResource getSpawn(int key) {
        for (MapCreatureResource sgr : spawnGroupResourceStorage) {
            if (sgr.getKey() == key) {
                return sgr;
            }
        }
        return null;
    }

    public void spawnDropObject(AbstractCreature lastAttacker, Player mostDamagePlayer, AbstractCreature monster,
                                Collection<Player> owners) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
