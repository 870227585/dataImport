package com.bdi.sselab.domain.log;


import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/26
 * @Time:15:21
 */

@Entity
@Table(name = "general_user_log", indexes = {@Index(name = "username_index", columnList = "username")})
public class GeneralUserLog extends Log{

    public GeneralUserLog() {

    }
}
