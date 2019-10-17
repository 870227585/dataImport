package com.bdi.sselab.service;

import com.bdi.sselab.domain.log.*;
import com.bdi.sselab.repository.departmentLog.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/26
 * @Time:16:13
 */
@Component
public class RolerToRepository {
    @Autowired
    private RootLogRepository rootLogRepository;

    @Autowired
    private GeneralUserLogRepository generalUserLogRepository;

    private static RolerToRepository rolerToRepository;

    @PostConstruct
    public void init() {
        rolerToRepository = this;
    }
    public JpaRepository getRepository(String roler) {
        if (roler != null) {
            if (roler.equals("admin")) {
                return rolerToRepository.rootLogRepository;
            }else {
                return rolerToRepository.generalUserLogRepository;
            }
        }
        return null;
    }

    public Log getLog(String roler) {
        if (roler != null) {
            if (roler.equals("admin")) {
                return new RootLog();
            }else {
                return new GeneralUserLog();
            }
        }
        return null;
    }
}
