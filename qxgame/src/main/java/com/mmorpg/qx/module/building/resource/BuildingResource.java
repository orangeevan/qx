package com.mmorpg.qx.module.building.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

import java.util.List;

/**
 * @author wang ke
 * @description: 建筑数据
 * @since 14:44 2020-09-28
 */
@Resource
public class BuildingResource {
    @Id
    private int id;

    private String name;

    private int type;

    private List<Integer> costGold;

    private List<Integer> skills;

    private List<Integer> jobCostGold;

    /**
     * 处于CD消耗金币比例
     */
    private int inCdCostRate;

    /**
     * 建筑cd
     */
    private int buildingCd;

    private int gold;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getCostGold() {
        return costGold;
    }

    public void setCostGold(List<Integer> costGold) {
        this.costGold = costGold;
    }

    public List<Integer> getSkills() {
        return skills;
    }

    public void setSkills(List<Integer> skills) {
        this.skills = skills;
    }

    public List<Integer> getJobCostGold() {
        return jobCostGold;
    }

    public void setJobCostGold(List<Integer> jobCostGold) {
        this.jobCostGold = jobCostGold;
    }

    public int getInCdCostRate() {
        return inCdCostRate;
    }

    public void setInCdCostRate(int inCdCostRate) {
        this.inCdCostRate = inCdCostRate;
    }

    public int getBuildingCd() {
        return buildingCd;
    }

    public void setBuildingCd(int buildingCd) {
        this.buildingCd = buildingCd;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
