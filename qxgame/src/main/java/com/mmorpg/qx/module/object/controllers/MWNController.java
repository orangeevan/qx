package com.mmorpg.qx.module.object.controllers;

import com.haipaite.common.event.core.EventBusManager;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.controllers.event.MWNDeadEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MWNController extends AbstractCreatureController<MWNCreature> {

    @Override
    public MWNCreature getOwner() {
        return (MWNCreature) super.getOwner();
    }

    @Override
    public void onDie(AbstractCreature lastAttacker, int skillId, int effectId) {
        super.onDie(lastAttacker, skillId, effectId);
        if (RelationshipUtils.isMWN(lastAttacker)) {
            this.getOwner().handleEffect(TriggerType.DIE_MWN_MWN, lastAttacker);
        }
        this.getOwner().handleEffect(TriggerType.DIE_MWN, this.getOwner());
        if (skillId > 0) {
            SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
            if (skillResource != null && skillResource.getSkillReleaseType() > 0 && skillResource.getSkillReleaseType() <= 100) {
                this.getOwner().handleEffect(TriggerType.DIE_BY_SKILL_EFFECT, lastAttacker);
            }
        }
        MWNCreature owner = getOwner();
        owner.getMaster().getRoom().recordMwnDead(owner);
        owner.setTarget(null);
        owner.resetExile();
        WorldMapInstance worldMapInstance = this.getOwner().getWorldMapInstance();
        WorldPosition position = getOwner().getPosition();
        if (RelationshipUtils.isMWN(lastAttacker)) {
            if (lastAttacker.getPosition() == null) {
                lastAttacker.setPosition(position);
            }
            MWNController controller = (MWNController) RelationshipUtils.toMWNCreature(lastAttacker).getController();
            controller.onKill(this.getOwner(), worldMapInstance);
        }
        //???????????????????????????????????????????????????????????????????????????????????????
        AbstractTrainerCreature master = this.getOwner().getMaster();
        if (lastAttacker == master) {
            master.setReplaceSpawn(true);
        }
        Reason reason = lastAttacker == master ? Reason.Mwn_Replace : Reason.Mwn_Fight_Fail;
        //?????????????????????????????????????????????????????????
        if (master.getRoom().isAttackMWN(owner) || master.getRoom().isDefenseMWN(owner)) {
            if (RelationshipUtils.isMWN(lastAttacker) && lastAttacker.isAlreadyDead()) {
                reason = Reason.Mwn_Replace;
            }
        }
        //???????????????????????????????????????????????????
        if (MWNService.getInstance().deadRemoveTrainerEleAttr(getOwner())) {
            master.getController().onMwnJobOrEleChange(getOwner(), reason);
        } else {
            MWNService.getInstance().sendMwnEvoOrOcc(null, 0, master, reason, null);
        }
        //??????????????????
        EventBusManager.getInstance().syncSubmit(MWNDeadEvent.valueOf(getOwner(), lastAttacker));
        //this.getOwner().getMaster().getSummons().remove(owner.getObjectId());
        //?????????????????????????????????????????????????????????,??????????????????????????????
        if (getOwner().getEffectController().hasNoEffects()) {
            delete();
        }
    }

    /***
     * ??????????????????????????????????????????
     * @param beKilled
     * @param mapInstance
     */
    public void onKill(AbstractCreature beKilled, WorldMapInstance mapInstance) {
        if (!RelationshipUtils.isMWN(beKilled)) {
            return;
        }
        //this.getOwner().handleEffect(TriggerType.MWN_FIGHT_WIN, beKilled);
    }

    @Override
    public void onAttack(AbstractCreature creature, DamageSkillResult damage) {
        super.onAttack(creature, damage);
        if (getOwner().isAlreadyDead()) {
            return;
        }
        //?????????????????????????????????
        if (RelationshipUtils.isMWN(creature)) {
            RelationshipUtils.toMWNCreature(creature).addHatredMwn(RelationshipUtils.toMWNCreature(getOwner()));
        }
        List<Effect> effects = creature.getEffectController().getEffects();
        effects.stream().forEach(e -> {
            if (e.getEffectResource().isRemoveBeAttack()) {
                creature.getEffectController().removeEffect(e.getEffectResourceId());
            }
        });
    }

    @Override
    public void see(AbstractVisibleObject object) {
        super.see(object);
        this.getOwner().handleEffect(TriggerType.SEE, getOwner());
    }

    @Override
    public void notSee(AbstractVisibleObject object, boolean isOutOfRange) {
        super.notSee(object, isOutOfRange);
//        if (object instanceof AbstractCreature) {
//            getOwner().getAggroList().remove((AbstractCreature) object);
//        }
//        if (object.getObjectType() == ObjectType.PLAYER) {
//            getOwner().getAI().handleEvent(Event.NOT_SEE_PLAYER, object);
//        }

    }


    @Override
    public void onDespawn() {
        super.onDespawn();
    }

    @Override
    public void delete() {
        super.delete();
        MWNCreature owner = getOwner();
        if (owner == null) {
            return;
        }
        owner.getEffectedList().clear();
        List<MWNCreature> assistedMWN = owner.getAssistedMWN();
        if (!CollectionUtils.isEmpty(assistedMWN)) {
            assistedMWN.clear();
        }
        List<MWNCreature> beAssistedMWN = owner.getBeAssistedMWN();
        if (!CollectionUtils.isEmpty(beAssistedMWN)) {
            beAssistedMWN.clear();
        }

    }

    @Override
    public void onSpawn(int mapId, int instanceId) {
        if (getOwner() == null) {
            return;
        }
        super.onSpawn(mapId, instanceId);
        PacketSendUtility.broadcastInWorldMap(getOwner(), getOwner().getInfo(), true);
        this.getOwner().handleEffect(TriggerType.SPAWN, getOwner());
        //?????????????????????????????????????????????????????????????????????????????????????????????
        AbstractTrainerCreature master = this.getOwner().getMaster();
        //????????????????????????????????????????????????????????????????????????????????????????????????
        //?????????????????????????????????????????????????????????????????????????????????
        List<MWNCreature> mwnList = getOwner().getWorldMapInstance().getMWNList(getOwner().getGridId());
        if (!CollectionUtils.isEmpty(mwnList) && mwnList.size() < 2) {
            Reason reason = getOwner().getMaster().isReplaceSpawn() ? Reason.Mwn_Replace : Reason.Call_Mwn;
            if (!RoundFightService.getInstance().hasMwnCalled(master, getOwner()) || MWNService.getInstance().deadRemoveTrainerEleAttr(getOwner())) {
                if (!master.getController().onMwnJobOrEleChange(getOwner(), reason)) {
                    MWNService.getInstance().sendMwnEvoOrOcc(null, 0, master, reason, null);
                }
            } else {
                // ????????????????????? && ??????????????????????????????
                MWNService.getInstance().sendMwnEvoOrOcc(null, 0, master, reason, null);
            }
            getOwner().getMaster().setReplaceSpawn(false);
        }
    }

    @Override
    public void onAddDamage() {
        super.onAddDamage();
        // getOwner().getAI().handleEvent(CZEvent.ATTACKED, this.getOwner(), null);
    }
}
