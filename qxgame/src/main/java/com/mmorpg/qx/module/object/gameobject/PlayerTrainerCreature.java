package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.controllers.PlayerTrainerController;
import com.mmorpg.qx.module.object.controllers.packet.PlayerTrainerInfoResp;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.player.model.LoginPosition;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.trainer.manager.TrainerManager;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.trainer.resource.PlayerTrainerResource;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 地图上玩家对象
 */
public class PlayerTrainerCreature extends AbstractTrainerCreature {

    private Player owner;

    /**
     * 下次登陆时登陆位置标记
     */
    //TODO 卡招后面如果pvp玩法玩家在场景退出，下次登录需要恢复坐标
    private LoginPosition loginPosition;

    private PlayerTrainer trainer;

    public PlayerTrainerCreature(long objId, PlayerTrainerController controller, WorldPosition position, PlayerTrainer trainer, int... selectSkill) {
        super(objId, controller, position);
        controller.setOwner(this);
        this.trainer = trainer;
        initSKill(selectSkill);
    }

    private void initSKill(int... selectSkill) {
        /** 驯养师配置表技能都是天赋技能，直接生效*/
        PlayerTrainerResource trainerResource = TrainerManager.getInstance().getPlayerTrainerResource(trainer.getResourceId());
        //List<Integer> skills = trainerResource.getSkill();
        if (selectSkill == null || selectSkill.length == 0) {
            throw new IllegalArgumentException("驯养师技能初始化异常");
        }
        List<SkillResource> initTrainerSkills = new ArrayList<>();
        Arrays.stream(selectSkill).forEach(skillId -> {
            SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
            initTrainerSkills.add(skillResource);
            if (skillResource.isPassive()) {
                List<Integer> effects = skillResource.getEffects();
                if (!CollectionUtils.isEmpty(effects)) {
                    for (int effectId : effects) {
                        Effect effect = new Effect(this, this, SkillManager.getInstance().getEffectResource(effectId).getEffectType().create(), skillId, effectId,
                                Target.valueOf(this.getGridId(), this.getObjectId()));
                        this.getEffectController().addEffect(effect);
                    }
                }
            }

        });
        /** 初始化技能配置*/
        //Collection<SkillResource> initTrainerSkills = SkillManager.getInstance().getSkillByReleaseType(SkillReleaseType.Trainer_Active_Skill);
        if (!CollectionUtils.isEmpty(initTrainerSkills)) {
            initTrainerSkills.forEach(skill -> this.getSkillController().addSkill(skill.getSkillId()));
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.PLAYER_TRAINER;
    }

    @Override
    public String getName() {
        return owner.getPlayerEnt().getName();
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public LoginPosition getLoginPosition() {
        return loginPosition;
    }

    public void setLoginPosition(LoginPosition loginPosition) {
        this.loginPosition = loginPosition;
    }


    @Override
    public List<Attr> getInitAttrList() {
        return Collections.unmodifiableList(trainer.getAttrList());
    }

    public PlayerTrainerInfoResp getInfo() {
        PlayerTrainerInfoResp resp = new PlayerTrainerInfoResp();
        resp.setResourceId(this.trainer.getResourceId());
        resp.setName(owner.getPlayerEnt().getName());
        resp.setPlayerId(owner.getPlayerEnt().getPlayerId());
        resp.setId(getObjectId());
        resp.setRole(owner.getPlayerEnt().getRole());
        resp.setSex(owner.getPlayerEnt().getSex());
        List<SkillVo> skillVo = this.getSkillController().getSkillVo();
        resp.setSkills(skillVo);
        resp.setAttrs(this.getAttrController().getAttrVos());
        resp.setCurHp(this.getCurrentHp());
        resp.setCurMp(this.getLifeStats().getCurrentMp());
        resp.setGold(this.getLifeStats().getCurrentGold());
        resp.setEffects(getEffectController().toEffectVo());
        resp.setDirType(this.getDir().getDir());
        resp.setSkinId(this.trainer.getSkinResourceId());
        resp.setGridId(this.getGridId());
        AbstractTrainerCreature firstOp = this.getRoom().getFirstOp();
        if (Objects.nonNull(firstOp)) {
            resp.setFirstOp(firstOp.getObjectId());
        }
//        StringBuilder builder = new StringBuilder();
//        resp.getAttrs().stream().forEach(attrVo -> {
//            builder.append(attrVo);
//        });
//        System.err.println("驯养师: "+this.getObjectId()+" 同步属性："+builder.toString());
        return resp;
    }

    @Override
    public void initAttr() {
            super.initAttr();
        //TODO  方便前端测试，后续删除
//        {
//            this.getLifeStats().increaseGold(10000, Reason.Attr_Change, false);
//            List<Attr> debugAttr = new ArrayList<>();
//            AttrType.getAllEleType().stream().forEach(type -> {
//                debugAttr.add(Attr.valueOf(type, 10));
//            });
//            AttrType.getAllJobType().stream().forEach(type -> {
//                debugAttr.add(Attr.valueOf(type, 10));
//            });
//            debugAttr.add(Attr.valueOf(AttrType.Max_Mp, 10));
//            this.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.DEBUG), debugAttr, true, false);
//        }
    }

    /**
     * 战场驯养师是否归属玩家
     *
     * @param player
     * @return
     */
    public boolean isOwner(Player player) {
        return this.owner.equals(player);
    }

    /**
     * 驯养师
     *
     * @return
     */
    public PlayerTrainer getTrainer() {
        return trainer;
    }
}
