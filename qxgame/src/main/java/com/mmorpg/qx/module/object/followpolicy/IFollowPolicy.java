package com.mmorpg.qx.module.object.followpolicy;

public interface IFollowPolicy {

	public boolean outWarning(int tx, int ty);

	public boolean tooFarFromHome(int tx, int ty);
}
