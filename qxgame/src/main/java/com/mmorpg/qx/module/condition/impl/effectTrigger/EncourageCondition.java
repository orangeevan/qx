package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.worldMap.model.VirPoint;

import java.util.Collection;

/**
 * @author: yuanchengyan
 * @description:
 * @since 17:00 2021/4/20
 */
public class EncourageCondition extends AbstractEffectTriggerCondition {


    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ENCOURAGE;
    }

    @Override
    public void init(ConditionResource resource) {
        super.init();

    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Object obj) {
        if (super.verify(trigger, triggerType, obj).isFailure()) {
            return Result.FAILURE;
        }
        if (RelationshipUtils.isMWN(trigger)) {
            Collection<MWNCreature> mwns = trigger.getWorldMapInstance().findMWN();
            for (MWNCreature mwn : mwns) {
                if (!RelationshipUtils.judgeRelationship(trigger, mwn, RelationshipUtils.Relationships.FRIEND_MWN_MWN)) {
                    return Result.FAILURE;
                }
                MWNResource mwnResource = mwn.getMwn().getResource();
                VirPoint virPoint = GameUtil.getMwnVirPoint(mwn.getWorldMapInstance().getParent(), mwn.getGridId());
                if (!GameUtil.isInRange(mwn.getWorldMapInstance().getParent(), virPoint, trigger.getGridId(), mwnResource.Attack_Range_Face, mwnResource.Attack_Range_Face_Ver)) {
                    return Result.FAILURE;
                }
            }
        }
        return Result.SUCCESS;
    }

}

