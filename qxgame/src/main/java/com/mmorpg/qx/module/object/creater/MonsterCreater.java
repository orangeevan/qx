package com.mmorpg.qx.module.object.creater;

import com.mmorpg.qx.common.identify.manager.IdentifyManager.IdentifyType;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.MonsterController;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.controllers.stats.CreatureLifeStats;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MonsterCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.attr.CreatureAttrController;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 怪物生成器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
@Component
public final class MonsterCreater extends AbstractObjectCreater {

    @Override
    public AbstractVisibleObject create(MapCreatureResource spawnResource, ObjectResource resource, WorldMapInstance instance, Map<String, Object> args) {
        int gridId = spawnResource.getGridId();
        DirType dir = spawnResource.createDir();
        WorldPosition worldPosition = WorldPosition.buildWorldPosition(instance, gridId);
        MonsterCreature monster = new MonsterCreature(identifyManager.getNextIdentify(IdentifyType.MONSTER), new MonsterController(), worldPosition);
        monster.setDir(dir);
        //monster.setAi(resource.getAiType().createAI());

        // 设置怪物的一些属性
        monster.setAttrController(new CreatureAttrController(monster));
        monster.setSpawnKey(spawnResource.getKey());
        monster.setObjectKey(resource.getKey());
        monster.setWarnrange(spawnResource.getWarnrange());
        //怪物不能移动
//		if (spawnResource.hasRouteStep() && spawnResource.getRouteType() != null) {
//			monster.setRouteRoad(spawnResource.getRouteType().createRoad((spawnResource.getRouteSteps())));
//			monster.setFollowPolicy(new RouteFollowPolicy(monster));
//		}
        // 设置属性
        setAttr(monster, resource);
        monster.setLifeStats(new CreatureLifeStats<MonsterCreature>(monster, monster.getAttrController().getCurrentAttr(AttrType.Max_Hp), monster.getAttrController().getCurrentAttr(AttrType.Max_Mp)));
        monster.setEffectController(new EffectController(monster));
        return monster;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.MONSTER;
    }

    @Override
    public void relive(ObjectResource resource, AbstractVisibleObject object, Map<String, Object> args) {
        MonsterCreature monster = (MonsterCreature) object;
        // 设置属性
        setAttr(monster, resource);
        monster.getLifeStats().setCurrentHpPercent(100);
        // 这里需要重置一下路线
        if (monster.hasRouteStep()) {
            monster.getRouteRoad().reset();
        }
    }

}
