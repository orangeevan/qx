package com.mmorpg.qx.module.consume;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.mmorpg.qx.module.consume.resource.ConsumeResource;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;

/**
 * 消费管理器
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
@Component
public class ConsumeManager {

	@Static
	private Storage<String, ConsumeResource> consumeResources;

	private static ConsumeManager self;

	public static ConsumeManager getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public static Consumes createConsumes(List<ConsumeResource> resources) {
		Consumes consumes = new Consumes();
		for (ConsumeResource resource : resources) {
			AbstractConsume consume = resource.getType().create();
			consumes.addConsumes(consume);
		}
		return consumes;
	}

}
