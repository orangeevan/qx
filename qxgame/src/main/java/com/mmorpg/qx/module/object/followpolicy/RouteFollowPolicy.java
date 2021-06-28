package com.mmorpg.qx.module.object.followpolicy;

import com.mmorpg.qx.module.object.gameobject.NpcCreature;

public class RouteFollowPolicy extends AbstractFollowPolicy {

	public RouteFollowPolicy(NpcCreature owner) {
		super(owner);
	}

//	@Override
//	protected int[] getBornXY() {
//		RouteRoad road = owner.getRouteRoad();
//		int bornX = owner.getBornX();
//		int bornY = owner.getBornY();
//		if (!road.isOver()) {
//			bornX = road.getNextStep().getX();
//			bornY = road.getNextStep().getY();
//		}
//		return new int[] { bornX, bornY };
//	}
}
