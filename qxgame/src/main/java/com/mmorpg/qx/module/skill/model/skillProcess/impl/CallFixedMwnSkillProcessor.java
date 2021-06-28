package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.creater.MWNCreater;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DisplaySkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 召唤指定魔物娘 魔物娘技能
 * 召唤出的魔物娘为特殊配置的魔物娘，其等级、阶数、潜能星数与释放技能的魔物娘一致，基础攻击力与生命值则根据配置设置
 * 根据等级、阶数、潜能星数更新基础攻击力与生命值
 * @since 19:40 2020-09-28
 */
@Component
public class CallFixedMwnSkillProcessor extends AbstractSkillProcessor<DisplaySkillResult, Object> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Call_Fixed_Mwn;
    }

    @Override
    public List<DisplaySkillResult> process(Skill skill, int roundIndex) {
        List<DisplaySkillResult> results = new ArrayList<>();
        int mwnResourceId = Integer.parseInt(skill.getResource().getParam());
        MWNResource mwnResource = MWNManager.getInstance().getMWNResource(mwnResourceId);
        MWNCreature casterMwn = RelationshipUtils.toMWNCreature(skill.getSkillCaster());
        if (casterMwn.getMaster().getLifeStats().getCurrentMp() < mwnResource.getCostMp()) {
            throw new ManagedException(ManagedErrorCode.CALL_MWN_LIMIT_MP);
        }
        casterMwn.getMaster().getLifeStats().reduceMp(mwnResource.getCostMp(), Reason.Call_Mwn, true, true);
        MoWuNiang moWuNiang = MWNService.getInstance().valueOf(mwnResourceId);
        // 设置新召唤魔物娘的等级、阶数、潜能星数
        moWuNiang.setLevel(casterMwn.getMwn().getLevel());
        moWuNiang.setCasteLv(casterMwn.getMwn().getCasteLv());
        moWuNiang.setStarLv(casterMwn.getMwn().getStarLv());
        // 更新魔物娘攻击力和最大生命
        MWNService.getInstance().updateHpOrAttack(moWuNiang, AttrType.Max_Hp);
        MWNService.getInstance().updateHpOrAttack(moWuNiang, AttrType.Attack);
        // 召唤魔物娘
        int gridId = skill.getTarget().getGridId();
        WorldPosition position = WorldMapService.getInstance().createWorldPosition(casterMwn.getWorldMapInstance(), gridId);
        MWNCreature mwnCreature = MWNCreater.create(casterMwn.getMaster(), moWuNiang, position);
        WorldMapService.getInstance().spawn(mwnCreature);
        // 设置魔物娘的驯养师
        mwnCreature.setMaster(casterMwn.getMaster());
        casterMwn.getMaster().addMWN(mwnCreature);

        String format = String.format("召唤指定魔物娘技能, 技能ID:%s, mwnResourceId:%s", skill.getResource().getSkillId(), mwnResourceId);
        System.err.println(format);

        results.add(DisplaySkillResult.valueOf(mwnCreature.getObjectId(), skill));
        return results;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
