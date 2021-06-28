package com.mmorpg.qx.module.object.controllers;

import com.haipaite.common.event.core.EventBusManager;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.configValue.ConfigValueManager;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.object.gameobject.event.TrainerJobOrEleAlterEvent;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 驯养师控制器
 * @since 19:48 2020-11-10
 */
public abstract class AbstractTrainerController<T extends AbstractTrainerCreature> extends AbstractCreatureController<AbstractTrainerCreature> {
    @Override
    public AbstractTrainerCreature getOwner() {
        return (AbstractTrainerCreature) super.getOwner();
    }

    public boolean onMwnJobOrEleChange(MWNCreature creature, Reason reason) {
        if (!creature.hasEleAttr() && !creature.hasJobAttr()) {
            return false;
        }
        AttrType eleAttrType = MWNService.getInstance().getEleAttrType(creature);
        AttrType jobAttrType = MWNService.getInstance().getJobAttrType(creature);
        List<Attr> newJobOrEleAttr = RoundFightService.getInstance().mwnAddTrainerAttrs(getOwner());
        int eleAttrBefore = getOwner().getAttrController().getCurrentAttr(eleAttrType);
//        StringBuilder builder = new StringBuilder();
//        newJobOrEleAttr.forEach(builder::append);
//        System.err.println("驯养师: " + getOwner().getObjectId() + " 当前职业元素属性：" + builder.toString());
        getOwner().getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.MWN_ATTR_JOB_ELE_CHANGE), newJobOrEleAttr, true, true);
        EventBusManager.getInstance().syncSubmit(TrainerJobOrEleAlterEvent.valueOf(getOwner(), eleAttrType, jobAttrType, reason, eleAttrBefore));
        return true;
    }

    /**
     * 投骰子触发
     */
    public void onThrowDice() {
        getOwner().handleEffect(TriggerType.Dice_Point, getOwner());
    }

    /**
     * 驯养师退出房间
     */
    public void onExitRoom() {
        getOwner().setRoom(null);
        getOwner().clearAllMwn();
    }


    /**
     * 移动完业务，此类业务应放在MOVE_END阶段去处理，
     * 为了保持跟前端一致，暂时放在MOVE阶段结束阶段处理
     */
    public void onMoveEnd() {
        AbstractTrainerCreature trainer = getOwner();
        trainer.resetDicePoint();
        //清除此次移动所有状态及数据
        trainer.getMoveController().stopMoving();
        //停留格有建筑，获取金币
        WorldPosition position = trainer.getPosition();
        AbstractBuilding building = trainer.getRoom().getWorldMapInstance().findBuildByGid(position.getGridId());
        if (Objects.nonNull(building)) {
            int gold = building.getResource().getGold();
            if (gold > 0) {
                trainer.getLifeStats().increaseGold(gold, Reason.Building, true);
            }
        }
        //停留格有己方魔物娘给金币
        MWNCreature mwn = trainer.getRoom().getWorldMapInstance().getMWNByGridId(position.getGridId());
        if (RelationshipUtils.judgeRelationship(trainer, mwn, RelationshipUtils.Relationships.SELF_TRAINER_MWN) && !mwn.isCallRound(trainer.getRoom().getRound())) {
            Optional<Integer> goldValue = Optional.of(ConfigValueManager.getInstance().getIntConfigValue("PassMwnAddGold"));
            if (goldValue.isPresent() && goldValue.get() > 0) {
                trainer.getLifeStats().increaseGold(goldValue.get(), Reason.Self_Mwn_Position, true);
            }
        }
        //挑衅
        //抽取手牌卡
        List<MoWuNiang> filterMwns = trainer.getUseCardStorage().getMwns().stream().filter(moWuNiang -> moWuNiang.getResource().getCostMp() <= trainer.getLifeStats().getCurrentMp()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterMwns)) {
            return;
        }

        MoWuNiang moWuNiang = filterMwns.stream().sorted(Comparator.comparingInt(o -> o.getResource().getCostMp())).limit(1).findFirst().get();
        //最终停留格有敌方魔物娘，没有召唤魔物娘战斗将受到敌方魔物娘对我方驯养师伤害
        if (mwn != null && moWuNiang != null && RelationshipUtils.judgeRelationship(trainer, mwn, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)) {
            boolean isProvocation = mwn.getEffectController().isInStatus(EffectStatus.Provocation);
            boolean islianxi = trainer.getFightRScount() == 1 && mwn.getEffectController().isInStatus(EffectStatus.Lianxi);
            if (isProvocation || islianxi) {
                RoundFightService.getInstance().callMWN(trainer, moWuNiang.getId(), trainer.getGridId());
            }
        }
    }

    @Override
    public void delete() {
        //nothing
    }
}
