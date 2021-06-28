package com.mmorpg.qx.module.object.gameobject.attr;

/**
 * @author wang ke
 * @description:属性配置表
 * @since 10:51 2020-11-10
 */
public class AttrResource {
    /**
     * 生命值
     */
    public int max_hp;

    /**
     * 魔法
     */
    public int max_mp;

    /**
     * 攻击力
     */
    public int attack;

    /**
     * 闪避加成
     */
    public int miss;

    /**
     * 暴击加成
     */
    public int crit_prob;

    /**
     * 免伤加成
     */
    public int harm_reduce_rate;

    /**
     * 护甲
     */
    public int defend;

    /**
     * 武力值加成
     */
    public int trainer_harm;

    /**
     * 吸血
     */
    public int suck_blood;

    /**
     * 命中率
     */
    public int hit_rate;

    /**
     * 添加敌方暴击率
     */
    public int add_eny_crit;

    /**
     * 脸朝向领域半径
     */
    public int Attack_Range_Face;

    /**
     * 垂直方向朝向领域半径
     */
    public int Attack_Range_Face_Ver;


    public int getMax_hp() {
        return max_hp;
    }

    public void setMax_hp(int max_hp) {
        this.max_hp = max_hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getMiss() {
        return miss;
    }

    public void setMiss(int miss) {
        this.miss = miss;
    }

    public int getCrit_prob() {
        return crit_prob;
    }

    public void setCrit_prob(int crit_prob) {
        this.crit_prob = crit_prob;
    }

    public int getHarm_reduce_rate() {
        return harm_reduce_rate;
    }

    public void setHarm_reduce_rate(int harm_reduce_rate) {
        this.harm_reduce_rate = harm_reduce_rate;
    }

    public int getDefend() {
        return defend;
    }

    public void setDefend(int defend) {
        this.defend = defend;
    }

    public int getTrainer_harm() {
        return trainer_harm;
    }

    public void setTrainer_harm(int trainer_harm) {
        this.trainer_harm = trainer_harm;
    }

    public int getSuck_blood() {
        return suck_blood;
    }

    public void setSuck_blood(int suck_blood) {
        this.suck_blood = suck_blood;
    }

    public int getHit_rate() {
        return hit_rate;
    }

    public void setHit_rate(int hit_rate) {
        this.hit_rate = hit_rate;
    }

    public int getAdd_eny_crit() {
        return add_eny_crit;
    }

    public void setAdd_eny_crit(int add_eny_crit) {
        this.add_eny_crit = add_eny_crit;
    }

    public int getMax_mp() {
        return max_mp;
    }

    public void setMax_mp(int max_mp) {
        this.max_mp = max_mp;
    }

    public int getAttack_Range_Face() {
        return Attack_Range_Face;
    }

    public void setAttack_Range_Face(int attack_Range_Face) {
        Attack_Range_Face = attack_Range_Face;
    }

    public int getAttack_Range_Face_Ver() {
        return Attack_Range_Face_Ver;
    }

    public void setAttack_Range_Face_Ver(int attack_Range_Face_Ver) {
        Attack_Range_Face_Ver = attack_Range_Face_Ver;
    }
}
