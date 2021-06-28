package com.mmorpg.qx.module.gm.service;


import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.common.session.SessionUtils;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.account.model.KickReason;
import com.mmorpg.qx.module.gm.anno.GmCommand;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.SkillBuildCdResp;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangke
 * @since v1.0 2019/4/2
 */
@Component
public class GmService {

    private Logger logger = SysLoggerFactory.getLogger(GmService.class);

    /**
     * 停服前，先把所有用户先踢下线
     */
    public void kickOffAll(Wsession wsession, Player player) {
        logger.info("GM[{}]已发出指令，将所有用户踢下线", player.getPlayerEnt().getAccount());
        kickOffAll(player);
    }

    @GmCommand(mode = "kickOffAll", pattern = "kickOffAll")
    public void kickOffAll(Player player) {
        ConcurrentHashMap<Wsession, Player> sessionToPlayer = PlayerManager.getInstance().getSessionToPlayer();
        sessionToPlayer.forEach((w, p) -> {
            //GM不能踢下线
            if (SessionUtils.hasServerOpPri(w)) {
                //GM不能踢下线，数据保存下，很可能接着会关服
                PlayerManager.getInstance().logoutWriteBack(p);
                return;
            }
            SessionUtils.kickPlayerAndClose(w, KickReason.GM_KICKOFF);
        });
    }

    @GmCommand(mode = "addEffect 对象ID 效果配置表ID", pattern = "^addEffect\\s+\\d+\\s+\\d+$")
    public void addEffect(Player player, Object[] params) {
        long creatureId = Long.valueOf((String) params[0]);
        int effectId = Integer.valueOf((String) params[1]);
        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        AbstractCreature target = trainerCreature.getWorldMapInstance().getCreatureById(creatureId);
        if (Objects.isNull(target)) {
            return;
        }
        Effect effect = new Effect(trainerCreature, target, SkillManager.getInstance().getEffectResource(effectId).getEffectType().create(), 0, effectId, Target.valueOf(target.getGridId(), target.getObjectId()));
        target.getEffectController().addEffect(effect);
    }

    @GmCommand(mode = "addEffectMvn 魔物娘配置表ID 效果配置表ID", pattern = "^addEffectMvn\\s+\\d+\\s+\\d+$")
    public void addEffectMvn(Player player, Object[] params) {
        int mvnid = Integer.valueOf((String) params[0]);
        int effectId = Integer.valueOf((String) params[1]);

        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        Collection<MWNCreature> mwns = trainerCreature.getMWN(true);
        for (MWNCreature mwn : mwns) {
            if (mwn.getResource().getId() != mvnid) {
                continue;
            }
            Effect effect = new Effect(trainerCreature, mwn, SkillManager.getInstance().getEffectResource(effectId).getEffectType().create(), 0, effectId, Target.valueOf(mwn.getGridId(), mwn.getObjectId()));
            mwn.getEffectController().addEffect(effect);
        }
    }

    @GmCommand(mode = "removeEffectMvn 魔物娘配置表ID 效果配置表ID", pattern = "^removeEffectMvn\\s+\\d+\\s+\\d+$")
    public void removeEffectMvn(Player player, Object[] params) {
        int mvnid = Integer.valueOf((String) params[0]);
        int effectId = Integer.valueOf((String) params[1]);

        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        Collection<MWNCreature> mwns = trainerCreature.getMWN(true);
        for (MWNCreature mwn : mwns) {
            if (mwn.getResource().getId() != mvnid) {
                continue;
            }
            mwn.getEffectController().removeEffect(effectId);
        }
    }

    @GmCommand(mode = "addEffectTrain 效果配置表ID", pattern = "^addEffectMvn\\s+\\d+$")
    public void addEffectTrain(Player player, Object[] params) {
        int effectId = Integer.valueOf((String) params[1]);
        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();

        Effect effect = new Effect(trainerCreature, trainerCreature, SkillManager.getInstance().getEffectResource(effectId).getEffectType().create(), 0, effectId, Target.valueOf(trainerCreature.getGridId(), trainerCreature.getObjectId()));
        trainerCreature.getEffectController().addEffect(effect);

    }

    @GmCommand(mode = "removeEffectTrain 效果配置表ID", pattern = "^removeEffectMvn\\s+\\d+$")
    public void removeEffectTrain(Player player, Object[] params) {
        int effectId = Integer.valueOf((String) params[1]);
        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        trainerCreature.getEffectController().removeEffect(effectId);

    }

    @GmCommand(mode = "addSkill 对象ID 技能配置表ID", pattern = "^addSkill[ ]*[0-9]\\d*[ ]*[0-9]\\d*$")
    public void addSkill(Player player, Object[] params) {
        long creatureId = Long.parseLong((String) params[0]);
        int skillId = Integer.parseInt((String) params[1]);
        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        AbstractCreature target = trainerCreature.getWorldMapInstance().getCreatureById(creatureId);
        if (Objects.isNull(target)) {
            return;
        }
        target.getSkillController().addSkill(skillId);
        PacketSendUtility.sendPacket(player, SkillBuildCdResp.valueOf(target));
    }

    @GmCommand(mode = "useSkill 施法者ID 对象ID 技能配置表ID", pattern = "^useSkill[ ]*[0-9]\\d*[ ]*[0-9]\\d*[ ]*[0-9]\\d*$")
    public void useSkill(Player player, Object[] params) {
        long creatureId = Long.parseLong((String) params[0]);
        long targetId = Long.parseLong((String) params[1]);
        int skillId = Integer.parseInt((String) params[2]);
        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        AbstractCreature caster = trainerCreature.getWorldMapInstance().getCreatureById(creatureId);
        AbstractCreature target = trainerCreature.getWorldMapInstance().getCreatureById(targetId);
        if (Objects.isNull(caster)) {
            return;
        }
        Skill skill = Skill.valueOf(SkillManager.getInstance().getSkillResource(skillId), caster, Target.valueOf(target.getGridId(), target.getObjectId()));
        List<AbstractSkillResult> results = caster.getSkillController().useSkill(skill);
        RoundFightUtils.sendUseSkill(caster, skill.getResource().getSkillId(), skill.getTarget() == null ? new ArrayList<>() : skill.getTarget().getTargetIds(), results, skill.getResource().isBroadcast());
        caster.getSkillController().afterUseSkill(skill);
    }

    @GmCommand(mode = "deductHp 对象ID 扣除数量", pattern = "^deductHp[ ]*[0-9]\\d*[ ]*[0-9]\\d*$")
    public void deductHp(Player player, Object[] params) {
        long targetId = Long.parseLong((String) params[0]);
        int value = Integer.parseInt((String) params[1]);
        PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
        AbstractCreature target = trainerCreature.getWorldMapInstance().getCreatureById(targetId);
        target.getMaster().getLifeStats().reduceHp(value, trainerCreature, null, true);
    }
}
