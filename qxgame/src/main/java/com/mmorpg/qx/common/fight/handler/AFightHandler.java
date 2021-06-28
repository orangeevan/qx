package com.mmorpg.qx.common.fight.handler;

import com.mmorpg.qx.common.fight.IBoardBox;
import com.mmorpg.qx.common.fight.Node;
import com.mmorpg.qx.common.fight.action.IAction;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author: yuanchengyan
 * @description:
 * @since 10:39 2021/4/28
 */
public abstract class AFightHandler<BOX extends IBoardBox> {
    protected Node root = Node.Builder.buildEmpty();
    protected BOX box;

    public void execute() {
        run(root.getNext());
        end();
    }

    protected void run(Node node) {
        while (Objects.nonNull(node)) {
            Class actionClazz = node.getClazz();
            IAction action = box.pushAction(actionClazz);
            try {
                while (!action.isEnd()) {
                    Iterator<Node> it = node.childIterator();
                    //先执行完子节点
                    while (it.hasNext()) {
                        Node child = it.next();
                        run(child);
                    }
                    action.action();
                    action.circleEnd();
                }
                action.end();
            } catch (Exception e) {
                e.printStackTrace();
            }
            box.popAction();
            node = node.getNext();
        }

    }

    public void setBox(BOX box) {
        this.box = box;
    }

    public void end() {

    }

}

