package com.mmorpg.qx.module.object.followpolicy;

import com.mmorpg.qx.module.object.gameobject.NpcCreature;

public class MasterFollowPolicy extends AbstractFollowPolicy {

	public MasterFollowPolicy(NpcCreature owner) {
		super(owner);
	}

//	@Override
//	protected int[] getBornXY() {
//		Summon summon = (Summon) owner;
//		Player master = summon.getMaster();
//		return new int[] { master.getX(), master.getY() };
//	}
}
