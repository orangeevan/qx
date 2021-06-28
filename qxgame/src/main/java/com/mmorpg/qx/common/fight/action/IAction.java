package com.mmorpg.qx.common.fight.action;

import com.mmorpg.qx.common.fight.IBoard;
import com.mmorpg.qx.common.fight.IBoardBox;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:19 2021/4/27
 */
public abstract class IAction<B extends IBoard, BOX extends IBoardBox>{
    protected BOX box;
    protected B board;

    public void init(BOX box, B board) {
        this.box = box;
        this.board = board;
    }

    public abstract boolean isEnd();

    public void end() {
    }

    public abstract void action();

    public void circleEnd() {
    }

    public BOX getBox() {
        return box;
    }

    public B getBoard() {
        return board;
    }
}

