package com.mmorpg.qx.module.object.controllers.attack;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * 仇恨信息
 * 
 * @author wang ke
 * @since v1.0 2018年2月25日
 *
 */
public class AggroInfo {
	private AbstractCreature attacker;
	private long hate;
	private long damage;

	/**
	 * @param attacker
	 */
	AggroInfo(AbstractCreature attacker) {
		this.attacker = attacker;
	}

	/**
	 * @return attacker
	 */
	public AbstractCreature getAttacker() {
		return attacker;
	}

	/**
	 * @param damage
	 */
	public void addDamage(long damage) {
		this.damage += damage;
		if (this.damage < 0) {
			this.damage = 0;
		}
	}

	/**
	 * @param damage
	 */
	public void addHate(long damage) {
		this.hate += damage;
		if (this.hate < 1) {
			this.hate = 1;
		}
	}

	/**
	 * @return hate
	 */
	public long getHate() {
		return this.hate;
	}

	/**
	 * @param hate
	 */
	public void setHate(long hate) {
		this.hate = hate;
	}

	/**
	 * @return damage
	 */
	public long getDamage() {
		return this.damage;
	}

	/**
	 * @param damage
	 */
	public void setDamage(long damage) {
		this.damage = damage;
	}
}
