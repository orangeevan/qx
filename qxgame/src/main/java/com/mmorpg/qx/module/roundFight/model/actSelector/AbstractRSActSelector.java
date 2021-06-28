package com.mmorpg.qx.module.roundFight.model.actSelector;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.manager.RoomManager;
import com.mmorpg.qx.module.roundFight.model.Room;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author wang ke
 * @description: 回合阶段行为选择器 K : 驯养师 , T:action指令，对玩家可能是消息ID，对AI可能是AIState
 * @since 19:30 2020-08-13
 */
public abstract class AbstractRSActSelector<K extends AbstractTrainerCreature, T> {
    /**
     * 回合阶段
     */
    private RoundStage roundStage;

    /**
     * 允许行为
     */
    private List<T> acceptActions;

    private ObjectType objectType;


    public AbstractRSActSelector(ObjectType type, RoundStage roundStage, T... actions) {
        this.roundStage = roundStage;
        if (actions != null) {
            acceptActions = Arrays.asList(actions);
        }
        this.objectType = type;
    }


    /***
     * 进入阶段完成业务
     * @param trainer
     */
    public abstract void ready(K trainer);

    /***
     * 行为是否允许
     * @param act
     * @return
     */
    public boolean isActionAccept(T act) {
        return acceptActions.contains(act);
    }

    /**
     * 开始执行该阶段必要行为，下阶段可能会依赖上阶段结果
     *
     * @param trainer
     */
    public abstract void start(K trainer);

    /***
     * 执行完后结尾操作
     * @param trainer
     */
    public void end(K trainer) {
        Room room = trainer.getRoom();
        //每阶段结束效果结算
        room.getTrainers().forEach(trainerCreature -> {
            /**先执行驯养师buff处理*/
            trainerCreature.getEffectController().schedule();
            /** 执行魔物娘buff处理*/
            Collection<MWNCreature> alvieMwns = trainerCreature.getMWN(true);
            if (CollectionUtils.isEmpty(alvieMwns)) {
                return;
            }
            alvieMwns.forEach(mwn -> mwn.getEffectController().schedule());
        });
        if (this.roundStage == RoundStage.Round_END) {
            room.setCurrentTurn(room.getNextTrainer());
            //trainer.getEffectController().removeInRoundEnd();
            trainer.resetCallMwnRound();
            trainer.resetCallMwnFight();
        }
        //回合阶段结束触发效果
        trainer.getEffectController().triggerEffects(TriggerType.RoundStage_End, trainer, 0);
        RoomManager.getInstance().switchNextRoundStage(room);
        room.getCurrentTurn().resetActStage();
    }

    public RoundStage getRoundStage() {
        return roundStage;
    }

    public void setRoundStage(RoundStage roundStage) {
        this.roundStage = roundStage;
    }

    public List<T> getAcceptActions() {
        return acceptActions;
    }

    public void setAcceptActions(List<T> acceptActions) {
        this.acceptActions = acceptActions;
    }

    public ObjectType getObjectType() {
        return objectType;
    }
}
