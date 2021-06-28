package com.mmorpg.qx.common.fight;

import com.mmorpg.qx.common.fight.action.IAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:20 2021/4/27
 */
public class Node<A extends IAction> {
    private Class<A> clazz;
    /**
     * 子节点先执行完在执行当前节点
     */
    private List<Node> children = new ArrayList<>();
    private Node next;

    private Node() {

    }

    public Iterator<Node> childIterator() {
        return children.iterator();
    }


    public Node getNext() {
        return next;
    }


    public Class<A> getClazz() {
        return clazz;
    }

    public void setClazz(Class<A> clazz) {
        this.clazz = clazz;
    }

    public boolean hasNext() {
        return Objects.nonNull(next);
    }


    public void setNext(Node next) {
        this.next = next;
    }

    public static Node nullNode() {
        return new Node();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public static class Builder {

        public static <A extends IAction> Node<A> build(Class<A> actionClz) {
            Node node = new Node();
            node.setClazz(actionClz);
            return node;
        }

        public static Node buildEmpty() {
            return Node.nullNode();
        }
    }
}

