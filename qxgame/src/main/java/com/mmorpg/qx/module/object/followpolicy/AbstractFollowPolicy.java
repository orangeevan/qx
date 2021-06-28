package com.mmorpg.qx.module.object.followpolicy;

import com.mmorpg.qx.module.object.gameobject.NpcCreature;

public abstract class AbstractFollowPolicy implements IFollowPolicy {

	protected final NpcCreature owner;

	protected AbstractFollowPolicy(NpcCreature owner) {
		this.owner = owner;
	}

	public NpcCreature getOwner() {
		return owner;
	}

	@Override
	public boolean outWarning(int tx, int ty) {
//		int warnrange = owner.getWarnrange();
//		int[] bornXY = getBornXY();
//
//		int distance = GameMathUtil.getGridDistance(tx, ty, bornXY[0], bornXY[1]);
//		if (distance > warnrange) {
//			return true;
//		}

		return false;
	}

	@Override
	public boolean tooFarFromHome(int tx, int ty) {
		//卡招游戏不需要做怪物AI行为
//		int homeRange = owner.getHomeRange();
//		int[] bornXY = getBornXY();
//
//		int distance1 = GameMathUtil.getGridDistance(owner.getX(), owner.getY(), bornXY[0], bornXY[1]);
//		int distance2 = GameMathUtil.getGridDistance(tx, ty, bornXY[0], bornXY[1]);
//		if (distance1 > homeRange || distance2 > homeRange) {
//			return true;
//		}

		return false;
	}

//	protected abstract int[] getBornXY();
}
