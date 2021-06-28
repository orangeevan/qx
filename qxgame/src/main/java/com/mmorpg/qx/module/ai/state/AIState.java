package com.mmorpg.qx.module.ai.state;

import com.mmorpg.qx.module.ai.state.impl.*;

/**
 * Ai状态
 *
 * @author wang ke
 * @since v1.0 2020年7月29日
 */
public enum AIState {
    /**
     * 空闲休眠等待轮次
     */
    Idle {
        @Override
        public IdleStateHandler createHandler() {
            return new IdleStateHandler();
        }
    },
    /**
     * 激活,获得轮次
     */
    Active {
        @Override
        public ActiveStateHandler createHandler() {
            return new ActiveStateHandler();
        }
    },
    /**
     * 抽卡
     */
    Extract_Card {
        @Override
        public ExtractCardStateHandler createHandler() {
            return new ExtractCardStateHandler();
        }
    },
    /**
     * 添加魔法上限，恢复魔法
     */
    Add_Max_Mp {
        @Override
        public AddMaxMpStateHandler createHandler() {
            return new AddMaxMpStateHandler();
        }
    },
    /**
     * 使用魔法
     */
    Use_Magic {
        @Override
        public UseMagicStateHandler createHandler() {
            return new UseMagicStateHandler();
        }
    },

    /**
     * 召唤战斗
     */
    Call_Fight {
        @Override
        public CallMwnFightStateHandler createHandler() {
            return new CallMwnFightStateHandler();
        }
    },
    /**
     * 摇骰子
     */
    Dice {
        @Override
        public ThrowDiceStateHandler createHandler() {
            return new ThrowDiceStateHandler();
        }
    },
    /**
     * 移动
     */
    Move {
        @Override
        public MoveStateHandler createHandler() {
            return new MoveStateHandler();
        }
    },
    /**
     * 拾取
     */
    Pick_Up_Item {
        @Override
        public PickUpStateHandler createHandler() {
            return new PickUpStateHandler();
        }
    },
    /**
     * 战斗
     */
    Fight {
        @Override
        public FightStateHandler createHandler() {
            return new FightStateHandler();
        }
    },
    /**
     * 操作建筑
     */
    Operate_Building {
        @Override
        public OperateBuildStateHandler createHandler() {
            return new OperateBuildStateHandler();
        }
    };

    public abstract <T extends IStateHandler> T createHandler();

}
