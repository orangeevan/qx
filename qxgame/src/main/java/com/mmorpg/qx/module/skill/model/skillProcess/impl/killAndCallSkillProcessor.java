package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.creater.MWNCreater;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wang ke
 * @description:伤害技能，击杀目标后召唤特定魔物娘
 * @since 19:14 2020-09-07
 */
@Component
public class killAndCallSkillProcessor extends HarmSkillProcessor {

    @Override
    public Object initParam(SkillResource resource) {
        String mwnResourceId = resource.getParam();
        return Integer.valueOf(mwnResourceId);
    }

    @Override
    public SkillType getSkillType() {
        return SkillType.Kill_And_Call;
    }

    @Override
    public List<DamageSkillResult> process(Skill skill, int roundIndex) {
        List<DamageSkillResult> process = super.process(skill, roundIndex);
        List<AbstractCreature> denfendersList = skill.getDenfendersList();
        if (!CollectionUtils.isEmpty(denfendersList)) {
            AbstractCreature creature = denfendersList.get(0);
            //默认只有一个目标，目标死亡后触发召唤
            if (creature.isAlreadyDead()) {
                AbstractCreature caster = skill.getSkillCaster();
                WorldPosition position = WorldMapService.getInstance().randomEmptyGrid(caster.getWorldMapInstance());
                if (position != null) {
                    int mwnResourceId = (int) initParam(skill.getResource());
                    MWNResource mwnById = MWNManager.getInstance().getMWNResource(mwnResourceId);
                    MWNCreature mwn = MWNCreater.create(caster.getMaster(), mwnById, position);
                    mwn.setMaster(caster.getMaster());
                    caster.getMaster().addMWN(mwn);
                    WorldMapService.getInstance().spawn(mwn);
                }
            }
        }
        return process;
    }
}
