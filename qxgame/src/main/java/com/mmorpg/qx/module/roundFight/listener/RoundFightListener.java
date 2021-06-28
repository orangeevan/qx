package com.mmorpg.qx.module.roundFight.listener;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.roundFight.event.*;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.skill.event.UseSkillEvent;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:59 2021/4/7
 */
@Component
public class RoundFightListener {

    @ReceiverAnno
    public void onCallMwnTriggerIndex(PlayerCallMwnEvent event) {
        AbstractCreature creature = event.getCreature();
        if (!creature.isObjectType(ObjectType.MWN)) {
            return;
        }
        creature.handleEffect(TriggerType.CALL_MWN_INDEX, creature, event.getIndex());
    }

    @ReceiverAnno
    public void onCallMwnTriggerGridPos(PlayerCallMwnEvent event) {
        AbstractCreature creature = event.getCreature();
        if (!creature.isObjectType(ObjectType.MWN)) {
            return;
        }
        creature.handleEffect(TriggerType.CALL_MWN_GRID_POS, creature);

    }

    @ReceiverAnno
    public void onCallMwnTriggerSense(PlayerCallMwnEvent event) {
        AbstractCreature creature = event.getCreature();
        if (!creature.isObjectType(ObjectType.MWN)) {
            return;
        }
        Collection<MWNCreature> mwns = creature.getWorldMapInstance().findMWN();
        for (MWNCreature mwn : mwns) {
            RelationshipUtils.Relationships ship = RelationshipUtils.Relationships.FRIEND_MWN_MWN;
            if (!RelationshipUtils.judgeRelationship(mwn, creature, ship)) {
                continue;
            }
            mwn.handleEffect(TriggerType.CALL_MWN_SENSE, creature);
        }

    }

    @ReceiverAnno
    public void onCallMwn(PlayerCallMwnEvent event) {
        AbstractCreature creature = event.getCreature();
        if (!creature.isObjectType(ObjectType.MWN)) {
            return;
        }
        creature.handleEffect(TriggerType.CALL_MWN, creature);
        //诅咒
        Collection<AbstractVisibleObject> objects = creature.getWorldMapInstance().findObjects(creature.getGridId());
        for (AbstractVisibleObject object : objects) {
            if (!(object instanceof CurseCreature)) {
                continue;
            }
            CurseCreature curse = (CurseCreature) object;
            creature.getLifeStats().reduceHp(curse.getHurt(), curse, Reason.Curse, true);
        }
    }

    @ReceiverAnno
    public void onMwnReplace(PlayerMwnReplaceEvent event) {
        AbstractCreature creature = event.getOnCreature();
        if (!creature.isObjectType(ObjectType.MWN)) {
            return;
        }
        creature.handleEffect(TriggerType.REPLACE, creature);
    }

    @ReceiverAnno
    public void onMwnFightBefore(MwnFightBeforeEvent event) {
        AbstractCreature attacker = event.getAttacker();
        AbstractCreature defender = event.getDefender();
        if (!attacker.isObjectType(ObjectType.MWN) || !defender.isObjectType(ObjectType.MWN)) {
            return;
        }
        attacker.handleEffect(TriggerType.MWN_BEFORE_FIGHT, attacker);
        attacker.handleEffect(TriggerType.MWN_BEFORE_FIGHT_ATTACKER, attacker);
        attacker.handleEffect(TriggerType.MWN_BEFORE_FIGHT_ENEMY, attacker);
        defender.handleEffect(TriggerType.MWN_BEFORE_FIGHT_DEFENDER, defender);
        defender.handleEffect(TriggerType.MWN_BEFORE_FIGHT_DEFENDER, defender);
        defender.handleEffect(TriggerType.MWN_BEFORE_FIGHT_ENEMY, defender);

    }

    @ReceiverAnno
    public void onMwnFightAfter(MwnFightAfterEvent event) {
        AbstractCreature winner = event.getWinner();
        AbstractCreature loser = event.getLoser();
        if (!winner.isObjectType(ObjectType.MWN) || !loser.isObjectType(ObjectType.MWN)) {
            return;
        }

        winner.handleEffect(TriggerType.MWN_FIGHT_WIN, winner);
        loser.handleEffect(TriggerType.MWN_FIGHT_LOSE, loser);
    }

    @ReceiverAnno
    public void onRoundStageChange(RoundStageChangeEvent event) {
        Room room = event.getRoom();
        AbstractTrainerCreature trainerCreature = room.getCurrentTurn();
        trainerCreature.handleEffect(TriggerType.ENTER_ROUND_STAGE, trainerCreature);
        Collection<AbstractTrainerCreature> creatures = room.getWorldMapInstance().getAllTrainer();
        //敌方进入状态
        for (AbstractTrainerCreature c : creatures) {
            if (c.getObjectId().equals(trainerCreature.getObjectId())) {
                continue;
            }
            if (!RelationshipUtils.judgeRelationship(c, trainerCreature, RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER)) {
                continue;
            }
            c.handleEffect(TriggerType.ENEMY_ENTER_ROUND_STAGE, trainerCreature);
            Collection<MWNCreature> mwns = c.getMWN(true);
            for (MWNCreature mwn : mwns) {
                mwn.handleEffect(TriggerType.ENEMY_ENTER_ROUND_STAGE, trainerCreature);
            }
        }

    }

    @ReceiverAnno
    public void onUseSkill(UseSkillEvent event) {
        AbstractCreature creature = event.getSkill().getSkillCaster();
        List<Effect> effects = creature.getEffectController().getEffects();
        for (Effect e : effects) {
            EffectResource resource = e.getEffectResource();
            if (resource.isRemoveUseSkill()) {
                creature.getEffectController().removeEffect(e.getEffectResourceId());
            }
        }
        if (RelationshipUtils.isMWN(creature)) {
            Collection<MWNCreature> mwns = creature.getWorldMapInstance().findMWN();
            for (MWNCreature mwn : mwns) {
                mwn.handleEffect(TriggerType.ENCOURAGE, mwn);
            }
            creature.addUseSkillRS();
            creature.handleEffect(TriggerType.USE_SKILL_ROUND, creature);
        }
    }

    @ReceiverAnno
    public void onEncourage(UseSkillEvent event) {
        AbstractCreature creature = event.getSkill().getSkillCaster();
        if (RelationshipUtils.isMWN(creature)) {
            Collection<MWNCreature> mwns = creature.getWorldMapInstance().findMWN();
            for (MWNCreature mwn : mwns) {
                mwn.handleEffect(TriggerType.ENCOURAGE, mwn);
            }
        }
    }

}

