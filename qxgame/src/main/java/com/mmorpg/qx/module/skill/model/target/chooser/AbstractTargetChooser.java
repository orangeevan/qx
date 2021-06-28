package com.mmorpg.qx.module.skill.model.target.chooser;

import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.target.Target;

public abstract class AbstractTargetChooser implements ITargetChooser {
	/**
	 * 多数类型不需要自选目标
	 * @param skillCaster
	 * @return
	 */
	@Override
	public Target chooseTarget(AbstractCreature skillCaster) {
		return null;
	}


	@Override
	public Result verifyTarget(AbstractCreature caster, Target target, Object params) {
		return null;
	}
}
