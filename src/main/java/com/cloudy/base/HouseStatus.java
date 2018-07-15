package com.cloudy.base;

/**
 * Created by ljy_cloudy on 2018/6/30.
 */
public enum HouseStatus {
    /**
     * 未审核
     */
    NOT_AUDITED(0),
    /**
     * 审核通过
     */
    PASSES(1),
    /**
     * 已出租
     */
    RENTED(2),
    /**
     * 已删除
     */
    DELETED(3);

    private int value;

    HouseStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
