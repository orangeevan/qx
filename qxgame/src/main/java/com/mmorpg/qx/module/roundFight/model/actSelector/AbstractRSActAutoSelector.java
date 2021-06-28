package com.mmorpg.qx.module.roundFight.model.actSelector;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

/**
 * @author wang ke
 * @description: 具有按回合自动执行行为选择器，多用于AI
 * @since 09:51 2020-08-14
 */
public abstract class AbstractRSActAutoSelector extends AbstractRSActSelector<RobotTrainerCreature, AIState> {

    public AbstractRSActAutoSelector(ObjectType objectType, RoundStage roundStage, AIState... actions) {
        super(objectType, roundStage, actions);
        if (!CollectionUtils.isEmpty(super.getAcceptActions())) {
            actCustom = new LinkedHashMap<>();
            super.getAcceptActions().stream().forEach(aiState -> orderAIAction(aiState));
        }
    }

    /**
     * 需要自动行为才需要定制,后面AI完善了可以直接对应Action来处理
     */
    private LinkedHashMap<AIState, Consumer<RobotTrainerCreature>> actCustom;

    /**
     * AI行为自动执行
     */
    private void autoAction(RobotTrainerCreature trainer) {
        if (!CollectionUtils.isEmpty(actCustom)) {
            //机器人驯养师处于特殊效果状态，不执行相应逻辑
            if (trainer.getEffectController().isInStatus(EffectStatus.Idle)) {
                return;
            }
            actCustom.values().forEach(consumer -> consumer.accept(trainer));
        }
    }

    /***
     * 给每个Act定制行为
     * @param act
     * @param consumer
     */
    public boolean orderAction(AIState act, Consumer<RobotTrainerCreature> consumer) {
        if (CollectionUtils.isEmpty(actCustom)) {
            actCustom = new LinkedHashMap<>();
        }
        actCustom.put(act, consumer);
        return true;
    }

    /***
     * 带有AI驯养师，行为由stateHandler处理
     * @param aiState
     * @param params
     * @return
     */
    public boolean orderAIAction(AIState aiState, Object... params) {
        return orderAction(aiState, trainerCreature -> trainerCreature.getAi().handleState(aiState, trainerCreature, params));
    }

    @Override
    public void start(RobotTrainerCreature trainer) {
        autoAction(trainer);
    }

}
