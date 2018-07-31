package com.cloudy.service.search;

/**
 * Created by ljy_cloudy on 2018/7/31.
 */
public class HouseBucketDTO {
    /**
     * 聚合bucket的key
     */
    private String key;

    /**
     * 聚合结果
     */
    private long count;

    public HouseBucketDTO() {
    }

    public HouseBucketDTO(String key, long count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
