package com.mmorpg.qx.module.ai.impl;

import com.mmorpg.qx.module.ai.AbstractAI;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;

/**
 * @author wang ke
 * @description: 全能机器人驯养师AI
 * @since 19:20 2020-08-18
 */
public class RobotTrainerAI extends AbstractAI {

    public RobotTrainerAI() {
        addStateHandler(AIState.Active.createHandler());
        addStateHandler(AIState.Idle.createHandler());
        addStateHandler(AIState.Use_Magic.createHandler());
        addStateHandler(AIState.Call_Fight.createHandler());
        addStateHandler(AIState.Dice.createHandler());
        addStateHandler(AIState.Extract_Card.createHandler());
        addStateHandler(AIState.Move.createHandler());
        addStateHandler(AIState.Pick_Up_Item.createHandler());
        addStateHandler(AIState.Fight.createHandler());
        addStateHandler(AIState.Add_Max_Mp.createHandler());
        addStateHandler(AIState.Operate_Building.createHandler());
    }
}
