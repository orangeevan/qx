package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.module.ai.event.Event;
import com.mmorpg.qx.module.object.controllers.attack.AggroList;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;

/**
 * NPC控制器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class NpcController extends AbstractCreatureController<NpcCreature> {


    @Override
    public NpcCreature getOwner() {
        return (NpcCreature) super.getOwner();
    }

    @Override
    public void onAttack(AbstractCreature creature, DamageSkillResult damage) {
        super.onAttack(creature, damage);
        if (getOwner().isAlreadyDead()) {
            return;
        }
        if (creature instanceof MWNCreature) {
            MWNCreature servant = (MWNCreature) creature;
            getOwner().getAggroList().addDamage(servant.getMaster(), damage.getValue());
            getOwner().addDamage(servant.getMaster(), damage.getValue());
        } else {
            getOwner().getAggroList().addDamage(creature, damage.getValue());
            getOwner().addDamage(creature, damage.getValue());
        }
    }

    synchronized protected void broadDamage() {

    }

    @Override
    public void see(AbstractVisibleObject object) {
        super.see(object);

//        NpcCreature owner = getOwner();
//
//        if (object.getObjectType() == ObjectType.PLAYER) {
//            owner.getAI().handleEvent(CZEvent., object);
//        }
//        getOwner().getObserveController().notifySeeObservers(object);
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

    protected void doReward(AbstractCreature lastAttacker, AggroList aggroList) {
        if (!(getOwner() instanceof MonsterCreature)) {
            return;
        }
        // 获取伤害第一的人
        AbstractTrainerCreature mostDamagePlayer = getOwner().getDamageRank().get(1);
        if (mostDamagePlayer == null) {
            // 这个应该不可能发生
            switch (lastAttacker.getObjectType()) {
                case PLAYER_TRAINER:
                    mostDamagePlayer = (PlayerTrainerCreature) lastAttacker;
                    break;
                case MWN:
                    mostDamagePlayer = ((MWNCreature) lastAttacker).getMaster();
                    break;
                default:
                    return;
            }
        }

        if (mostDamagePlayer == null) {
            return;
        }
    }

    @Override
    public void onDespawn() {
        NpcCreature owner = getOwner();
        if (owner == null) {
            return;
        }
        getOwner().getDamages().clear();
    }

    @Override
    public void delete() {
        NpcCreature owner = getOwner();
        if (owner == null) {
            return;
        }
        owner.getAI().stop(this.getOwner());
        getOwner().getAggroList().clear();
        getOwner().getDamages().clear();
        getOwner().getMoveController().stopMoving();
        getOwner().getEffectController().removeAllEffects();
        //if (getOwner().isSpawned()) {
        getOwner().getWorldMapInstance().removeObject(getOwner());
        //}
        super.delete();
    }

    @Override
    public void onSpawn(int mapId, int instanceId) {
        NpcCreature owner = getOwner();
        if (owner == null) {
            return;
        }
        //owner.getAI().handleEvent(Event.BIRTH, null);
        // 身上加一个光环
		/*
		if (owner.getObjectResource().getAuraSkillId() != 0) {
			int skillId = owner.getObjectResource().getAuraSkillId();
			if (!owner.getEffectController().containsSkill(skillId)) {
				Target target = new Target();
				SkillManager.getInstance().npcUseSkill(skillId, owner, target);
			}
		}
		*/

        super.onSpawn(mapId, instanceId);
    }

    @Override
    public void onAddDamage() {
        super.onAddDamage();
        // getOwner().getAI().handleEvent(CZEvent.ATTACKED, this.getOwner(), null);
    }

    @Override
    public void onDie(AbstractCreature lastAttacker, int skillId, int effectId) {
        super.onDie(lastAttacker, skillId, effectId);
    }

    @Override
    public void onAddHate() {
        super.onAddHate();
    }

    public void onAtSpawnLocation() {

    }

    @Override
    public void onBeAttacked(AbstractCreature attacker) {
        super.onBeAttacked(attacker);
        getOwner().getAI().handleEvent(Event.ATTACKED, getOwner(), attacker);
    }

    public void onFightOff() {
        getOwner().getAggroList().clear();
        getOwner().getDamages().clear();
    }

}
