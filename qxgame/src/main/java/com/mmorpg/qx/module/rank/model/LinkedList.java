package com.mmorpg.qx.module.rank.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 双向链表，非线程安全
 * <p>
 * Created by zhangpeng on 2021/1/22
 */
public class LinkedList<E> {

    private int count;
    private Node<E> head;
    private Node<E> tail;

    public static class Node<E> {
        private final E item;
        private Node<E> next;
        private Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        private Node<E> addBefore(E element) {
            Node<E> node = new Node<>(prev, element, this);
            prev.next = node;
            prev = node;
            return node;
        }

        private Node<E> addAfter(E element) {
            Node<E> node = new Node<>(this, element, next);
            next.prev = node;
            next = node;
            return node;
        }

        public Node<E> replace(E element) {
            Node<E> node = new Node<>(prev, element, next);
            prev.next = node;
            next.prev = node;
            this.next = null;
            this.prev = null;
            return node;
        }

        public E getItem() {
            return item;
        }

        private Node<E> getNext() {
            return next;
        }

        private Node<E> getPrev() {
            return prev;
        }
    }

    public LinkedList() {
        this.head = new Node<>(null, null, null);
        this.tail = new Node<>(null, null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    public void addFirst(E element) {
        Node<E> next = this.head.next;
        Node<E> node = new Node<>(this.head, element, next);
        this.head.next = node;
        next.prev = node;
        this.count++;
    }

    public void addLast(E element) {
        Node<E> prev = this.tail.prev;
        Node<E> node = new Node<>(prev, element, this.tail);
        prev.next = node;
        this.tail.prev = node;
        this.count++;
    }

    public Node<E> addBefore(Node<E> node, E element) {
        Node<E> newnode = node.addBefore(element);
        this.count++;
        return newnode;
    }

    public Node<E> addAfter(Node<E> node, E element) {
        Node<E> newnode = node.addAfter(element);
        this.count++;
        return newnode;
    }

    public Node<E> replace(Node<E> node, E element) {
        Node<E> eNode = node.replace(element);
        return eNode;
    }

    public void remove(Node<E> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        this.count--;
    }

    public Node<E> getFirst() {
        if (this.head.next == this.tail)
            return null;
        return this.head.next;
    }

    public Node<E> getLast() {
        if (this.tail.prev == this.head)
            return null;
        return this.tail.prev;
    }

    public Node<E> getNext(Node<E> node) {
        if (node.getNext() == this.tail)
            return null;
        return node.getNext();
    }

    public Node<E> getPrev(Node<E> node) {
        if (node.getPrev() == this.head)
            return null;
        return node.getPrev();
    }

    public Node<E> get(E element) {
        if (count == 0)
            return null;
        for (Node<E> node = tail.prev; node != null; node = node.getPrev()) {
            if (node.getItem().equals(element)) {
                return node;
            }
        }
        return null;
    }

    public int getIndex(Node<E> node) {
        int index = 1;
        Node<E> curNode = node;
        while (curNode.next != null) {
            index++;
            curNode = curNode.next;
        }
        return size() - index;
    }

    public boolean isHead(Node<E> node){
        return node == head;
    }

    public boolean isTail(Node<E> node){
        return node == tail;
    }

    public int size() {
        return count;
    }

    public void clear() {
        head.next = tail;
        tail.prev = head;
        count = 0;
    }

    /**
     * 转ArrayList，数据序列化
     *
     * @return
     */
    public ArrayList<E> toArrayList() {
        ArrayList<E> list = new ArrayList<>();
        Node<E> node = getFirst();
        if (Objects.isNull(node)) {
            return list;
        }
        while (Objects.nonNull(getNext(node))) {
            list.add(getNext(node).item);
            node = getNext(node);
        }
        return list;
    }
}
