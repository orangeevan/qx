package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 伤害技能
 * @since 20:25 2020-08-31
 */
@Component
public class HarmSkillProcessor extends AbstractSkillProcessor<DamageSkillResult, Object> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Harm;
    }

    @Override
    public List<DamageSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfendList = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfendList)) {
            return null;
        }
        List<DamageSkillResult> results = new ArrayList<>();
        denfendList.forEach(defender -> {
            //被攻击触发
            defender.handleEffect(TriggerType.ATTACKED, skill.getSkillCaster());
            DamageSkillResult result = RoundFightUtils.fight(skill.getSkillCaster(), defender, skill);
            result.setRoundIndex(roundIndex);
            results.add(result);
            //魔物娘防御者是否有援护
            if (RelationshipUtils.isMWN(defender)) {
                MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(defender);
                if (mwnCreature.hasAssisted()) {
                    List<MWNCreature> assistedMWN = mwnCreature.getAssistedMWN();
                    float percent = 0;
                    for (MWNCreature assist : assistedMWN) {
                        Effect effect = assist.getEffectController().getEffect(EffectType.Share_Damage);
                        if (effect != null && effect.getValue() > 0) {
                            float effectValue = GameUtil.toRatio10000(effect.getValue());
                            percent += effectValue;
                            int damage = Math.round(result.getValue() * effectValue);
                            //援护者扣除血量
                            assist.getLifeStats().reduceHp(damage, skill.getSkillCaster(), skill.getResource().getSkillId(), 0);
                            //生成新的伤害结果
                            DamageSkillResult assistSkillResult = DamageSkillResult.valueOf(skill.getSkillCaster().getObjectId(), defender.getObjectId(), damage, skill.getResource().getSkillId());
                            results.add(assistSkillResult);
                        }
                    }
                    percent = percent > 1 ? 1 : percent;
                    result.setValue((int) (result.getValue() * (1 - percent)));
                }
            }

            //溅射处理
            if (skill.getSkillCaster().getEffectController().isInStatus(EffectStatus.Splash)) {
                Effect effect = skill.getSkillCaster().getEffectController().getEffect(EffectType.Share_Damage);
                List<MWNCreature> mwnArround = defender.getWorldMapInstance().getMWNAroundGrid(defender.getGridId(), false);
                if (!CollectionUtils.isEmpty(mwnArround)) {
                    mwnArround.stream().filter(mwn -> !denfendList.contains(mwn)).forEach(mwn -> {
                        //溅射伤害处理
                        int splashDamage = Math.round(result.getValue() * GameUtil.toRatio10000(effect.getValue()));
                        DamageSkillResult splashResult = DamageSkillResult.valueOf(skill.getSkillCaster().getObjectId(), defender.getObjectId(), splashDamage, skill.getResource().getSkillId());
                        results.add(splashResult);
                    });
                }
            }
            if(result.getValue() > defender.getCurrentHp()){
                result.setValue(defender.getCurrentHp());
            }
            skill.getSkillCaster().getController().attackAndNotifyAttackedObserver(defender, result);
        });
        return results;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
