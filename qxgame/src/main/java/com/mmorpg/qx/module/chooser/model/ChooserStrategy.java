package com.mmorpg.qx.module.chooser.model;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.mmorpg.qx.module.chooser.manager.ChooserManager;

/**
 * 选择策略
 * @author wang ke
 * @since v1.0 2018年3月27日
 *
 */
abstract public class ChooserStrategy {

	@Autowired
	private ChooserManager chooserManager;

	/**
	 * 选择器类型
	 * @return
	 */
	abstract public ChooserType getType();

	/**
	 * 
	 * @param chooser
	 * @return
	 */
	abstract public List<ChooserItem> chooser(Chooser chooser);

	@PostConstruct
	public void init() {
		chooserManager.rigister(this);
	}

}
