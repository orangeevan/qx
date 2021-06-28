package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 反噬
 *
 * @author: yuanchengyan
 * @description:
 * @since 14:07 2021/4/12
 */
public class BiteBackEffect extends AbstractEffectTemplate {
    private List<Attr> alterAttrs;

    @Override
    public void init(EffectResource resource) {

        super.init(resource);
        if (StringUtils.isEmpty(resource.getParam())) {
            alterAttrs = Collections.EMPTY_LIST;
        }
        alterAttrs = JsonUtils.string2List(resource.getParam(), Attr.class);
    }

    @Override
    public boolean applyEffect(Effect effect) {

        if (alterAttrs.size() == 0) {
            return false;
        }
        AbstractCreature effected = effect.getEffected();
        AbstractCreature effector = effect.getEffector();

        List<Attr> newAttrs = GameUtil.magicStreAlterEffectAttr(effect, effected, alterAttrs);
        effector.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResource().getId()), newAttrs, true, true);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        MWNCreature enemyMWN = effect.getEffected().getMaster().getFightMWN();
        enemyMWN.getAttrController().removeModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResourceId()));
    }
}

