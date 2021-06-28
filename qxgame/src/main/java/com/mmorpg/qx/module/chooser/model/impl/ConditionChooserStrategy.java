package com.mmorpg.qx.module.chooser.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.chooser.model.Chooser;
import com.mmorpg.qx.module.chooser.model.ChooserItem;
import com.mmorpg.qx.module.chooser.model.ChooserStrategy;
import com.mmorpg.qx.module.chooser.model.ChooserType;
import com.mmorpg.qx.module.condition.AbstractCondition;

/**
 * 根条件来选择策略
 * @author wang ke
 * @since v1.0 2018年3月27日
 *
 */
@Component
public class ConditionChooserStrategy extends ChooserStrategy {

	@Override
	public ChooserType getType() {
		return ChooserType.CONDITION;
	}

	@Override
	public List<ChooserItem> chooser(Chooser chooser) {
		List<ChooserItem> results = new ArrayList<>();
		for (ChooserItem item : chooser.getItems()) {
			AbstractCondition abstractCondition = item.getCondition().createConditon();
			if (abstractCondition.verify(chooser.getConditionTarget(), null, 1).isSuccess()) {
				results.add(item);
			}
		}
		return results;
	}

}
