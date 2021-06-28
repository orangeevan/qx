package com.mmorpg.qx.module.match.module;

import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.match.enums.MatchType;
import com.mmorpg.qx.module.match.manager.MatchManager;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author wang ke
 * @description: 匹配策略
 * @since 17:54 2020-08-12
 */
public abstract class AbstractMatchStrategy {

    /***
     * 匹配逻辑
     * @param playerTrainer
     * @param conditions
     * @return
     */
    public abstract Collection<AbstractCreature> match(PlayerTrainer playerTrainer, Conditions conditions);

    public abstract MatchType getMatchType();

    @PostConstruct
    private void init() {
        MatchManager.getInstance().addMatchStrategy(this);
    }
}
