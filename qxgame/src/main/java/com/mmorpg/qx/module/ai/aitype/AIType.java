package com.mmorpg.qx.module.ai.aitype;

import com.mmorpg.qx.module.ai.AbstractAI;
import com.mmorpg.qx.module.ai.impl.*;

import java.rmi.ServerError;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author wang ke
 * @description:CZ对应AI类型
 * @since 19:17 2020-08-18
 */
public enum AIType {
    /**
     * 第一個机器人驯养师AI
     */
    Robot_Trainer(1) {
        @Override
        public RobotTrainerAI createAI() {
            return new RobotTrainerAI();
        }
    },
    /**
     * Boss机器人，不会走，不会投骰子
     */
    Boss_Trainer(2) {
        @Override
        public BossTrainerAI createAI() {
            return new BossTrainerAI();
        }
    },
    ;
    private int id;

    private AIType(int id) {
        this.id = id;
    }

    public abstract <T extends AbstractAI> T createAI();

    /**
     * AI无状态可以缓存，如果子类有状态，需调用createAI方法
     */
    public static Map<AIType, AbstractAI> ai = new HashMap<>();

    static {
        Arrays.stream(AIType.values()).forEach(aiType -> ai.put(aiType, aiType.createAI()));
    }

    public static AIType valueOf(int id) {
        Optional<AIType> type = Arrays.stream(AIType.values()).filter(aiType -> aiType.id == id).findFirst();
        if (type.isPresent()) {
            return type.get();
        }
        throw new IllegalArgumentException("不支持AI类型: " + id);
    }

    public static AbstractAI getAI(AIType type) {
        return ai.get(type);
    }
}
