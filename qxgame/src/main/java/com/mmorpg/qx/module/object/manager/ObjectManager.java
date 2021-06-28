package com.mmorpg.qx.module.object.manager;

import com.alibaba.fastjson.JSON;
import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.creater.AbstractObjectCreater;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public final class ObjectManager implements ResourceReload {

    private static final Logger logger = SysLoggerFactory.getLogger(ObjectManager.class);

    @Static
    public Storage<Integer, ObjectResource> objectResourceStorage;

    public static ObjectManager getInstance() {
        return BeanService.getBean(ObjectManager.class);
    }

    @PostConstruct
    protected void init() {
        initResource();
    }

    private void initResource() {
        Collection<ObjectResource> all = objectResourceStorage.getAll();
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        all.stream().forEach(objectResource -> {
            String attrs = objectResource.getAttrs();
            if (!StringUtils.isBlank(attrs)) {
                List<Attr> attrList = JSON.parseArray(attrs, Attr.class);
                objectResource.setAttrsList(attrList);
            }
        });
    }

    public ObjectResource getObjectResource(int key) {
        return objectResourceStorage.get(key, true);
    }

    /**
     * 根据key创建一个可见生物
     *
     * @param sresource
     * @return
     */
    public AbstractVisibleObject createObject(MapCreatureResource sresource, WorldMapInstance instance, Map<String, Object> args) {
        ObjectResource resource = objectResourceStorage.get(sresource.getObjectKey(), true);
        AbstractObjectCreater creater = AbstractObjectCreater.getCreater(resource.getObjectType());
        if (creater == null) {
            logger.error(String.format("no any creater found for object type : [%s] exception", resource.getObjectType()));
            throw new RuntimeException("no any creater found for object type : [%s] " + resource.getObjectType());
        }
        return creater.create(sresource, resource, instance, args);
    }

    public AbstractVisibleObject createObject(ObjectResource resource, WorldMapInstance instance, Map<String, Object> args) {
        AbstractObjectCreater creater = AbstractObjectCreater.getCreater(resource.getObjectType());
        if (creater == null) {
            throw new RuntimeException("no any creater found for object type : [%s] " + resource.getObjectType());
        }
        return creater.create(null, resource, instance, args);
    }

    public void reliveObject(AbstractVisibleObject object, Map<String, Object> args) {
        ObjectResource resource = objectResourceStorage.get(object.getObjectKey(), true);
        AbstractObjectCreater.getCreater(resource.getObjectType()).relive(resource, object, args);
    }

    @Override
    public void reload() {
        initResource();
    }

    @Override
    public Class<?> getResourceClass() {
        return ObjectResource.class;
    }
}
