package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.packet.BuildingUseSkillReq;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.skill.service.SkillService;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 操作建筑
 * @since 19:32 2020-09-02
 */
public class OperateBuildStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Operate_Building;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        if (owner.isAlreadyDead()) {
            return;
        }
        if (owner.getRoom() == null || owner.getRoom().getCurrentTurn() != owner || !owner.getRoom().isInStage(RoundStage.MOVE)) {
            return;
        }
        //模拟玩家驯养师操作建筑
        WorldPosition position = owner.getPosition();
        WorldMapInstance worldMapInstance = owner.getWorldMapInstance();
        AbstractBuilding buildByGid = worldMapInstance.findBuildByGid(position.getGridId());
        if (Objects.isNull(buildByGid) || CollectionUtils.isEmpty(buildByGid.getSkills())) {
            return;
        }
        //默认建筑第一个技能
        Integer skillId = buildByGid.getSkills().get(0);
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        Target target = TargetManager.getInstance().chooseTarget(skillResource.getTargets().keySet().stream().collect(Collectors.toList()), owner);
        if (Objects.isNull(target)) {
            return;
        }
        BuildingUseSkillReq skillReq = new BuildingUseSkillReq();
        skillReq.setBuildingId(buildByGid.getObjectId());
        skillReq.setTarget(target);
        try {
            BeanService.getBean(SkillService.class).buildingUseSkill(owner, skillReq);
        } catch (ManagedException e) {

        }
    }
}
