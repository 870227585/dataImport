package com.bdi.sselab.domain.log;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/6/13
 * @Time:9:37
 */
@Data
@AllArgsConstructor
@Entity
public class SchemeStatus {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private int count;

    private double timer;

    private boolean status;

    public SchemeStatus() {

    }

}
