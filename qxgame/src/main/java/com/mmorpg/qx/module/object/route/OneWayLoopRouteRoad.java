package com.mmorpg.qx.module.object.route;

import com.mmorpg.qx.common.SystemOut;

public class OneWayLoopRouteRoad extends RouteRoad {

	public OneWayLoopRouteRoad(RouteStep[] routeSteps) {
		super(routeSteps);
	}

	@Override
	public void overStep() {
		// 单向循环路线 (0,1,2,0,1,2,0,1,2....)
		step = ++step % length;
	}

	public static void main(String[] args) {
		OneWayLoopRouteRoad road = new OneWayLoopRouteRoad(new RouteStep[5]);
		SystemOut.println(road.step);
		for (int i = 0; i < 10; i++) {
			road.overStep();
			SystemOut.println(road.step);
		}
	}
}
