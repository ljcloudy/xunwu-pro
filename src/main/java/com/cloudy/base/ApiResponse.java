package com.cloudy.base;

/**
 * Created by ljy_cloudy on 2018/5/31.
 */
public class ApiResponse {
    private int code;

    private String messge;

    private Object Data;

    private boolean more;

    public ApiResponse() {
        this.code = Status.SUCCESS.getCode();
        this.messge = Status.SUCCESS.getMessage();
    }

    public ApiResponse(int code, String messge, Object data) {
        this.code = code;
        this.messge = messge;
        Data = data;
    }

    public static ApiResponse ofStatus(Status status) {
        return new ApiResponse(status.getCode(), status.getMessage(), null);
    }

    public static ApiResponse ofSuccess(Object data) {
        return new ApiResponse(Status.SUCCESS.getCode(), Status.SUCCESS.getMessage(), data);
    }

    public static ApiResponse ofMessage(int code, String messge) {
        return new ApiResponse(code, messge, null);
    }


    public enum Status {
        SUCCESS(200, "ok"),
        BAD_REQUEST(400, "Bad Request"),
        INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
        NOT_VALID_PARAM(40005, "Not Valid Param"),
        NOT_SUPPORTED_OPERATION(40006, "Not Supported operation"),
        NOT_FOUND(40007, "Not Found"),
        NOT_LOGIN(50000, "Not Login");

        private int code;
        private String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessge() {
        return messge;
    }

    public void setMessge(String messge) {
        this.messge = messge;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}
