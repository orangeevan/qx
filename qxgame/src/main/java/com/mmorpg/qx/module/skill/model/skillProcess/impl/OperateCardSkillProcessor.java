package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.SelectRandom;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.creater.MWNCreater;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang peng
 * @description 卡牌操作-获取或丢弃牌库中随机或指定的卡牌
 * @since 16:11 2021/4/13
 */
@Component
public class OperateCardSkillProcessor extends AbstractSkillProcessor<AbstractSkillResult, Map<String, Integer>> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Operate_Card;
    }

    @Override
    public List<AbstractSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfenders = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfenders)) {
            return null;
        }
        Map<String, Integer> param = initParam(skill.getResource());
        // mode:1获取2丢弃 type:1随机2指定  num:获取数量  mwnId:指定条件,魔物娘配置ID  eleType:指定条件,魔物娘所属元素
        Integer mode = param.get("mode");
        Integer type = param.get("type");
        Integer num = param.get("num");
        Integer mwnId = param.get("mwnId");
        Integer eleType = param.get("eleType");
        if ((mode != 1 && mode != 2) || (type != 1 && type != 2)) {
            throw new ManagedException(ManagedErrorCode.SYS_ERROR);
        }
        if (mode == 1) {
            if (type == 1) {
                getRandomCard(denfenders, num);
            } else {
                getFixedCard(denfenders, num, mwnId, eleType);
            }
        } else {
            if (type == 1) {
                throwRandomCard(denfenders, num);
            } else {
                throwFixedCard(denfenders, num, mwnId, eleType);
            }
        }
        String format = String.format("卡牌操作技能, 技能ID:%s, mode:%s, type:%s, num:%s, mwnId:%s, eleType:%s",
                skill.getResource().getSkillId(), mode, type, num, mwnId, eleType);
        System.err.println(format);
        return null;
    }

    /**
     * 随机一张卡牌
     *
     * @param source
     * @return
     */
    private List<MoWuNiang> randomCard(List<MoWuNiang> source) {
        List<MoWuNiang> result = new ArrayList<>();
        if (!source.isEmpty()) {
            SelectRandom<MoWuNiang> selectRandom = new SelectRandom<>();
            source.forEach(mwn -> selectRandom.addElement(mwn, 1));
            result = selectRandom.run(1);
        }
        return result;
    }

    /**
     * 发送鬼牌
     *
     * @param trainer
     */
    private void sendGhostCard(AbstractTrainerCreature trainer) {
        if (!trainer.isAlreadyDead()) {
            int reduceHp = (int) Math.pow(2, trainer.incExtractCardZero(1));
            trainer.getLifeStats().reduceHp(reduceHp, trainer, Reason.No_Card, true);
            RoundFightService.getInstance().sendBlackCard(trainer);
        }
    }

    /**
     * 卡牌加入手牌
     *
     * @param trainer
     * @param add
     */
    private void addToUseCard(AbstractTrainerCreature trainer, List<MoWuNiang> add) {
        List<MoWuNiang> gainCards = new ArrayList<>();
        List<MoWuNiang> explodeCards = new ArrayList<>();
        for (MoWuNiang mwn : add) {
            if (!trainer.getUseCardStorage().addMwn(mwn)) {
                // 移除的魔物娘进入驯养师召唤列表，以待复活
                MWNCreature mwnCreature = MWNCreater.create(trainer, mwn, null);
                mwnCreature.getLifeStats().reduceHp(mwnCreature.getCurrentHp(), null, 0, 0);
                trainer.addMWN(mwnCreature);
                explodeCards.add(mwn);
            } else {
                gainCards.add(mwn);
            }
        }
        if (!CollectionUtils.isEmpty(gainCards)) {
            RoundFightUtils.trainerCardsChange(trainer, gainCards, true, false, Reason.Operate_Card_Skill);
        }
        if (!CollectionUtils.isEmpty(explodeCards)) {
            RoundFightUtils.trainerCardsChange(trainer, explodeCards, false, false, Reason.Explode_Card);
        }
    }

    /**
     * 根据条件选择卡牌
     *
     * @param source
     * @param mwnId
     * @param eleType
     * @return
     */
    private List<MoWuNiang> requiredCard(List<MoWuNiang> source, Integer mwnId, Integer eleType) {
        return source.stream().filter(t -> mwnId == 0 || t.getResourceId() == mwnId)
                .filter(t -> eleType == 0 || t.getResource().getEleType() == eleType)
                .collect(Collectors.toList());
    }

    /**
     * 获取随机卡牌
     *
     * @param denfenders
     * @param num
     */
    private void getRandomCard(List<AbstractCreature> denfenders, Integer num) {
        for (AbstractCreature defender : denfenders) {
            AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(defender);
            List<MoWuNiang> source = trainer.getSourceCardStorage().getMwns();
            for (int i = 0; i < num; i++) {
                if (!source.isEmpty()) {
                    List<MoWuNiang> select = randomCard(source);
                    // 牌库移除卡牌
                    source.removeAll(select);
                    RoundFightUtils.trainerCardsChange(trainer, select, false, true, Reason.Operate_Card_Skill);
                    // 卡牌加入手牌
                    addToUseCard(trainer, select);
                } else {
                    // 发放鬼牌
                    sendGhostCard(trainer);
                }
            }
        }
    }

    /**
     * 获取指定卡牌
     *
     * @param denfenders
     * @param num
     * @param mwnId
     * @param eleType
     */
    private void getFixedCard(List<AbstractCreature> denfenders, Integer num, Integer mwnId, Integer eleType) {
        for (AbstractCreature defender : denfenders) {
            AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(defender);
            List<MoWuNiang> source = trainer.getSourceCardStorage().getMwns();
            for (int i = 0; i < num; i++) {
                if (!source.isEmpty()) {
                    List<MoWuNiang> required = requiredCard(source, mwnId, eleType);
                    if (!required.isEmpty()) {
                        List<MoWuNiang> select = randomCard(required);
                        // 牌库移除卡牌
                        source.removeAll(select);
                        RoundFightUtils.trainerCardsChange(trainer, select, false, true, Reason.Operate_Card_Skill);
                        // 卡牌加入手牌
                        addToUseCard(trainer, select);
                    } else {
                        throw new ManagedException(ManagedErrorCode.NO_FIXED_CARD);
                    }
                } else {
                    // 中断抽卡
                    throw new ManagedException(ManagedErrorCode.SOURCE_CARD_IS_EMPTY);
                }
            }
        }
    }

    /**
     * 丢弃随机卡牌
     *
     * @param denfenders
     * @param num
     */
    private void throwRandomCard(List<AbstractCreature> denfenders, Integer num) {
        for (AbstractCreature defender : denfenders) {
            AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(defender);
            List<MoWuNiang> source = trainer.getSourceCardStorage().getMwns();
            for (int i = 0; i < num; i++) {
                if (!source.isEmpty()) {
                    List<MoWuNiang> select = randomCard(source);
                    // 牌库移除卡牌
                    source.removeAll(select);
                    RoundFightUtils.trainerCardsChange(trainer, select, false, true, Reason.Operate_Card_Skill);
                    // 卡牌判定死亡
                    MWNCreature mwnCreature = MWNCreater.create(trainer, select.get(0), null);
                    mwnCreature.getLifeStats().reduceHp(mwnCreature.getCurrentHp(), null, 0, 0);
                    trainer.addMWN(mwnCreature);
                } else {
                    // 发放鬼牌
                    sendGhostCard(trainer);
                }
            }
        }
    }

    /**
     * 丢弃指定卡牌
     *
     * @param denfenders
     * @param num
     * @param mwnId
     * @param eleType
     */
    private void throwFixedCard(List<AbstractCreature> denfenders, Integer num, Integer mwnId, Integer eleType) {
        for (AbstractCreature defender : denfenders) {
            AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(defender);
            List<MoWuNiang> source = trainer.getSourceCardStorage().getMwns();
            for (int i = 0; i < num; i++) {
                if (!source.isEmpty()) {
                    List<MoWuNiang> required = requiredCard(source, mwnId, eleType);
                    if (!required.isEmpty()) {
                        List<MoWuNiang> select = randomCard(required);
                        // 牌库移除卡牌
                        source.removeAll(select);
                        RoundFightUtils.trainerCardsChange(trainer, select, false, true, Reason.Operate_Card_Skill);
                        // 卡牌判定死亡
                        MWNCreature mwnCreature = MWNCreater.create(trainer, select.get(0), null);
                        mwnCreature.getLifeStats().reduceHp(mwnCreature.getCurrentHp(), null, 0, 0);
                        trainer.addMWN(mwnCreature);
                    } else {
                        throw new ManagedException(ManagedErrorCode.NO_FIXED_CARD);
                    }
                } else {
                    // 中断抽卡
                    throw new ManagedException(ManagedErrorCode.SOURCE_CARD_IS_EMPTY);
                }
            }
        }
    }

    @Override
    public Map<String, Integer> initParam(SkillResource resource) {
        return JsonUtils.string2Map(resource.getParam(), String.class, Integer.class);
    }
}
