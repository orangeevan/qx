package com.mmorpg.qx.module.troop.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @since 16:21 2021/6/7
 */
@Resource
public class TroopSkillResource {

    // 唯一ID
    @Id
    private int id;

    private int skillId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
