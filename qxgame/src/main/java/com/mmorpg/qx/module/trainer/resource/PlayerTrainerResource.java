package com.mmorpg.qx.module.trainer.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 驯养师配置表
 * @since 11:30 2020-08-19
 */
@Resource
public class PlayerTrainerResource extends AttrResource {

    @Id
    private int id;

    private String name;

    private int level;

    private List<Attr> baseAttrsList;

    private List<Integer> trainerSkill;

    private List<Integer> skill;

    private int chipKey;

    private int chipNum;

    private List<Integer> manualIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Attr> getBaseAttrsList() {
        return new ArrayList<>(baseAttrsList);
    }

    public void setBaseAttrsList(List<Attr> baseAttrsList) {
        this.baseAttrsList = baseAttrsList;
    }

    public List<Integer> getTrainerSkill() {
        return trainerSkill;
    }

    public void setTrainerSkill(List<Integer> trainerSkill) {
        this.trainerSkill = trainerSkill;
    }

    public void init() {
        baseAttrsList = GameUtil.buildAttr(this);
    }

    public int getChipKey() {
        return chipKey;
    }

    public void setChipKey(int chipKey) {
        this.chipKey = chipKey;
    }

    public int getChipNum() {
        return chipNum;
    }

    public void setChipNum(int chipNum) {
        this.chipNum = chipNum;
    }

    public List<Integer> getManualIds() {
        return manualIds;
    }

    public void setManualIds(List<Integer> manualIds) {
        this.manualIds = manualIds;
    }

    public List<Integer> getSkill() {
        return skill;
    }

    public void setSkill(List<Integer> skill) {
        this.skill = skill;
    }
}
