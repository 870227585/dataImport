package com.bdi.sselab.controller;

import com.bdi.sselab.repository.userDepart.UserRepository;
import com.bdi.sselab.utils.FileNameScanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author zjh
 * @Date 2019/03/28,10:30
 */
@PropertySource(value = "classpath:conf.properties", encoding = "UTF-8")
@RestController
public class XmlSearchController {

    @Value("${import.xml.path}")
    private String path;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileNameScanUtils fileNameScanUtils;

    private final static String FILE_SEPARATOR = System.getProperty("file.separator");

    @GetMapping("/findXmls")
    public ResponseEntity<List<String>> findXmlsName(Principal principal) {
        String roler = userRepository.findByUsername(principal.getName()).getRoler();
        String absolutePath =  System.getProperty("user.dir") + FILE_SEPARATOR + path + FILE_SEPARATOR + roler + FILE_SEPARATOR + "xml";
        System.out.println(absolutePath);
        List<String> fileNames = new ArrayList<>();
        fileNameScanUtils.getAllFiles(absolutePath, fileNames);
        return ResponseEntity.ok(fileNames);
    }

    @PostMapping("/adminFindXmls")
    public ResponseEntity<List<String>> findXmlsName(@RequestParam(value = "username") String username) {
        String roler = null;
        List<String> fileNames = new ArrayList<>();
        try{
            roler = userRepository.findByUsername(username).getRoler();
        }catch (NullPointerException e) {
            e.printStackTrace();
            fileNames.add("此用户未注册");
            return ResponseEntity.ok(fileNames);
        }
        String absolutePath =  System.getProperty("user.dir") + FILE_SEPARATOR + path + FILE_SEPARATOR + roler + FILE_SEPARATOR + "xml";
        System.out.println(absolutePath);
        fileNameScanUtils.getAllFiles(absolutePath, fileNames);
        return ResponseEntity.ok(fileNames);
    }
}
