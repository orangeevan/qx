package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * @author: yuanchengyan
 * @description:
 * @since 17:00 2021/4/20
 */
public class SenseCondition extends AbstractEffectTriggerCondition {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.CALL_MWN_SENSE;
    }

    @Override
    public void init(ConditionResource resource) {
        super.init();
        if (StringUtils.isNotBlank(getParams())) {
            param = JsonUtils.string2Object(getParams(), SenseCondition.Param.class);
        }
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Object obj) {
        if (super.verify(trigger, triggerType, obj).isFailure()) {
            return Result.FAILURE;
        }

        Collection<MWNCreature> mwns = trigger.getWorldMapInstance().findMWN();
        for (MWNCreature mwn : mwns) {
            if (mwn.getObjectId() == trigger.getObjectId()) {
                continue;
            }
            MWNResource resource = MWNManager.getInstance().getMWNResource(mwn.getObjectKey());
            if (Objects.isNull(resource)) {
                return Result.FAILURE;
            }
            if (param.mvnId > 0 && param.mvnId != resource.getId()) {
                return Result.FAILURE;
            }
            if (Objects.nonNull(param.attrType) && !trigger.hasAttr(param.attrType)) {
                return Result.FAILURE;
            }
            if (param.job > 0 && param.job != resource.getJobType()) {
                return Result.FAILURE;
            }
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }

    public static class Param {
        private int mvnId;
        private AttrType attrType;
        private int job;

        public Param() {
        }

        public int getMvnId() {
            return mvnId;
        }

        public void setMvnId(int mvnId) {
            this.mvnId = mvnId;
        }

        public AttrType getAttrType() {
            return attrType;
        }

        public void setAttrType(AttrType attrType) {
            this.attrType = attrType;
        }

        public int getJob() {
            return job;
        }

        public void setJob(int job) {
            this.job = job;
        }
    }
}

