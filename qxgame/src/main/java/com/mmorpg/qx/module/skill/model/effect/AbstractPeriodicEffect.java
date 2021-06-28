package com.mmorpg.qx.module.skill.model.effect;

import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.resource.EffectResource;

/**
 * 有间隔的心跳buff
 *
 * @author wang ke
 * @since v1.0 2018年3月3日
 */
public abstract class AbstractPeriodicEffect extends AbstractEffectTemplate {
    // 循环回合数
    private int periodTime;

    @Override
    public void init(EffectResource resource) {
        periodTime = resource.getPeriod();
    }

    @Override
    public boolean applyEffect(Effect effect) {
        if (effect.getEffected().isAlreadyDead()) {
            return false;
        }
        //effect.getEffectResource().getAndCreateConditions().verify()
        effect.addToEffectedController();
        // 默认延迟后开始跳动
        effect.setLastPeriodRound(effect.getRoom().getRound());
        effect.setPeriodRound(effect.getRoom().getRound() + periodTime);
        return true;
    }


    public void onPeriodicAction(Effect effect) {
        DamageSkillResult damage = DamageSkillResult.valueOf(effect.getEffector().getObjectId(), effect.getEffector().getObjectId(), effect.getValue(), effect.getSkillResourceId());
        // 扣减伤害
        effect.getEffector().getController().attackAndNotifyAttackedObserver(effect.getEffected(), damage);
        effect.setLastPeriodRound(effect.getRoom().getRound());
        effect.setPeriodRound(effect.getRoom().getRound() + periodTime);
    }

    public int getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(int periodTime) {
        this.periodTime = periodTime;
    }

}