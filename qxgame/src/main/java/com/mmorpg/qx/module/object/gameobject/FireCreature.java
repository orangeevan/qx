package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.NpcController;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

/**
 * @author wang ke
 * @description: 恶魔•火
 * @since 19:58 2020-09-04
 */
public class FireCreature extends AbstractCreature {

    public FireCreature(long objId, NpcController controller, WorldPosition position, Effect effect) {
        super(objId, controller, position);
        this.lifeEndRound = effect.getEndRound();
        this.damage = effect.getValue();
        this.effectId = effect.getEffectResourceId();
    }

    //释放者
    private AbstractCreature owner;
    //生命时长
    private int lifeEndRound;
    //每回合造成伤害
    private int damage;
    //恶魔火效果id
    private int effectId;
    //当前扣血回合
    private int effectRound;

    @Override
    public ObjectType getObjectType() {
        return ObjectType.FIRE;
    }

    @Override
    public String getName() {
        return "恶魔•火";
    }

    public AbstractCreature getOwner() {
        return owner;
    }

    public void setOwner(AbstractCreature owner) {
        this.owner = owner;
    }

    public int getLifeEndRound() {
        return lifeEndRound;
    }

    public int getDamage() {
        return damage;
    }

    public int getEffectId() {
        return effectId;
    }

    public int getEffectRound() {
        return effectRound;
    }

    public void setEffectRound(int effectRound) {
        this.effectRound = effectRound;
    }

    public boolean hasEffected(int round) {
        return effectRound >= round;
    }
}
