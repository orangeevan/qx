package com.mmorpg.qx.module.roundFight.enums;

/**
 * @author wang ke
 * @description: 核心玩法回合阶段
 * @since 17:21 2020-08-13
 */
public enum RoundStage {
    //等待其他玩家准备完毕
    Wait_Trainer_Ready {
        @Override
        public RoundStage getNextStage() {
            return Extract_Card_Before;
        }
    },

    //抽卡前，魔物上限+1，恢复魔法,建筑状态刷新，buff刷新，被动技能释放
    Extract_Card_Before {
        @Override
        public RoundStage getNextStage() {
            return Extract_Card;
        }

        @Override
        public int getRSStartTimeOut() {
            return 5000;
        }
    },
    //抽取卡牌后，判断,是否无牌，扣血，手指牌已有7张，魔物娘卡牌丢弃
    Extract_Card {
        @Override
        public RoundStage getNextStage() {
            return Throw_Dice_Before;
        }

        @Override
        public int getRSStartTimeOut() {
            return 5000;
        }
    },
    //投掷骰子前 释放魔法，召唤魔物娘占领格子或战斗，
    Throw_Dice_Before {
        @Override
        public RoundStage getNextStage() {
            return Throw_Dice;
        }

        @Override
        public int getRSStartTimeOut() {
            return 10000;
        }

    },
    //投掷骰子
    Throw_Dice {
        @Override
        public RoundStage getNextStage() {
            return Throw_Dice_After;
        }


        @Override
        public int getRSEndTimeOut() {
            return 30000;
        }
    },
    //投骰子后
    Throw_Dice_After {
        @Override
        public RoundStage getNextStage() {
            return MOVE;
        }
    },
    //移动过程中 碰到建筑对应效果操作
    MOVE {
        @Override
        public RoundStage getNextStage() {
            return MOVE_END;
        }

        @Override
        public int getRSStartTimeOut() {
            return 5000;
        }

        @Override
        public int getRSEndTimeOut() {
            return 20000;
        }
    },
    //到达终点 拾取物品，召唤魔物娘，战斗，建筑效果
    MOVE_END {
        @Override
        public RoundStage getNextStage() {
            return Round_END;
        }

        @Override
        public int getRSStartTimeOut() {
            return 5000;
        }

        @Override
        public int getRSEndTimeOut() {
            return 30000;
        }
    },
    //整个回合结束
    Round_END {
        @Override
        public RoundStage getNextStage() {
            return Extract_Card_Before;
        }

        @Override
        public int getRSStartTimeOut() {
            return 0;
        }

        @Override
        public int getRSEndTimeOut() {
            return 0;
        }
    },

    ;

    public abstract RoundStage getNextStage();

    /**
     * 阶段开启超时时间
     *
     * @return
     */
    public int getRSStartTimeOut() {
        return 20000;
    }

    /**
     * 阶段结束超时时间，等客户端动作表现完
     *
     * @return
     */
    public int getRSEndTimeOut() {
        return 5000;
    }
}
