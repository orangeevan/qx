package com.mmorpg.qx.module.ai.impl;

import com.mmorpg.qx.module.ai.AbstractAI;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;

/**
 * @author wang ke
 * @description:Boss驯养师，不能走，不投骰子
 * @since 19:14 2020-09-10
 */
public class BossTrainerAI extends AbstractAI {
    public BossTrainerAI(){
        addStateHandler(AIState.Active.createHandler());
        addStateHandler(AIState.Idle.createHandler());
        addStateHandler(AIState.Use_Magic.createHandler());
        addStateHandler(AIState.Call_Fight.createHandler());
        //addStateHandler(AIState.DICE.createHandler());
        addStateHandler(AIState.Extract_Card.createHandler());
        //addStateHandler(AIState.MOVE.createHandler());
        addStateHandler(AIState.Pick_Up_Item.createHandler());
        addStateHandler(AIState.Fight.createHandler());
        addStateHandler(AIState.Add_Max_Mp.createHandler());
        addStateHandler(AIState.Operate_Building.createHandler());
    }
}
