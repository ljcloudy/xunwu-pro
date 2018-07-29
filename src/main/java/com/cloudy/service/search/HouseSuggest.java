package com.cloudy.service.search;

/**
 * Created by ljy_cloudy on 2018/7/29.
 */
public class HouseSuggest {

    private String input;

    private int weight = 10;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
