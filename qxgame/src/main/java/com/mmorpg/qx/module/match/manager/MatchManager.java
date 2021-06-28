package com.mmorpg.qx.module.match.manager;

import com.haipaite.common.utility.New;
import com.mmorpg.qx.module.match.enums.MatchType;
import com.mmorpg.qx.module.match.module.AbstractMatchStrategy;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author wang ke
 * @description: 匹配管理者
 * @since 17:47 2020-08-12
 */
@Component
public class MatchManager {

    private static MatchManager instance;

    private Map<MatchType, AbstractMatchStrategy> matchStrategy = New.hashMap(MatchType.values().length);

    public void addMatchStrategy(AbstractMatchStrategy strategy){
        matchStrategy.put(strategy.getMatchType(), strategy);
    }

    public AbstractMatchStrategy getMatchStrategy(MatchType type){
        return matchStrategy.get(type);
    }

    @PostConstruct
    private void init(){
        instance = this;
    }

    public static MatchManager getInstance(){
        return instance;
    }
}
