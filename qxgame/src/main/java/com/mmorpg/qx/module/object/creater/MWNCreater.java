package com.mmorpg.qx.module.object.creater;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.common.identify.manager.IdentifyManager.IdentifyType;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.controllers.MWNController;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 战场地图召唤魔物娘生成器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class MWNCreater {

    /**
     * 创建魔物娘生物，属性来自魔物娘养成
     *
     * @param trainer
     * @param moWuNiang
     * @param position
     * @return
     */
    public static MWNCreature create(AbstractTrainerCreature trainer, MoWuNiang moWuNiang, WorldPosition position) {
        MWNResource resource = moWuNiang.getResource();
        MWNCreature mwnCreature = createSimpleCreature(trainer, resource, position);
        mwnCreature.setMwn(moWuNiang);
        initAttr(mwnCreature, new ArrayList<>(moWuNiang.getAttrList()));
        initSkill(mwnCreature);
        return mwnCreature;
    }

    /**
     * 创建魔物娘，属性主要来自魔物娘配置表基础属性
     *
     * @param trainer
     * @param mwnResource
     * @param position
     * @return
     */
    public static MWNCreature create(AbstractTrainerCreature trainer, MWNResource mwnResource, WorldPosition position) {
        MWNCreature creature = createSimpleCreature(trainer, mwnResource, position);
        initAttr(creature, new ArrayList<>(mwnResource.getBaseAttrsList()));
        initSkill(creature);
        return creature;
    }

    private static MWNCreature createSimpleCreature(AbstractTrainerCreature trainer, MWNResource mwnResource, WorldPosition position) {
        MWNCreature creature = new MWNCreature(IdentifyManager.getInstance().getNextIdentify(IdentifyType.MOWUNIANG), new MWNController(), position, trainer, mwnResource.getId());
        creature.setPosition(position);
        creature.setDir(DirType.indexOfType(trainer.getDir().getDir()));
        creature.setSpawnKey(mwnResource.getId());
        creature.setObjectKey(mwnResource.getId());
        creature.setEffectController(new EffectController(creature));
        return creature;
    }

    private static void initAttr(MWNCreature mwnCreature, List<Attr> attrs) {
        MWNResource resource = mwnCreature.getResource();
        if (attrs.stream().anyMatch(attr -> attr.getType() == AttrType.Trainer_Harm)) {
            Attr trainerHarm = Attr.valueOf(AttrType.Trainer_Harm, resource.getCostMp());
            attrs.add(trainerHarm);
        }
        mwnCreature.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Level_Base), attrs, true, false);
    }

    private static void initSkill(MWNCreature creature) {
        MWNResource resource = MWNManager.getInstance().getMWNResource(creature.getResourceId());
        if (!CollectionUtils.isEmpty(resource.getSkill())) {
            resource.getSkill().forEach(skillId -> {
                if (skillId == 0) {
                    return;
                }
                SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
                if (skillResource.isPassive()) {
                    List<Integer> effects = skillResource.getEffects();
                    if (!CollectionUtils.isEmpty(effects)) {
                        for (int effectId : effects) {
                            Effect effect = new Effect(creature, creature, SkillManager.getInstance().getEffectResource(effectId).getEffectType().create(), skillId, effectId, Target.valueOf(creature.getGridId(), creature.getObjectId()));
                            creature.getEffectController().addEffectsOnly(effect);
                        }
                    }
                } else {
                    creature.getSkillController().addSkill(skillId);
                }
            });
        }
    }
}
