package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.manager.RoomManager;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 限制移动, 特殊效果，触发方式必定为新回合出触发
 * 1.当玩家技能及效果结算结束后弹出弹窗；告知玩家此时点击右下角将直接结束回合不会经历投骰子及移动阶段；倒计时3秒或点击确认按钮可关闭弹窗。
 * 2.此时按钮状态与投骰子后阶段一致，点击自动进入下个玩家回合。
 * @since 17:48 2020-09-04
 */
public class LimitMoveEffect extends AbstractEffectTemplate {
    private float value;

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
        int configValue = effect.getEffectResource().getValue();
        value = GameUtil.toRatio10000(configValue);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(creature);
                Room room = trainer.getRoom();
                if (room.getCurrentTurn() == trainer && trainer.isInActStage(null) && room.isInStage(RoundStage.Throw_Dice)) {
                    //直接跳过投骰子和移动流程
                    RoomManager.getInstance().switchRoundStage(room, RoundStage.MOVE_END);
                    return true;
                }
            }
        }
        return false;
    }
}
