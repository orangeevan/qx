package com.mmorpg.qx.module.skill.model.cd;

import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.packet.vo.TrainerBuildCDVo;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能冷却容器
 *
 * @author wang ke
 * @since v1.0 2018年3月2日
 */
public final class CDContainer {
    //private ConcurrentHashMap<Integer, Long> skillCoolDowns = new ConcurrentHashMap<>();
    //private ConcurrentHashMap<Integer, Long> publicCoolDowns = new ConcurrentHashMap<>();
    /**
     * 技能CD目前是回合为单位
     */
    private ConcurrentHashMap<Integer, Integer> skillCD = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> publicCD = new ConcurrentHashMap<>();

    /**
     * 建筑CD
     */
    private ConcurrentHashMap<Long, Integer> buildCD = new ConcurrentHashMap<>();


    /***
     * 当前技能CD回合
     * @param skillId
     * @param currentRound
     * @return
     */
    public boolean isSkillCD(int skillId, int currentRound) {
        if (skillCD == null || !skillCD.containsKey(skillId)) {
            return false;
        }
        int coolDown = skillCD.get(skillId);
        if (coolDown <= currentRound) {
            return false;
        }
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        if (skillResource.getPublicGroup() == 0) {
            return false;
        }
        if (!publicCD.containsKey(skillResource.getPublicGroup())) {
            return false;
        }
        int coolDownPublic = publicCD.get(skillResource.getPublicGroup());
        if (coolDownPublic <= currentRound) {
            return false;
        }
        return true;
    }

    /**
     * @param build
     * @param currentRound
     * @return
     */
    public boolean isBuildCD(long build, int currentRound) {
        if (!buildCD.containsKey(build)) {
            return false;
        }
        int coolDown = buildCD.get(build);
        if (coolDown <= currentRound) {
            return false;
        }
        return true;
    }

    public void resetSkillCD(int skillId) {
        if (skillCD.containsKey(skillId)) {
            skillCD.remove(skillId);
        }
    }

    public int getCurSkillCD(int skillId) {
        return skillCD.containsKey(skillId) ? skillCD.get(skillId) : 0;
    }

    public int getCurPublicSkillCD(int group) {
        return publicCD.containsKey(group) ? publicCD.get(group) : 0;
    }

    public int getCurBuildCD(long buildId) {
        return buildCD.containsKey(buildId) ? buildCD.get(buildId) : 0;
    }

    /***
     * 更新技能cd，如有公共cd则更新，约定CD为0不计CD
     * @param skillId
     * @param cd
     */
    public void updateSkillCD(int skillId, int cd) {
        if (cd > 0) {
            skillCD.put(skillId, getCurSkillCD(skillId) + cd);
        }
        updateSkillGroupCD(skillId);
    }

    private void updateSkillGroupCD(int skillId) {
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        if (skillResource.getPublicGroup() == 0 || skillResource.getPublicCDGroup() == 0) {
            return;
        }
        publicCD.put(skillResource.getPublicGroup(), getCurPublicSkillCD(skillResource.getPublicGroup()) + skillResource.getPublicCDGroup());
    }

    /***
     * 清除技能CD，不清除公共CD
     * @param skillId
     */
    public void clearSkillCD(int skillId) {
        skillCD.remove(skillId);
    }

    /***
     * 清除技能CD，公共CD
     * @param skillId
     */
    public void clearSkillAllCD(int skillId) {
        skillCD.remove(skillId);
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        if (skillResource.getPublicGroup() == 0) {
            return;
        }
        publicCD.remove(skillResource.getPublicGroup());
    }

    public void updateBuildCD(long buildId, int cd) {
        if (cd <= 0) {
            return;
        }
        buildCD.put(buildId, getCurBuildCD(buildId) + cd);
    }

    public List<TrainerBuildCDVo> getBuildVo() {
        List<TrainerBuildCDVo> buildCDVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(buildCD)) {
            for (Map.Entry<Long, Integer> cd : buildCD.entrySet()) {
                buildCDVos.add(TrainerBuildCDVo.valueOf(cd.getKey(), cd.getValue()));
            }
        }
        return buildCDVos;
    }

    public void initSkillCD(int skillId, int cd, int groupCD) {
        skillCD.put(skillId, cd);
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        publicCD.put(skillResource.getPublicGroup(), groupCD);
    }
}
