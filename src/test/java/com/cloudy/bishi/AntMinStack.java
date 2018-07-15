package com.cloudy.bishi;

import java.util.Arrays;

/**
 * Created by ljy_cloudy on 2018/7/7.
 */
public class AntMinStack {

    private static final int DEFAULE_SIZE = 10;

    private Integer[] data = null;

    private Integer [] sortData = null;

    private int size;

    public AntMinStack() {
        data = new Integer[DEFAULE_SIZE];
    }

    public AntMinStack(int initialCapacity) {
        if (initialCapacity > 0) {
            data = new Integer[initialCapacity];
            sortData = new Integer[initialCapacity];
        } else if (initialCapacity == 0) {
            data = new Integer[DEFAULE_SIZE];
            sortData = new Integer[DEFAULE_SIZE];
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " +
                    initialCapacity);
        }
    }

    public int min() {
        return sortData[size-1];
    }

    public int pop() {
        int value = data[size-1];
        data[size-1] = null;
        sortData[size-1] =null;
        size--;
        return value;
    }

    public void push(int value) {
        if( size >= data.length ){
            throw new RuntimeException("栈溢出！");
        }
        data[size++] = value;
        if(size ==1){
            sortData[size-1] = value;
        }else {
            if(sortData[size-2] != null && value > sortData[size-2]){
                sortData[size-1] = sortData[size-2];
            }else {
                sortData[size-1] = value;
            }
        }

    }

    public String toString(){
        StringBuilder sb = new StringBuilder("stack:[ ");
        for (int i = 0; i < data.length; i++) {
            sb.append(data[i]);
            if(i != data.length -1){
                sb.append(", ");
            }
        }
        sb.append(" ]");

        return sb.toString();
    }

    public static void main(String[] args) {
        AntMinStack stack = new AntMinStack(20);

        stack.push(9);
        stack.push(6);
        stack.push(10);
        stack.push(52);
        stack.push(5);
        stack.push(46);
        stack.push(7);
        System.out.println(stack.toString());

        stack.pop();
        stack.pop();
//        stack.pop();
//        stack.pop();
        stack.pop();
        stack.pop();
        stack.pop();

        stack.push(20);
        stack.push(245);
        stack.push(1);
        stack.push(2);
        stack.push(2);
        stack.push(2);
        stack.push(2);
        System.out.println(stack.toString());

        System.out.println(stack.min());
        System.out.println(Arrays.toString(stack.sortData));
    }
}
