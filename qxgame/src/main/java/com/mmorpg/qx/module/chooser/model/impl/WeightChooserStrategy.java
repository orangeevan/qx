package com.mmorpg.qx.module.chooser.model.impl;

import java.util.List;

import com.mmorpg.qx.module.chooser.model.Chooser;
import com.mmorpg.qx.module.chooser.model.ChooserItem;
import com.mmorpg.qx.module.chooser.model.ChooserStrategy;
import com.mmorpg.qx.module.chooser.model.ChooserType;
import com.haipaite.common.utility.SelectRandom;
import org.springframework.stereotype.Component;

/**
 * 根据权重来选择策略
 * @author wang ke
 * @since v1.0 2018年3月27日
 *
 */
@Component
public class WeightChooserStrategy extends ChooserStrategy {

	@Override
	public ChooserType getType() {
		return ChooserType.WEIGHT;
	}

	@Override
	public List<ChooserItem> chooser(Chooser chooser) {
		SelectRandom<ChooserItem> selector = new SelectRandom<>();
		for (ChooserItem item : chooser.getItems()) {
			selector.addElement(item, item.getWeight());
		}
		return selector.run(chooser.getResultCount());
	}

}
