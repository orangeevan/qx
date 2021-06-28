package com.mmorpg.qx.module.object.creater;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.object.controllers.NpcController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.CurseCreature;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

/**
 * 诅咒
 *
 * @author: yuanchengyan
 * @since 20:04 2020-09-04
 */
public class CurseCreater {

    public static CurseCreature create(AbstractCreature caster, int hurt, ObjectResource resource, WorldMapInstance instance, Effect effect, WorldPosition pos) {
        WorldPosition worldPosition = WorldMapService.getInstance().createWorldPosition(instance, pos.getGridId());
        CurseCreature creatures = new CurseCreature(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MONSTER),  hurt, new NpcController(), worldPosition, effect);
        instance.addObject(creatures);
        creatures.getSkillController().addSkill(resource.getSkill());
        creatures.setOwner(caster);
        return creatures;
    }

}
