package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.creater.FireCreater;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.FireCreature;
import com.mmorpg.qx.module.object.manager.ObjectManager;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

import java.util.Arrays;

/**
 * @author wang ke
 * @description: 恶魔火魔法, 死亡后释放魔法
 * @since 19:50 2020-09-04
 */

public class FireEffect extends AbstractEffectTemplate {
    //对象id
    private int objectId;
    //数量
    private int fireNum;

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
        objectId = Integer.valueOf(effect.getEffectResource().getParam());
        fireNum = effect.getValue();
    }

    @Override
    public boolean applyEffect(Effect effect) {
        //死亡后触发
        AbstractCreature effector = effect.getTrigger();
        if (effector == null || !effector.isAlreadyDead()) {
            return false;
        }
        ObjectResource fireResource = ObjectManager.getInstance().getObjectResource(objectId);
        WorldMapInstance mapInstance = effector.getWorldMapInstance();
        FireCreature[] fireCreatures = FireCreater.create(effector, fireResource, mapInstance, fireNum, effect);
        Arrays.stream(fireCreatures).forEach(fireCreature -> WorldMapService.getInstance().spawn(fireCreature));
        return true;
    }
}
