package com.mmorpg.qx.module.skill.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.module.skill.model.target.TargetType;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wang ke
 * @description: 效果资源管理
 * @since 18:06 2020-11-30
 */
@Component
public class EffectManager implements ResourceReload {
    @Static
    private Storage<Integer, EffectResource> effectResourceStorage;

    @PostConstruct
    private void init() {
        initResouces();
    }

    public static EffectManager getInstance() {
        return BeanService.getBean(EffectManager.class);
    }

    private void initResouces() {
        if (!CollectionUtils.isEmpty(effectResourceStorage.getAll())) {
            effectResourceStorage.getAll().forEach(effectResource -> {
                if (!StringUtils.isEmpty(effectResource.getTargetType())) {
                    Map<String, String> strMap = JsonUtils.string2Map(effectResource.getTargetType(), String.class, String.class);
                    Map<TargetType, String> targets = new HashMap<>();
                    for (Map.Entry<String, String> entry : strMap.entrySet()) {
                        targets.put(TargetType.valueOf(entry.getKey()), entry.getValue());
                    }
                    effectResource.setTargets(targets);
                }
            });
        }
    }

    @Override
    public void reload() {
        initResouces();
    }

    @Override
    public Class<?> getResourceClass() {
        return EffectResource.class;
    }

    public EffectResource getResource(int id) {
        return effectResourceStorage.get(id, false);
    }
}
