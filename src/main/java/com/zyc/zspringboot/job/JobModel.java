package com.zyc.zspringboot.job;

public enum JobModel {

    TIME_SEQ(0),ONCE(1),REPEAT(2);

    private int value;


    private JobModel(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
