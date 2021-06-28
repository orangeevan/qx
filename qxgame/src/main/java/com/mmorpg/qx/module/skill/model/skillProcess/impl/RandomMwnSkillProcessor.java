package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DisplaySkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.skin.manager.SkinResourceManager;
import com.mmorpg.qx.module.skin.resource.MWNSkinResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wang ke
 * @description: 随机魔物娘 祭坛技能
 * @since 15:44 2020-09-01
 */
@Component
public class RandomMwnSkillProcessor extends AbstractSkillProcessor<DisplaySkillResult, List<Integer>> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Random_Mwn;
    }

    /***
     * 随机出的魔物娘召唤所需魔法值必定小于等于玩家当前最大魔法值。
     * 获取的卡牌部分养成属性（等级）与玩家手牌的平均等级相同。
     * 所获取的卡牌为满足玩家设定条件的随机一张游戏中的魔物娘。
     * @param skill
     * @param roundIndex
     * @return
     */
    @Override
    public List<DisplaySkillResult> process(Skill skill, int roundIndex) {
        List<DisplaySkillResult> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(skill.getTarget().getTargetIds())) {
            return results;
        }
        AttrType attrType = null;
        if (skill.isBuildingSkill() && Objects.nonNull(skill.getBuildSelectJobType())) {
            attrType = skill.getBuildSelectJobType();
        }
        System.err.println("请求复活魔物娘属性类型: " + attrType);
        AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(skill.getSkillCaster());
        Collection<MWNResource> allMwn = MWNManager.getInstance().getAllMwnResource();
        List<Integer> mwnCostMp = initParam(skill.getResource());
        List<MWNResource> filterMwns = allMwn.stream().filter(mwnResource -> mwnCostMp.contains(mwnResource.getCostMp())).collect(Collectors.toList());
        //随机魔物娘
        MWNResource mwnResource = MWNManager.getInstance().randomResource(attrType, trainer, skill.isBuildingSkill(), filterMwns);
        if (Objects.isNull(mwnResource)) {
            return results;
        }
        MoWuNiang moWuNiang = new MoWuNiang();
        moWuNiang.setResourceId(mwnResource.getId());
        moWuNiang.setId(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MOWUNIANG));
        //moWuNiang.setAttrList(mwnResource.getBaseAttrsList());
        MWNSkinResource mwnOriginalSkin = SkinResourceManager.getInstance().getMwnOriginalSkin(mwnResource.getId());
        moWuNiang.setSkinResourceId(mwnOriginalSkin.getId());
        if (trainer.getUseCardStorage().addMwn(moWuNiang)) {
            RoundFightUtils.trainerCardsChange(trainer, Stream.of(moWuNiang).collect(Collectors.toList()), true, false, Reason.Trainer_Skill_Effect);
            results.add(DisplaySkillResult.valueOf(trainer.getObjectId(), skill));
            System.err.println("已复活魔物娘：" + moWuNiang.getResourceId() + " id: " + moWuNiang.getId());
        } else {
            throw new ManagedException(ManagedErrorCode.USE_STORAGE_FULL);
        }
        return results;
    }

    @Override
    public List<Integer> initParam(SkillResource resource) {
        //魔物娘消费列表
        List<Integer> mwnCostMapList = JsonUtils.string2List(resource.getParam(), Integer.class);
        return mwnCostMapList;
    }
}
