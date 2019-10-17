package com.bdi.sselab.controller;

import com.bdi.sselab.domain.user.User;
import com.bdi.sselab.repository.userDepart.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/23
 * @Time:17:46
 */
@RepositoryRestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${import.xml.path}")
    private String path;

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "password") String password) {

        User user = new User();
        user.setRoler(username);
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        HashMap result = new HashMap();
        if (userRepository.findByUsername(username) != null) {
            System.out.println("用户已存在");
            result.put("code", 203);
            result.put("message", "用户已存在");
            return ResponseEntity.ok(result);
        }else {
            userRepository.save(user);
            //创建用户对应的文件目录
            File file = new File(path + "/" + username);
            if (! file.exists()) {
                file.mkdir();
            }else {
            }
            File file1 = new File(file.getPath() + "/" + "xml");
            File file2 = new File(file.getPath() + "/" + "excel");
            File file3 = new File(file.getPath() + "/" + "template");
            if (! file1.exists()) {
                file1.mkdir();
            }
            if (! file2.exists()) {
                file2.mkdir();
            }
            if (! file3.exists()) {
                file3.mkdir();
            }
        }
        result.put("code", 200);
        result.put("message", "注册成功");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/showAllUser")
    public ResponseEntity<List<String>> findAllUser () {
        List<String> usernames = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        return ResponseEntity.ok(usernames);
    }

    @PostMapping("/deleteUser")
    public ResponseEntity selectUser(@RequestParam(value = "username") String username) {
        HashMap result = new HashMap();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            try {
                userRepository.delete(user);
                result.put("code", 200);
                result.put("message", "删除成功");
            }catch (Exception e) {
                e.printStackTrace();
                result.put("code", 205);
                result.put("message", "删除失败");
                return ResponseEntity.ok(result);
            }
        }else {
            result.put("code", 204);
            result.put("message", username + "不存在");
        }
        return ResponseEntity.ok(result);
    }
}
