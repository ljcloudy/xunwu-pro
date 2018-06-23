package com.cloudy.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
@Entity
@Table(name = "subway")
public class Subway implements Serializable{

    private static final long serialVersionUID = -3978918934259486421L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "city_en_name")
    private String cityEnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }
}
