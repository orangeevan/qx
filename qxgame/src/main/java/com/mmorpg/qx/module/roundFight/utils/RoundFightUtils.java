package com.mmorpg.qx.module.roundFight.utils;

import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.fight.FightType;
import com.mmorpg.qx.common.fight.handler.AFightHandler;
import com.mmorpg.qx.common.fight.handler.impl.MwnFightHandler;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrRestrainUtils;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.player.entity.PlayerCommonEnt;
import com.mmorpg.qx.module.player.model.PlayerCommonType;
import com.mmorpg.qx.module.roundFight.model.DicePoint;
import com.mmorpg.qx.module.roundFight.packet.MwnSimpleFightResp;
import com.mmorpg.qx.module.roundFight.packet.RoundCardUpdateResp;
import com.mmorpg.qx.module.roundFight.packet.ThrowDiceResp;
import com.mmorpg.qx.module.roundFight.packet.TrainerUseCardNumResp;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.skill.model.FightStatus;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.effect.impl.DicePointsChangeEffect;
import com.mmorpg.qx.module.skill.model.effect.impl.ShieldEffect;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.UseSkillResp;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author wang ke
 * @description: 战斗工具类
 * @since 14:49 2020-08-06
 */
public class RoundFightUtils {
    private static final Logger logger = SysLoggerFactory.getLogger(RoundFightUtils.class);

    /***
     * 基础战斗过程，计算单次战斗攻击者伤害
     *
     *      * 判断闪避：
     *      * 	根据魔物娘概率判定是否闪避成功，闪避成功此次攻击伤害为0，未成功进入暴击判断。
     *      *
     *      * 判断暴击：
     *      * 	根据魔物娘暴击概率判断是否触发暴击，暴击则魔物娘进攻的攻击力值计算为200%。未暴击则此阶段结算伤害为攻击力的100%并进入魔法效果判断。
     *      *
     *      * 判断魔法效果：
     *      * 	在上述伤害累计后的基础上，如双方魔法与技能会带来基础伤害的提升与降低，需要进行判断，结算后进入实际伤害判断。
     *      *
     *      * 实际伤害：
     *      * 	进攻方攻击力判定暴击后伤害值+技能影响进攻方伤害-技能影响防守方伤害减免。
     *      * 	判断后对双方生命值进行结算（有护盾状态优先扣除护盾生命值），如此时有一方或双方生命值已为0，直接判断结束战斗，此时不判断吸血效果。
     *      * 	如双方生命值都大于0，继续判断吸血效果
     *      *
     *      * 判断吸血效果：
     *      * 	判断造成伤害方是否有吸血技能效果，如有，在此阶段回复实际伤害的吸血属性百分比生命值。回复量不超过生命值上限。
     * @param attacker
     * @param defender
     * @param skill
     */
    public static DamageSkillResult fight(AbstractCreature attacker, AbstractCreature defender, Skill skill) {
        DamageSkillResult damage = DamageSkillResult.valueOf(attacker.getObjectId(), defender.getObjectId(), skill, 0);
        if (defender.isAlreadyDead()) {
            return damage;
        }
        /**判断闪避*/
        int hitRate = AttrType.Hit_Rate.getAttrValue(attacker) - AttrType.Miss.getAttrValue(defender);
        if (hitRate <= 0 || !RandomUtils.isHit(hitRate)) {
            damage.setStatus(FightStatus.MISS);
            return damage;
        }
        SkillResource skillResource = (skill == null) ? null : skill.getResource();
        int harm = (skillResource == null) ? 0 : skillResource.getBasicDamage() + attacker.getAttrController().getCurrentAttr(AttrType.Attack);
        /**伤害加成*/
        if (attacker.getAttrController().hasAttr(AttrType.Harm_Add_Rate)) {
            int harmAdd = attacker.getAttrController().getCurrentAttr(AttrType.Harm_Add_Rate);
            harm = harm + Math.round(harm * GameUtil.toRatio10000(harmAdd));
        }
        //魔物娘之间克制,伤害加成50%
        if (RelationshipUtils.isMWN(attacker) && RelationshipUtils.isMWN(defender)) {
            if (AttrRestrainUtils.creatureAttrRestrain(attacker, defender)) {
                harm += harm / 2;
            }
        }
        /**判断暴击*/
        if (attacker.getAttrController().hasAttr(AttrType.Crit_Prob) || defender.getAttrController().hasAttr(AttrType.Add_Eny_Crit)) {
            int critProb = AttrType.Crit_Prob.getAttrValue(attacker) + AttrType.Add_Eny_Crit.getAttrValue(defender);
            if (RandomUtils.isHit(critProb)) {
                damage.setStatus(FightStatus.CRIT);
                harm += harm;
            }
        }

//        /**判断免伤*/
//        if (defender.getAttrController().hasAttr(AttrType.Avoid_Injury)) {
//            long avoidHarm = defender.getAttrController().getCurrentAttr(AttrType.Avoid_Injury);
//            damage.setStatus(FightStatus.AVOID_HARM);
//            damage.setAvoidHarm((int) avoidHarm);
//            if (harm <= avoidHarm) {
//                damage.setValue(0);
//                return damage;
//            }
//            harm -= avoidHarm;
//        }
        /**防御者护盾处理*/
        if (defender.getEffectController().isInStatus(EffectStatus.Shield)) {
            Effect effect = defender.getEffectController().getEffect(EffectType.Shield);
            ShieldEffect effectTemplate = (ShieldEffect) effect.getEffectTemplate();
            int left = effectTemplate.leftAbsorbDamge();
            if (harm > left) {
                harm -= left;
                effectTemplate.absorbDamage(left);
            } else {
                effectTemplate.absorbDamage(harm);
                harm = 0;
            }
            effect.addUseTime();
            if (effectTemplate.isAbsorbFull()) {
                effectTemplate.endEffect(effect, defender.getEffectController());
            }
            damage.setStatus(FightStatus.AVOID_HARM);
        }
        /** 魔法强度降低伤害*/
        if (defender.getEffectController().isInStatus(EffectStatus.Magicstren_Dec_Harm)) {
            harm -= (int) (500 * defender.getMaster().getMagicStrength());
            harm = harm < 0 ? 0 : harm;
        }
        /**判断减伤*/
        if (defender.getAttrController().hasAttr(AttrType.Harm_Reduce_Rate)) {
            int harmReduce = defender.getAttrController().getCurrentAttr(AttrType.Harm_Reduce_Rate);
            harm = harm - Math.round(harm * GameUtil.toRatio10000(harmReduce));
            harm = harm < 0 ? 0 : harm;
        }
        /**护甲*/
        if (defender.getAttrController().hasAttr(AttrType.Defend)) {
            int defend = defender.getAttrController().getCurrentAttr(AttrType.Defend);
            harm = harm - defend;
            harm = harm < 0 ? 0 : harm;
        }
        /**判断吸血*/
        if (attacker.getAttrController().hasAttr(AttrType.Suck_Blood)) {
            int suckRatio = defender.getAttrController().getCurrentAttr(AttrType.Suck_Blood);
            int suckBlood = Math.round(harm * GameUtil.toRatio10000(suckRatio));
            if (suckRatio > 0) {
                attacker.getLifeStats().increaseHp(suckBlood, Reason.Trainer_Skill_Effect, false, true);
                damage.setSuck(suckBlood);
            }
        }

        damage.setValue(harm);
        return damage;
    }

    /**
     * 新战斗逻辑
     *
     * @param attacker
     * @param defender
     */
    public static void fight(MWNCreature attacker, MWNCreature defender) {
        attacker.getMaster().setCallMwnFight(defender.getGridId());
        mwnSimpleFight(attacker, defender);
        AFightHandler handler = FightType.MWN.getHandler(() -> {
            MwnFightHandler.BoardBox boardBox = new MwnFightHandler.BoardBox();
            boardBox.setAttacker(attacker);
            boardBox.setDefender(defender);
            return boardBox;
        });
        handler.execute();
    }

//    /***
//     * 魔物娘双方多回合战斗，必有一方战死或双方均战死
//     * @param attacker
//     * @param defender
//     */
//    public static void fight(MWNCreature attacker, MWNCreature defender) {
//        /**处理魔物娘战斗*/
//        attacker.getMaster().setCallMwnFight(defender.getGridId());
//        mwnSimpleFight(attacker, defender);
//        List<AbstractSkillResult> damageList = new ArrayList<>();
//        String info = String.format("魔物娘战斗开始，进攻方魔物娘 【%s】血量【%s】， 防守方魔物娘 【%s】血量【%s】 战斗： ", attacker.getResourceId() + "|" + attacker.getObjectId() + "|" + attacker.getName(), attacker.getCurrentHp(), defender.getResourceId() + "|" + defender.getObjectId() + "|" + defender.getName(), defender.getCurrentHp());
//        System.err.println(info);
//        logger.info(info);
//        /** a的攻击目标*/
//        Target targetA = Target.valueOf(defender.getGridId(), defender.getObjectId());
//        /**b的攻击目标*/
//        Target targetB = Target.valueOf(attacker.getGridId(), attacker.getObjectId());
//        /**循环战斗，直到一方死亡,设置100次防止没有伤害技能造成循环*/
//        int roundIndex = 1;
//        Skill skillA = null;
//        Skill skillB = null;
//        do {
//            /** a挑选技能*/
//            skillA = attacker.getSkillController().selectSkill(targetA);
//            /**b挑选技能*/
//            skillB = defender.getSkillController().selectSkill(targetB);
//            if (skillA != null) {
//                List<AbstractSkillResult> damages = skillA.useSkill(roundIndex);
//                String format = String.format("魔物娘进攻方A id【%s】【%s】，使用技能：【%s】  敌方剩余血量:【%s】", attacker.getObjectId(), attacker.getName(), skillA.getResource().getSkillId(), defender.getCurrentHp());
//                System.err.println(format);
//                if (!CollectionUtils.isEmpty(damages)) {
//                    for (AbstractSkillResult result : damages) {
//                        System.err.println("添加进攻方技能战报： " + result);
//                    }
//                }
//                if (!CollectionUtils.isEmpty(damages)) {
//                    damageList.addAll(damages);
//                }
//                attacker.getSkillController().afterUseSkill(skillA);
//            }
//            if (skillB != null) {
//                List<AbstractSkillResult> damages = skillB.useSkill(roundIndex);
//                String format = String.format("魔物娘防守方B id【%s】【%s】，使用技能：【%s】  敌方剩余血量:【%s】", defender.getObjectId(), defender.getName(), skillB.getResource().getSkillId(), attacker.getCurrentHp());
//                System.err.println(format);
//                if (!CollectionUtils.isEmpty(damages)) {
//                    for (AbstractSkillResult result : damages) {
//                        System.err.println("添加防守方技能战报： " + result);
//                    }
//                }
//                if (!CollectionUtils.isEmpty(damages)) {
//                    damageList.addAll(damages);
//                }
//                defender.getSkillController().afterUseSkill(skillB);
//            }
//            roundIndex++;
//        } while (!attacker.getLifeStats().isAlreadyDead() && !defender.getLifeStats().isAlreadyDead() && roundIndex < 100);
//        int attSupporter = attacker.getSupporter() == 0 ? 0 : attacker.getMaster().getMwnCreatureByCardId(attacker.getSupporter()).getMwn().getSkinResourceId();
//        int defSupporter = defender.getSupporter() == 0 ? 0 : defender.getMaster().getMwnCreatureByCardId(defender.getSupporter()).getMwn().getSkinResourceId();
//        long winnerId = defender.isAlreadyDead() ? attacker.getObjectId() : defender.getObjectId();
//        MwnRoundFightReportResp reportResp = MwnRoundFightReportResp.valueOf(damageList, winnerId, attacker, defender, attSupporter, defSupporter);
//        PacketSendUtility.broadcastInWorldMap(attacker.getMaster().getWorldMapInstance(), reportResp, null);
//        /**战斗完后处理*/
//        attacker.afterFightRound(defender, skillB == null ? 0 : skillB.getResource().getSkillId(), 0);
//        defender.afterFightRound(attacker, skillA == null ? 0 : skillA.getResource().getSkillId(), 0);
//        System.err.println("魔物娘战斗战报： " + reportResp.getReports().size());
//        logger.info(String.format("魔物娘战斗结束，进攻方魔物娘 【%s】 血量:【%s】， 防守方魔物娘 【%s】 血量:【%s】", attacker.getResourceId() + "|" + attacker.getObjectId(), attacker.getCurrentHp(), defender.getResourceId() + "|" + defender.getObjectId(), defender.getCurrentHp()));
//        //对战后
//        AbstractCreature winner = defender.isAlreadyDead() ? attacker : defender;
//        AbstractCreature loser = defender.isAlreadyDead() ? attacker : defender;
//        EventBusManager.getInstance().syncSubmit(MwnFightAfterEvent.valueOf(winner, loser));
//    }

    public static boolean judgeMwnSimpleFight(AbstractTrainerCreature trainer) {
        if (RelationshipUtils.isPlayerTrainer(trainer)) {
            PlayerCommonEnt commonEnt = ((PlayerTrainerCreature) trainer).getOwner().getCommonEnt();
            Map<PlayerCommonType, Integer> commonData = commonEnt.getCommonData();
            if (commonData == null || !commonData.containsKey(PlayerCommonType.MWN_SIMPLE_FIGHT)) {
                return false;
            }
            if (commonData.get(PlayerCommonType.MWN_SIMPLE_FIGHT) == 0) {
                return false;
            }
            return true;
        } else if (RelationshipUtils.isRobotTrainer(trainer)) {
            return true;
        }
        return false;
    }

    public static boolean isMwnSimpleFight(AbstractTrainerCreature trainerA, AbstractTrainerCreature trainerB) {
        return judgeMwnSimpleFight(trainerA) && judgeMwnSimpleFight(trainerB);
    }

    /***
     * 魔物娘简易战斗广播
     * @param mwnA
     * @param mwnB
     */
    public static void mwnSimpleFight(MWNCreature mwnA, MWNCreature mwnB) {
        if (isMwnSimpleFight(mwnA.getMaster(), mwnB.getMaster())) {
            MwnSimpleFightResp simpleFightResp = MwnSimpleFightResp.valueOf(1);
            PacketSendUtility.broadcastInWorldMap(mwnA.getMaster(), simpleFightResp, true);
        }
    }

    /**
     * 驯养师卡牌变化
     *
     * @param trainer
     * @param mwnList
     * @param isGain
     * @param isSource
     */
    public static void trainerCardsChange(AbstractTrainerCreature trainer, List<MoWuNiang> mwnList, boolean isGain, boolean isSource, Reason reason) {
        PacketSendUtility.sendPacket(trainer, RoundCardUpdateResp.valueOf(trainer.getObjectId(), mwnList, isGain, isSource, reason));
        //卡包数量变化，需广播前端
        if (isSource) {
            PacketSendUtility.broadcastInWorldMap(trainer, TrainerUseCardNumResp.valueOf(trainer), true);
        }
    }

    public static void throwDice(AbstractTrainerCreature creature) {
        int[] dicePoints = GameUtil.DICE_POINTS;
        if (creature.getEffectController().isInStatus(EffectStatus.Dice_Points_Change)) {
            Effect effect = creature.getEffectController().getEffect(EffectType.Dice_Points_Change);
            DicePointsChangeEffect effectTemplate = (DicePointsChangeEffect) effect.getEffectTemplate();
            dicePoints = effectTemplate.getDicePoints();
        } else if (creature.getEffectController().isInStatus(EffectStatus.Mwn_Throw_Dice)) {
            creature.getEffectController().unsetStatus(EffectStatus.Mwn_Throw_Dice, false);
            // 魔物娘投骰子 已设置点数
            dicePoints = new int[]{creature.getDicePoint().calcPoints()};
        }
        int[] points = RoundFightService.getInstance().randomDice(1, dicePoints);
        DicePoint dicePoint = DicePoint.valueOf(points[0]);
        dicePoint.setRound(creature.getRoom().getRound());
        ThrowDiceResp playerThrowDiceResp = ThrowDiceResp.valueOf(creature.getObjectId(), dicePoint);
        PacketSendUtility.broadcastInWorldMap(creature, playerThrowDiceResp, true);
        creature.setDicePoint(dicePoint);
        //处理投骰子
        creature.getController().onThrowDice();
    }

    public static Set<AbstractCreature> getTargetCreatures(Target target, WorldMapInstance worldMapInstance) {
        if (Objects.isNull(target) || CollectionUtils.isEmpty(target.getTargetIds()) || Objects.isNull(worldMapInstance)) {
            return null;
        }
        Set<AbstractCreature> creatures = new HashSet<>(target.getTargetIds().size());
        for (long targetId : target.getTargetIds()) {
            AbstractCreature creatureById = worldMapInstance.getCreatureById(targetId);
            if (creatureById != null) {
                creatures.add(creatureById);
            }
        }
        return creatures;
    }

    public static void sendUseSkill(AbstractCreature caster, int skillId, List<Long> targetIds, List<AbstractSkillResult> skillResults, boolean broadcast) {
        UseSkillResp useSkillResp = new UseSkillResp();
        useSkillResp.build(caster.getObjectId()).build(skillId).build(targetIds).buildSkillResults(skillResults);
        if (broadcast) {
            PacketSendUtility.broadcastInWorldMap(caster, useSkillResp, true);
        } else {
            PacketSendUtility.sendPacket(caster, useSkillResp);
        }
    }

    /**
     * 回合魔法上限
     */
    public static final int ROUND_MAX_MP = 10;

    /**
     * 开始涨魔法上限回合
     */
    public static final int ROUND_BEGING_ADD_MP = 4;
}
