package com.bdi.sselab.domain.log;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/26
 * @Time:15:58
 */
@Data
@AllArgsConstructor
@MappedSuperclass
public class Log implements Comparable<Log>{
    @Id
    @GeneratedValue
    protected Long id;
    //上传的文件名
    private String filename;
    //上传时间
    private Date time;
    //上传的数据条数
    private int dataNums;
    //是否上传成功
    private String status;
    //用户名
    private String username;

    public Log() {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getDataNums() {
        return dataNums;
    }

    public void setDataNums(int dataNums) {
        this.dataNums = dataNums;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(Log log) {
        return -time.compareTo(log.getTime());
    }

}
