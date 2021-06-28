package com.mmorpg.qx.common;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zhang peng
 * @description 概率相关
 * @since 17:52 2021/4/26
 */
public class Probability {

    public static class RateObject {
        /** ID */
        private int id;
        /** 权重比例 */
        private int rate;

        public RateObject (int id, int rate) {
            this.id = id;
            this.rate = rate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }
    }


    /**
     * 获取随机权重对象
     *
     * @param list
     * @return
     */
    public static RateObject randomObject(List<RateObject> list) {
        int totalRate = 0;
        for (RateObject temp : list) {
            totalRate += temp.getRate();
        }
        int randomRate = rand(0, totalRate);
        int currRate = 0;
        RateObject t = null;
        for (RateObject temp : list) {
            currRate += temp.getRate();
            if (randomRate <= currRate) {
                t = temp;
                break;
            }
        }
        return t;
    }

    /**
     * 获取随机数
     *
     * @return Integer, null: when max < min
     */
    public static Integer rand(int min, int max) {
        int tmp = max - min;
        if (tmp < 0) {
            return null;
        } else if (tmp == 0) {
            return min;
        } else {
            return ThreadLocalRandom.current().nextInt(tmp + 1) + min;
        }
    }

}
