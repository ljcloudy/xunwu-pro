package com.cloudy.service;

import com.cloudy.web.dto.HouseDTO;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public class ServiceResult<T> {
    private boolean success;

    private String message;

    private T result;

    public ServiceResult() {
    }

    public ServiceResult(boolean success) {
        this.success = success;
    }

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public static ServiceResult notFound() {
        ServiceResult result = new ServiceResult();
        result.setSuccess(false);
        result.setMessage("not Found!");
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static <T> ServiceResult<T> of(T t) {
        ServiceResult<T> result = new ServiceResult();
        result.setSuccess(true);
        result.setResult(t);
        return result;
    }

    public static ServiceResult success() {
        return new ServiceResult(true);
    }
}
