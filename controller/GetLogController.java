package com.bdi.sselab.controller;

import com.bdi.sselab.domain.log.Log;
import com.bdi.sselab.repository.departmentLog.GeneralUserLogRepository;
import com.bdi.sselab.repository.departmentLog.RootLogRepository;
import com.bdi.sselab.repository.userDepart.UserRepository;
import com.bdi.sselab.service.RolerToRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/26
 * @Time:16:21
 */
@RepositoryRestController
@RequestMapping("api/logs")
public class GetLogController {

    @Autowired
    private RootLogRepository rootLogRepository;
    @Autowired
    private GeneralUserLogRepository generalUserLogRepository;

    @PostMapping ("/getLogs")
    public ResponseEntity<List<Log>> selectLogs(@RequestParam(value = "username") String username) {
//        jpaRepository = rolerToRepository.getRepository(userRepository.findByUsername(principal.getName()).getRoler());
        List logs;
        if (username.equals("root")) {
            logs = rootLogRepository.findAll();
        }else {
            logs = generalUserLogRepository.findByUsername(username);
        }
        Collections.sort(logs);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/selectLogs")
    public ResponseEntity<List<Log>> selectLogsByUsername(Principal principal) {
        List logs;
        try {
            if (principal.getName().equals("root")) {
                logs = rootLogRepository.findAll();
            }else {
                logs = generalUserLogRepository.findByUsername(principal.getName());
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Collections.sort(logs);
        return ResponseEntity.ok(logs);
    }
}
