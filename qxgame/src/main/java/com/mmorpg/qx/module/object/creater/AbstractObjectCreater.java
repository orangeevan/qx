package com.mmorpg.qx.module.object.creater;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图对象生成器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public abstract class AbstractObjectCreater {

    private static final Map<ObjectType, AbstractObjectCreater> CREATER_MAP = new HashMap<ObjectType, AbstractObjectCreater>();

    @Autowired
    protected IdentifyManager identifyManager;

    @PostConstruct
    protected void init() {
        CREATER_MAP.put(getObjectType(), this);
    }

    /**
     * 生成方法
     *
     * @param sresource
     * @param resource
     * @param instance
     * @param args
     * @return
     */
    public abstract AbstractVisibleObject create(MapCreatureResource sresource, ObjectResource resource, WorldMapInstance instance, Map<String, Object> args);

    public void relive(ObjectResource resource, AbstractVisibleObject object, Map<String, Object> args) {

    }

    protected void setAttr(AbstractCreature creature, ObjectResource resource) {
        creature.getAttrController().clear();
        List<Attr> attrs = new ArrayList<>(resource.getAttrsList());
        for (Attr attr : resource.getAttrsList()) {
            attrs.add(attr.clone());
        }
        if (!attrs.isEmpty()) {
            creature.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Level_Base), attrs);
        } else {
            throw new RuntimeException(String.format("ObjectResource id[%s]属性为空", resource.getKey()));
        }
    }

    public abstract ObjectType getObjectType();

    public static AbstractObjectCreater getCreater(ObjectType type) {
        return CREATER_MAP.get(type);
    }
}
