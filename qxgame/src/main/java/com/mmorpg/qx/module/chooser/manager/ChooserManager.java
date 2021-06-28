package com.mmorpg.qx.module.chooser.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.chooser.model.Chooser;
import com.mmorpg.qx.module.chooser.model.ChooserItem;
import com.mmorpg.qx.module.chooser.model.ChooserStrategy;
import com.mmorpg.qx.module.chooser.model.ChooserType;

/**
 * 选择器
 * @author wang ke
 * @since v1.0 2018年3月27日
 *
 */
@Component
public class ChooserManager {

	private Map<ChooserType, ChooserStrategy> choosers = new HashMap<>();

	public void rigister(ChooserStrategy chooserStrategy) {
		choosers.put(chooserStrategy.getType(), chooserStrategy);
	}

	private static ChooserManager self;

	public static ChooserManager getInstance() {
		return self;
	}

	public List<ChooserItem> chooser(Chooser chooser) {
		return choosers.get(chooser.getType()).chooser(chooser);
	}

	@PostConstruct
	public void init() {
		self = this;
	}

}
