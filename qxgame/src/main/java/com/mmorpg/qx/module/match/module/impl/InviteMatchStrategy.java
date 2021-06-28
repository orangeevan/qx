package com.mmorpg.qx.module.match.module.impl;

import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.match.enums.MatchType;
import com.mmorpg.qx.module.match.module.AbstractMatchStrategy;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author wang ke
 * @description: 邀请匹配
 * @since 19:07 2020-08-12
 */
@Component
public class InviteMatchStrategy extends AbstractMatchStrategy {

    @Override
    public Collection<AbstractCreature> match(PlayerTrainer player, Conditions conditions) {
        //TODO 后期有其他逻辑再添加
        return null;
    }

    @Override
    public MatchType getMatchType() {
        return MatchType.INVITE;
    }
}
