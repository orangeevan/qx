package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wang ke
 * @description: 属性类buff，增减
 * @since 10:52 2020-08-27
 */
public class AlterAttrEffect extends AbstractEffectTemplate {

    private List<Attr> alterAttrs;

    @Override
    public boolean applyEffect(Effect effect) {
        EffectResource effectResource = effect.getEffectResource();
        if (StringUtils.isEmpty(effectResource.getParam())) {
            return false;
        }
        Map<AttrType, Integer> attrMap = JsonUtils.string2Map(effectResource.getParam(), AttrType.class, Integer.class);
        if (CollectionUtils.isEmpty(attrMap)) {
            return true;
        }
        alterAttrs = new ArrayList<>();
        for (Map.Entry<AttrType, Integer> entry : attrMap.entrySet()) {
            alterAttrs.add(Attr.valueOf(entry.getKey(), entry.getValue()));
        }
        chooseTargets(effect);
        Set<AbstractCreature> targets = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(creature -> {
                List<Attr> newAttrs = GameUtil.magicStreAlterEffectAttr(effect, effect.getEffector(), alterAttrs);
                creature.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effectResource.getId()), newAttrs, true, true);
            });
        }
        //effect.getEffected().getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect), alterAttrs, true, true);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> targets = this.getTargets();
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(creature -> {
                creature.getAttrController().removeModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResourceId()));
            });
            //this.targets = targets;
        }
        //effect.getEffected().getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect), attrList, true, true);
    }
}
