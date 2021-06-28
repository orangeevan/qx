package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.UseSkillReq;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.skill.service.SkillService;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:驯养师战斗
 * @since 17:18 2020-08-18
 */
public class FightStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Fight;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        //驯养师通过释放技能参与战斗
        Collection<SkillResource> skills = SkillManager.getInstance().getSkillByReleaseType(SkillReleaseType.Trainer_Active_Skill);
        if (CollectionUtils.isEmpty(skills)) {
            return;
        }
        Optional<SkillResource> any = skills.stream().filter(skillResource -> owner.getSkillController().hasSkill(skillResource.getSkillId())).findAny();
        if (!any.isPresent()) {
            return;
        }
        SkillService skillService = BeanService.getBean(SkillService.class);
        UseSkillReq skillReq = new UseSkillReq();
        skillReq.setObjectId(owner.getObjectId());
        skillReq.setSkillId(any.get().getSkillId());
        skillReq.setTarget(Target.valueOf(owner.getGridId(), new ArrayList<>()));
        try {
            skillService.useSkill(owner, skillReq);
        } catch (ManagedException e) {

        }
    }
}
