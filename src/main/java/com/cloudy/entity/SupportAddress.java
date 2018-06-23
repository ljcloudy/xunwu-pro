package com.cloudy.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
@Entity
@Table(name = "support_address")
public class SupportAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "belong_to")
    private String belongTo;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "cn_name")
    private String cnName;

    private String level;

    @Column(name = "baidu_map_lat")
    private double baiduMapLon;

    @Column(name = "baidu_map_lng")
    private double baiduMapLat;

    public enum Level {
        CITY("city"),
        REGION("region");

        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getBaiduMapLon() {
        return baiduMapLon;
    }

    public void setBaiduMapLon(double baiduMapLon) {
        this.baiduMapLon = baiduMapLon;
    }

    public double getBaiduMapLat() {
        return baiduMapLat;
    }

    public void setBaiduMapLat(double baiduMapLat) {
        this.baiduMapLat = baiduMapLat;
    }
}
