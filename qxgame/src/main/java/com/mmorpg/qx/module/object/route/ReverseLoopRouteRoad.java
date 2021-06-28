package com.mmorpg.qx.module.object.route;

import com.mmorpg.qx.common.SystemOut;

/**
 * 反向循环路线
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
public class ReverseLoopRouteRoad extends RouteRoad {

	private boolean forward = true;

	public ReverseLoopRouteRoad(RouteStep[] routeSteps) {
		super(routeSteps);
	}

	@Override
	public void overStep() {
		// 反向循环路线(0,1,2,1,0,1,2.. )
		if (forward) {
			step++;
		} else {
			step--;
		}
		if (step == 0) {
			forward = true;
		}
		if (step == length - 1) {
			forward = false;
		}

		if (step < 0 || step >= length) {
			reset();
		}
	}

	@Override
	public void reset() {
		super.reset();
		forward = true;
	}

	public static void main(String[] args) {
		ReverseLoopRouteRoad road = new ReverseLoopRouteRoad(new RouteStep[5]);
		SystemOut.println(road.step);
		for (int i = 0; i < 10; i++) {
			road.overStep();
			SystemOut.println(road.step);
		}
	}
}
