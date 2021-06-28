package com.mmorpg.qx.module.worldMap.manager;

import com.alibaba.fastjson.JSON;
import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.New;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.worldMap.model.WorldMap;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author wang ke
 * @description:地图管理类
 * @since 19:57 2020-07-30
 */
@Component
public final class MapResourceManager implements ResourceReload, ApplicationContextAware {


    private static final Logger logger = SysLoggerFactory.getLogger(MapResourceManager.class);

    @Static
    private Storage<Integer, MapResource> mapResources;

    /**
     * 所有地图数据 key: id
     */
    private final Map<Integer, WorldMap> worldMaps = new ConcurrentHashMap<>();

    @Autowired
    private ServerConfigValue serverConfigValue;

    @PostConstruct
    public void init() throws Exception {
        initResource();
    }

    private void initResource() throws Exception {
        for (MapResource resource : mapResources.getAll()) {
            WorldMap map = createWorldMap(resource);
            worldMaps.put(resource.getMapId(), map);
        }
    }


    public AbstractVisibleObject findObject(int mapId, int instanceId, long objId) {
        WorldMap worldMap = getWorldMap(mapId);
        if (worldMap != null) {
            WorldMapInstance instance = worldMap.getWorldMapInstanceById(instanceId);
            if (instance != null) {
                return instance.findObject(objId);
            }
        }
        return null;
    }

    public boolean isInstanceExist(int worldId, int instanceId) {
        return getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
    }

    /**
     * Return World Map by id
     *
     * @param id - id of world map.
     * @return World map.
     */
    public WorldMap getWorldMap(int id) {
        return worldMaps.get(id);
    }

    public static final MapResourceManager getInstance() {
        return BeanService.getBean(MapResourceManager.class);
    }

    public Map<Integer, WorldMap> getWorldMaps() {
        return worldMaps;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String[] getWorldMapInfo() {
        List<String> result = New.arrayList();
        for (WorldMap worldMap : worldMaps.values()) {
            Iterator<WorldMapInstance> ite = worldMap.iterator();
            while (ite.hasNext()) {
                WorldMapInstance instance = ite.next();
                result.add(instance.toString());
            }
        }
        return result.toArray(new String[result.size()]);
    }


    public MapResource getMapResource(int mapId) {
        return mapResources.get(mapId, true);
    }

    /**
     * 地图业务走完移除地图实例
     *
     * @param mapId
     * @param instanceId
     * @return
     */
    public boolean removeMapInstance(int mapId, int instanceId) {
        if (!worldMaps.containsKey(mapId)) {
            return false;
        }
        WorldMap worldMap = worldMaps.get(mapId);
        if (!worldMap.getInstances().containsKey(instanceId)) {
            return false;
        }
        worldMap.removeInstance(instanceId);
        return true;
    }

    @Override
    public void reload() {
        try {
            initResource();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    public Class<?> getResourceClass() {
        return MapResource.class;
    }

    private WorldMap createWorldMap(MapResource mapResource) throws Exception {
        WorldMap map = new WorldMap();
        map.setMapId(mapResource.getMapId());
        map.setName(mapResource.getName());
        map.setMaxNum(mapResource.getMaxPlayerNum());
        map.setBirthGrid(mapResource.getBirthGrid());
        map.setBirthDir(mapResource.getBirthDir());
        String fileName = serverConfigValue.getMapResourcePath() + mapResource.getFileName() + ".json";
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
                List<MapGrid> mapGrids = JSON.parseArray(msg, MapGrid.class);
                if (CollectionUtils.isEmpty(mapGrids)) {
                    throw new RuntimeException("地图： " + mapResource.getMapId() + " 没有配置格子数据");
                }
                Map<Integer, MapGrid> grids = new HashMap(mapGrids.size());
                mapGrids.stream().forEach(grid -> grids.put(grid.getId(), grid));
                map.setMapGrids(grids);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                baos.close();
                inputStream.close();
            }
        }
        logger.debug(String.format("map id [%s] name [%s] init finish", map.getMapId(), fileName));
        return map;
    }
}
