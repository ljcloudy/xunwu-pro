package com.cloudy.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
@Entity
@Table(name = "subway_station")
public class SubwayStation implements Serializable{

    private static final long serialVersionUID = 4283007121186947071L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subway_id")
    private Long subwayId;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(Long subwayId) {
        this.subwayId = subwayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
