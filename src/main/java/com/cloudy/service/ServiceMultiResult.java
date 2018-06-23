package com.cloudy.service;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
public class ServiceMultiResult<T> {

    private int total;

    private List<T> result;

    public ServiceMultiResult(int total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getResultSize(){
        if (this.result == null){
            return 0;
        }
        return this.result.size();
    }
}
