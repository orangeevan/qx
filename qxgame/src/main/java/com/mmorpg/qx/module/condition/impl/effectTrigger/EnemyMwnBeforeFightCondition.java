package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

import java.util.Collection;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class EnemyMwnBeforeFightCondition extends AbstractEffectTriggerCondition<Integer> {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.MWN_BEFORE_FIGHT_ENEMY;
    }

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        param = JsonUtils.string2Object(params, EnemyMwnBeforeFightCondition.Param.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        Result result = super.verify(trigger, triggerType, amount);
        if (result.isFailure()) {
            return result;
        }
        int supportMwn = param.supportMwn;

        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(trigger);
        Collection<AbstractVisibleObject> enemys = mwnCreature.getWorldMapInstance().findAbstractVisibleObject(p -> {
            return p instanceof MWNCreature &&
                    p.getObjectId() != mwnCreature.getObjectId() &&
                    RelationshipUtils.judgeRelationship((MWNCreature) p, mwnCreature, RelationshipUtils.Relationships.ENEMY_MWN_MWN);
        });
        for (AbstractVisibleObject enemy : enemys) {
            MWNCreature mvn = (MWNCreature) enemy;
            if (supportMwn == 0 && mvn.getSupporter() == 0) {
                return Result.SUCCESS;
            }
            if (supportMwn == -1 && mvn.getSupporter() != 0) {
                return Result.SUCCESS;
            }
            if (supportMwn > 0) {
                MWNCreature mwn = trigger.getMaster().getMwn(mvn.getSupporter());
                if (mwn.getObjectKey() == supportMwn) {
                    return Result.SUCCESS;
                }
            }
        }

        return Result.FAILURE;

    }

    public static class Param {
        private int supportMwn;

        public Param() {
        }

        public int getSupportMwn() {
            return supportMwn;
        }

        public void setSupportMwn(int supportMwn) {
            this.supportMwn = supportMwn;
        }
    }
}
