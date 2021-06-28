/*
package com.mmorpg.qx.module.consume;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.skill.lift.model.Skill;

public class SkillMpAction extends AbstractConsume {

	@Override
	public void act(Object object, ModuleInfo moduleInfo) {
		Skill skill = (Skill) object;
		skill.getEffector().getLifeStats().reduceMp(value);
	}

	@Override
	public boolean verify(Object object) {
		return CoreConditionType.createSkillMpCondition(value).verify(object);
	}

	@Override
	public AbstractConsume clone() {
		SkillMpAction countryCoppersAction = new SkillMpAction();
		countryCoppersAction.setCode(code);
		countryCoppersAction.setValue(value);
		return countryCoppersAction;
	}
}
*/
