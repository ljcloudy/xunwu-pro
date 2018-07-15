package com.cloudy.base;

/**
 * Created by ljy_cloudy on 2018/6/30.
 */
public class ApiDataTableResponse extends ApiResponse {

    private int draw;

    private long recordsTotal;

    private long recordsFiltered;

    public ApiDataTableResponse(int draw, long recordsTotal, long recordsFiltered) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
    }

    public ApiDataTableResponse(int code, String messge, Object data) {
        super(code, messge, data);
    }


    public ApiDataTableResponse(ApiResponse.Status status) {
        super(status.getCode(), null, null);
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
