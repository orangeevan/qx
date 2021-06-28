package com.mmorpg.qx.module.skill.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.model.target.TargetType;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 技能管理器
 *
 * @author wang ke
 * @since v1.0 2018年3月2日
 */
@Component
public class SkillManager implements ResourceReload {

    @Static
    private Storage<Integer, SkillResource> skillResourceStorage;

    @Autowired
    private EffectManager effectManager;

    public static SkillManager getInstance() {
        return BeanService.getBean(SkillManager.class);
    }

    @PostConstruct
    protected void init() {
        initResources();
    }

    private void initResources() {
        /** 初始化使用条件*/
        if (!CollectionUtils.isEmpty(skillResourceStorage.getAll())) {
            skillResourceStorage.getAll().forEach(skillResource -> {
                if (!StringUtils.isEmpty(skillResource.getUseConditions())) {
                    skillResource.setUseConditionsList(JsonUtils.string2List(skillResource.getUseConditions(), ConditionResource.class));
                }
                if (!StringUtils.isEmpty(skillResource.getTargetType())) {
                    Map<String, String> strMap = JsonUtils.string2Map(skillResource.getTargetType(), String.class, String.class);
                    Map<TargetType, String> targets = new HashMap<>();
                    for (Map.Entry<String, String> entry : strMap.entrySet()) {
                        targets.put(TargetType.valueOf(entry.getKey()), entry.getValue());
                    }
                    skillResource.setTargets(targets);
                }
            });
        }

    }

    public SkillResource getSkillResource(int id) {
        return skillResourceStorage.get(id, true);
    }

    public EffectResource getEffectResource(int id) {
        return effectManager.getResource(id);
    }

    public void addEffects(AbstractCreature caster, List<Integer> effects, int skillId, final Target target) {
        if (CollectionUtils.isEmpty(effects)) {
            return;
        }

        for (int effectId : effects) {
            EffectResource effectResource = SkillManager.getInstance().getEffectResource(effectId);
            Target tempTarget = target;
            if (Objects.isNull(tempTarget) || CollectionUtils.isEmpty(tempTarget.getTargetIds())) {
                tempTarget = TargetManager.getInstance().chooseTarget(effectResource.getTargets().keySet().stream().collect(Collectors.toList()), caster);
            }
            if (Objects.isNull(tempTarget) || CollectionUtils.isEmpty(tempTarget.getTargetIds())) {
                continue;
            }
            //目标数为0全体
            if (effectResource.getMaxTarget() > 0) {
                if (tempTarget.getTargetIds().size() > effectResource.getMaxTarget()) {
                    List<Long> subList = tempTarget.getTargetIds().subList(0, effectResource.getMaxTarget() - 1);
                    tempTarget = Target.valueOf(tempTarget.getGridId(), subList);
                }
            }
            final Target realTarget = tempTarget;
            tempTarget.getTargetIds().forEach(targetId -> {
                AbstractCreature effected = caster.getWorldMapInstance().getCreatureById(targetId);
                //effected.getEffectController().getEffects()
                if (effected != null && effectResource != null) {
                    Effect effect = new Effect(caster, effected, effectResource.getEffectType().create(), skillId, effectId, realTarget);
                    effected.getEffectController().addEffect(effect);
                }
            });
        }
    }

    public Collection<SkillResource> getSkillByReleaseType(SkillReleaseType type) {
        return skillResourceStorage.getAll().stream().filter(resource -> resource.getReleaseType() == type).collect(Collectors.toList());
    }

    @Override
    public void reload() {
        initResources();
    }

    @Override
    public Class<?> getResourceClass() {
        return SkillResource.class;
    }
}
