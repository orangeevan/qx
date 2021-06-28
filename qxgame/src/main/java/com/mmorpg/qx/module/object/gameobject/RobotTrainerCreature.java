package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.ai.AbstractAI;
import com.mmorpg.qx.module.ai.aitype.AIType;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.AbstractCreatureController;
import com.mmorpg.qx.module.object.controllers.packet.RobotTrainerInfoResp;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.trainer.resource.AITrainerResource;
import com.mmorpg.qx.module.troop.model.CardBag;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description: 陪玩家对弈召唤师
 * @since 13:48 2020-07-30
 */
public class RobotTrainerCreature extends AbstractTrainerCreature {

    private AbstractAI ai;

    private AITrainerResource aiTrainerResource;

    public RobotTrainerCreature(long objId, AbstractCreatureController<? extends AbstractCreature> controller, WorldPosition position, AITrainerResource resource) {
        super(objId, controller, position);
        controller.setOwner(this);
        init(resource);
    }

    public void init(AITrainerResource resource) {
        /**机器人卡包*/
        CardBag cardBagStorage = new CardBag();
        cardBagStorage.setOwner(this);
        Map<Integer, Integer> cards = resource.getCards();
        if (!CollectionUtils.isEmpty(cards)) {
            cards.entrySet().stream().forEach(entry -> {
                MWNResource mwnById = MWNManager.getInstance().getMWNResource(entry.getKey());
                int num = 1;
                while (num < entry.getValue()) {
                    MoWuNiang moWuNiang = new MoWuNiang();
                    moWuNiang.setId(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MOWUNIANG));
                    moWuNiang.setResourceId(mwnById.getId());
                    //moWuNiang.setAttrList(mwnById.getBaseAttrsList());
                    cardBagStorage.addMwn(moWuNiang);
                    num++;
                }
            });
        } else {// TODO 没有配置卡包直接添加
            MWNManager.getInstance().getAllMwnResource().stream().limit(cardBagStorage.getSize()).forEach(mwnById -> {
                MoWuNiang moWuNiang = new MoWuNiang();
                moWuNiang.setId(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MOWUNIANG));
                moWuNiang.setResourceId(mwnById.getId());
                //moWuNiang.setAttrList(mwnById.getBaseAttrsList());
                cardBagStorage.addMwn(moWuNiang);
            });
        }
        this.setSourceCardStorage(cardBagStorage);
        CardBag useCardBag = new CardBag(ItemManager.getInstance().getRoundMaxCardNum());
        useCardBag.setOwner(this);
        this.setUseCardStorage(useCardBag);
        ai = AIType.getAI(resource.getAiType());
        List<Integer> skill = resource.getSkill();
        if (!CollectionUtils.isEmpty(skill)) {
            skill.stream().forEach(skillId -> {
                getSkillController().addSkill(skillId);
            });
        }
        this.aiTrainerResource = resource;
        if (resource.getInitEffect() > 0) {
            EffectResource effectResource = SkillManager.getInstance().getEffectResource(resource.getInitEffect());
            Effect effect = new Effect(this, this, effectResource.getEffectType().create(), 0, effectResource.getId(), Target.valueOf(this.getGridId(), this.getObjectId()));
            //this.getEffectController().addEffect(effect);
            effect.addToEffectedController();
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ROBOT_TRAINER;
    }

    @Override
    public String getName() {
        return aiTrainerResource.getName();
    }

    public RobotTrainerCreature(long objId, AbstractCreatureController<? extends AbstractCreature> controller, WorldPosition position, AbstractAI ai) {
        super(objId, controller, position);
        this.ai = ai;
    }

    public RobotTrainerInfoResp getRobotTrainerInfo() {
        RobotTrainerInfoResp resp = new RobotTrainerInfoResp();
        resp.setTrainerId(this.getObjectId());
        resp.setDirType(this.getDir().getDir());
        resp.setGridId(this.getGridId());
        resp.setName(this.getName());
        List<SkillVo> skillVo = this.getSkillController().getSkillVo();
        resp.setSkills(skillVo);
        resp.setAttrs(this.getAttrController().getAttrVos());
        resp.setEffects(this.getEffectController().toEffectVo());
        resp.setCurHp(getCurrentHp());
        resp.setCurMp(getLifeStats().getCurrentMp());
        resp.setSkinId(this.aiTrainerResource.getSkinId());
        resp.setResourceId(this.aiTrainerResource.getModelId());
        return resp;
    }

    public AbstractAI getAi() {
        return ai;
    }

    /***
     * 卡招AI没有状态，可以实时切换
     * @param ai
     */
    public void setAi(AbstractAI ai) {
        this.ai = ai;
    }

    @Override
    public List<Attr> getInitAttrList() {
        return aiTrainerResource.getBaseAttrsList();
    }
}
