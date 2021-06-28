package com.mmorpg.qx.module.integral.model;


import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang peng
 * @description:
 * @since 19:08 2021/3/4
 */
public class IntegralStore {

    /**
     * Map<积分code, 积分数量>
     */
    private Map<Integer, Integer> stores = new HashMap<>();

    public Map<Integer, Integer> getStores() {
        return stores;
    }

    public void setStores(Map<Integer, Integer> stores) {
        this.stores = stores;
    }
}
