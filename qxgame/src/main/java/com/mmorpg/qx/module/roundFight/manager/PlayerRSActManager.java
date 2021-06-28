package com.mmorpg.qx.module.roundFight.manager;

import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.ActStage;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.DicePoint;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActSelector;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 玩家每回合行为管理者
 * @since 11:06 2020-08-14
 */
public class PlayerRSActManager {

    public static AbstractRSActSelector createRsActSelector(RoundStage stage) {
        switch (stage) {
            case Extract_Card_Before:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER,
                        RoundStage.Extract_Card_Before, PacketId.PLAYER_USE_MAGIC_REQ, PacketId.USE_SKILL_REQ) {
                    @Override
                    public void ready(PlayerTrainerCreature trainer) {
                        RoundFightService.getInstance().recoverMpOnNewRound(trainer);
                        trainer.clearFightRScount();
                        trainer.clearConsumeMpRS();
                        trainer.clearUseSkillRS();
                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {

                    }
                };

            case Extract_Card:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.Extract_Card, PacketId.PLAYER_EXTRACT_CARD_REQ) {
                    @Override
                    public void ready(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {
                        //广播开始抽牌
                        //玩家开始抽牌
                        RoundFightService.getInstance().roundAddCard(trainer, 1, Reason.Round_Extract_Card);
                    }
                };

            case Throw_Dice_Before:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.Throw_Dice_Before,
                        PacketId.PLAYER_USE_MAGIC_REQ, PacketId.PLAYER_CALL_SUMMON_REQ, PacketId.USE_SKILL_REQ, PacketId.MWN_WEAR_EQUIP_REQ, PacketId.MWN_FIGHT_REQ, PacketId.MWN_FIGHT_READY_REQ, PacketId.MWN_SUPPORT_REQ) {

                    @Override
                    public void ready(PlayerTrainerCreature trainer) {
                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void end(PlayerTrainerCreature trainer) {
                        super.end(trainer);
                    }
                };

            case Throw_Dice:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.Throw_Dice, PacketId.PLAYER_THROW_DICE_REQ, PacketId.COST_CARD_THROW_DICE) {

                    @Override
                    public void ready(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {
                        synchronized (trainer) {
                            DicePoint dicePoint = trainer.getDicePoint();
                            if (Objects.isNull(dicePoint) || dicePoint.getRound() < trainer.getRoom().getRound()) {
                                RoundFightUtils.throwDice(trainer);
                                //trainer.syncActStage(ActStage.START, ActStage.END);
                            }
                        }
                    }
                };
            case Throw_Dice_After:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.Throw_Dice_After,
                        PacketId.PLAYER_CALL_SUMMON_REQ, PacketId.USE_SKILL_REQ) {

                    @Override
                    public void ready(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {
                        trainer.syncActStage(ActStage.START, ActStage.END);
                    }
                };
            case MOVE:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.MOVE, PacketId.TRAINER_MOVE_REQ, PacketId.USE_BUILDING_SKILL_REQ) {

                    @Override
                    public void ready(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {
                        /**根据骰子数移动，考虑玩家超时未操作帮玩家完成移动动作*/
                        DicePoint dicePoint = trainer.getDicePoint();
                        if (dicePoint == null) {
                            return;
                        }
                        int steps = dicePoint.calcPoints() - trainer.getMoveController().getStep();
                        /** 开始移动*/
                        trainer.getMoveController().startMove(steps, trainer.getDir(), false);
                    }

                    @Override
                    public void end(PlayerTrainerCreature trainer) {
                        try {
                            trainer.getController().onMoveEnd();
                        } finally {
                            super.end(trainer);
                        }
                    }

                };
            case MOVE_END:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.MOVE_END, PacketId.PLAYER_USE_MAGIC_REQ, PacketId.USE_SKILL_REQ,
                        PacketId.PLAYER_CALL_SUMMON_REQ, PacketId.MWN_FIGHT_REQ, PacketId.MWN_WEAR_EQUIP_REQ, PacketId.MWN_FIGHT_READY_REQ, PacketId.USE_BUILDING_SKILL_REQ, PacketId.MWN_SUPPORT_REQ) {

                    @Override
                    public void ready(PlayerTrainerCreature trainer) {
                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void end(PlayerTrainerCreature trainer) {
                        try {
                            MWNCreature mwn = trainer.getRoom().getWorldMapInstance().getMWNByGridId(trainer.getGridId());
                            //最终停留格有敌方魔物娘，没有召唤魔物娘战斗将受到敌方魔物娘对我方驯养师伤害
                            if (!trainer.isCallMwnFight(trainer.getGridId()) && RelationshipUtils.judgeRelationship(trainer, mwn, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)) {
                                RoundFightService.getInstance().mwnFightHarmTrainer(trainer, mwn);
                            }
                        } finally {
                            super.end(trainer);
                        }
                    }
                };
            case Round_END:
                return new AbstractRSActSelector<PlayerTrainerCreature, Short>(ObjectType.PLAYER_TRAINER, RoundStage.Round_END, PacketId.PLAYER_USE_MAGIC_REQ, PacketId.USE_SKILL_REQ) {

                    @Override
                    public void ready(PlayerTrainerCreature trainer) {

                    }

                    @Override
                    public void start(PlayerTrainerCreature trainer) {

                    }
                };
            default:
                throw new IllegalArgumentException("不支持回合类型");

        }
    }
}
