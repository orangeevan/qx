package com.mmorpg.qx.module.object.creater;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.object.controllers.NpcController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.FireCreature;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

import java.util.List;

/**
 * @author wang ke
 * @description: 恶魔火
 * @since 20:04 2020-09-04
 */
public class FireCreater {

    public static FireCreature[] create(AbstractCreature caster, ObjectResource resource, WorldMapInstance instance, int num, Effect effect) {
        FireCreature[] creatures = new FireCreature[num];
        WorldPosition[] positions = new WorldPosition[num];
        List<MapGrid> aroundGrid = GameUtil.findAroundGrid(instance.getParent(), caster.getGridId(), true);
        for (int i = 0; i < aroundGrid.size(); i++) {
            if (i < num) {
                WorldPosition worldPosition = WorldMapService.getInstance().createWorldPosition(instance, aroundGrid.get(i).getId());
                positions[i] = worldPosition;
            }
        }
        caster.getPosition();
        for (int i = 0; i < num; i++) {
            FireCreature fireCreature = new FireCreature(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MONSTER), new NpcController(), positions[i], effect);
            instance.addObject(fireCreature);
            fireCreature.getSkillController().addSkill(resource.getSkill());
            fireCreature.setOwner(caster);
            creatures[i] = fireCreature;
        }
        return creatures;
    }

}
