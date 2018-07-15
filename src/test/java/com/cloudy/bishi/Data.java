package com.cloudy.bishi;

/**
 * Created by ljy_cloudy on 2018/7/7.
 */
public class Data {
    private String id;

    private String groupId;

    private float quota;

    public Data(){}

    public Data(String id, String groupId, float quota){
        this.id = id;
        this.groupId = groupId;
        this.quota = quota;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public float getQuota() {
        return quota;
    }

    public void setQuota(float quota) {
        this.quota = quota;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", quota=" + quota +
                '}';
    }
}
