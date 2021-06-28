package com.mmorpg.qx.module.skill.model.skillProcess;

import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description: 根据每个类型定义对应技能处理
 * @since 19:38 2020-08-31
 */
public abstract class AbstractSkillProcessor<T extends AbstractSkillResult, K> {

    private static Map<SkillType, AbstractSkillProcessor> typeToProcess = new HashMap<>(SkillType.values().length);

    public abstract SkillType getSkillType();

    @PostConstruct
    void init() {
        typeToProcess.put(getSkillType(), this);
    }

    public abstract List<T> process(Skill skill, int roundIndex);

    public abstract K initParam(SkillResource resource);

    public static AbstractSkillProcessor getSkillProcessor(SkillType type){
        return typeToProcess.get(type);
    }
}
