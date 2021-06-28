package com.mmorpg.qx.module.skill.model.target.chooser;

import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetType;

/**
 * 目标选择器
 * @author wang ke
 * @since v1.0 2018年3月2日
 *
 */
public interface ITargetChooser {
	/**
	 * 目标类型
	 * @return
	 */
	public TargetType getTargetType();

	/**
	 * 验证目标
	 * @param caster
	 * @param target
	 * @return
	 */
	public Result verifyTarget(AbstractCreature caster, Target target);

	/**
	 * 有些技能自选目标
	 * @param skillCaster
	 * @return
	 */
	public Target chooseTarget(AbstractCreature skillCaster);


	public Result verifyTarget(AbstractCreature caster, Target target, Object params);
}
