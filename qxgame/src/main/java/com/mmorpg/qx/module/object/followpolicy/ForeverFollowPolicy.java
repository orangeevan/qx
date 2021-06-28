package com.mmorpg.qx.module.object.followpolicy;

import com.mmorpg.qx.module.object.gameobject.NpcCreature;

public class ForeverFollowPolicy extends AbstractFollowPolicy {

	public ForeverFollowPolicy(NpcCreature owner) {
		super(owner);
	}

	@Override
	public boolean tooFarFromHome(int tx, int ty) {
		return false;
	}

//	@Override
//	protected int[] getBornXY() {
//		return new int[] { owner.getBornX(), owner.getBornY() };
//	}

}
