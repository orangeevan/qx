package com.mmorpg.qx.module.object.followpolicy;

import com.mmorpg.qx.module.object.gameobject.NpcCreature;

public class StateFollowPolicy extends AbstractFollowPolicy {

	public StateFollowPolicy(NpcCreature owner) {
		super(owner);
	}

//	@Override
//	protected int[] getBornXY() {
//		return new int[] { owner.getBornX(), owner.getBornY() };
//	}
}
