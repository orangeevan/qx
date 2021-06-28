package com.haipaite.common.utility;

import java.util.Random;


public final class RandomUtils extends org.apache.commons.lang.math.RandomUtils {
    public static final int RATE_BASE = 10000;

    private RandomUtils() {
        throw new IllegalAccessError("该类不允许实例化");
    }


    public static boolean isHit(int rate) {
        if (rate <= 0) {
            return false;
        }
        int value = org.apache.commons.lang.math.RandomUtils.nextInt(10000);
        if (value <= rate) {
            return true;
        }
        return false;
    }


    public static boolean isHit(int rate, Random random) {
        if (rate <= 0) {
            return false;
        }
        int value = org.apache.commons.lang.math.RandomUtils.nextInt(random, 10000);
        if (value <= rate) {
            return true;
        }
        return false;
    }


    public static int betweenInt(int min, int max, boolean include) {
        if (min > max) {
            throw new IllegalArgumentException("最小值[" + min + "]不能大于最大值[" + max + "]");
        }
        if (!include && min == max) {
            throw new IllegalArgumentException("不包括边界值时最小值[" + min + "]不能等于最大值[" + max + "]");
        }

        if (include) {
            max++;
        } else {
            min++;
        }
        return (int) (min + Math.random() * (max - min));
    }


    public static int[] getRandomArray(int min, int max, int n) {
        int len = max - min + 1;

        if (max < min || n > len) {
            return null;
        }

        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int j = 0; j < result.length; j++) {
            index = Math.abs(rd.nextInt() % len--);
            result[j] = source[index];
            source[index] = source[len];
        }
        return result;
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\RandomUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */