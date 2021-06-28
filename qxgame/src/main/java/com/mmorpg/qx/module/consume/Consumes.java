package com.mmorpg.qx.module.consume;

import java.util.ArrayList;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.condition.Result;

public class Consumes {

	private ArrayList<AbstractConsume> consumeList = new ArrayList<>();

	/**
	 * 获取第一个道具消耗的key
	 * 
	 * @return
	 */
	/*
	public String getFirstItemKey() {
		for (AbstractConsume consume : consumeList) {
			if (consume instanceof ItemConsume) {
				return consume.getCode();
			}
		}
		return null;
	}
	*/

	/*
	public Set<String> getAllItemKeys() {
		Set<String> items = new HashSet<String>();
		for (AbstractConsume consume : consumeList) {
			if (consume instanceof ItemConsume) {
				items.add(consume.getCode());
			}
		}
		return items;
	}
	*/

	/*
	public long getCurrencyValue(CurrencyType currencyType) {
		long result = 0L;
		for (AbstractConsume consume : consumeList) {
			if (consume instanceof CurrencyConsume) {
				if (((CurrencyConsume) consume).getType() == currencyType) {
					result += consume.getValue();
				}
			}
		}
		return result;
	}
	*/

	
	public Result verify(Object object, int amount) {
		for (AbstractConsume consume : consumeList) {
			Result r = consume.verify(object, amount);
			if (!r.isSuccess()) {
				return r;
			}
		}
		return Result.SUCCESS;
	}

	public void act(Object object, ModuleInfo moduleInfo, int amount) {
		for (AbstractConsume consume : consumeList) {
			consume.consume(object, moduleInfo, amount);
		}
	}

	public void addConsumes(Consumes consumes) {
		for (AbstractConsume ac : consumes.consumeList) {
			this.addConsume(ac);
		}
	}

	public void addConsume(AbstractConsume consume) {
		AbstractConsume add = consume;
		for (AbstractConsume temp : consumeList) {
			add = temp.merge(add);
			// 合并成功返回null
			if (add == null) {
				break;
			}
		}
		if (add != null) {
			consumeList.add(add.clone());
		}
	}

	public void addConsumes(AbstractConsume... consumes) {
		for (AbstractConsume consume : consumes) {
			addConsume(consume);
		}
	}

	public boolean isEmpty() {
		return consumeList.isEmpty();
	}
}
