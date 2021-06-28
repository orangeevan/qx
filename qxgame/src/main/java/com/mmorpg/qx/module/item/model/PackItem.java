package com.mmorpg.qx.module.item.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.object.gameobject.AbstractObject;

/**
 * 背包物品
 * @author wang ke
 * @since v1.0 2018年3月6日
 *
 */
public class PackItem extends AbstractObject {

	@Protobuf(description = "配置表id")
	private int key;
	@Protobuf(description = "数量")
	private int num;
	/** 创建时间 */
	private long creatTime;

	public static PackItem valueOf(int key, int num) {
		PackItem item = new PackItem();
		item.setObjectId((long) key);
		item.setKey(key);
		item.setNum(num);
		item.setCreatTime(System.currentTimeMillis());
		return item;
	}

	@JSONField(serialize = false)
	public String getName() {
		return ItemManager.getInstance().getResource(key).getName();
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public long getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}

	public void addNum(long num) {
		this.num += num;
	}
}
