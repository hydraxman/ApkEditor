package com.bryansharp.tools.parseapk.utils;

/**
 * Created by bushaopeng on 17/4/5.
 */
public class IntPointer {
    private int value;

    private IntPointer(int value) {
        this.value = value;
    }

    public static IntPointer get(int value) {
        return new IntPointer(value);
    }

    public int add(int addValue) {
        value += addValue;
        return value;
    }

    public int addOne() {
        return value++;
    }

    public int getValue() {
        return value;
    }
}
