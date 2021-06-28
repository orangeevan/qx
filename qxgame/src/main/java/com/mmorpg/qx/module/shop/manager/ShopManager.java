package com.mmorpg.qx.module.shop.manager;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.shop.resource.ShopResource;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;

/**
 * 商城
 * @author wang ke
 * @since v1.0 2018年3月21日
 *
 */
@Component
public class ShopManager {
	@Static
	private Storage<Integer, ShopResource> shopResources;

	private static ShopManager self;

	public static ShopManager getInstance() {
		return self;
	}

	@PostConstruct
	public void init() {
		self = this;
	}

	public ShopResource getResource(int id) {
		return shopResources.get(id, true);
	}
}
