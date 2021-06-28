package com.mmorpg.qx.common.fight;

/**
 * @author: yuanchengyan
 * @description:
 * @since 10:35 2021/4/28
 */
public interface IBoard<BOX extends IBoardBox> {
    void initBoard(BOX box);
}

