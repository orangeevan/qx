package com.mmorpg.qx.module.troop.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.module.troop.resource.TroopSkillResource;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @since 16:31 2021/6/7
 */
@Component
public class TroopSkillManager implements ResourceReload {

    @Static
    private Storage<Integer, TroopSkillResource> troopSkillStorage;

    /**
     * 第一行为默认技能ID
     *
     * @return
     */
    public int getDefaultSkill() {
        return troopSkillStorage.getAll().stream().filter(t -> t.getId() == 1).map(TroopSkillResource::getSkillId).findAny().get();
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return null;
    }

    public static TroopSkillManager getInstance() {
        return BeanService.getBean(TroopSkillManager.class);
    }
}
