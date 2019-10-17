package com.bdi.sselab.controller;

import com.bdi.sselab.domain.log.Log;
import com.bdi.sselab.repository.departmentLog.SchemeStatusRepository;
import com.bdi.sselab.repository.userDepart.UserRepository;
import com.bdi.sselab.service.RolerToRepository;
import com.bdi.sselab.utils.FileNameScanUtils;
import com.bdi.sselab.utils.ParseXmlUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wh on 2019/3/26.
 */
@Api(description = "上传文件接口")
@RestController
@PropertySource(value = "classpath:conf.properties", encoding = "UTF-8")
public class FileUploadController {
    private final static String FILE_SEPARATOR = System.getProperty("file.separator");
    @Value("${import.xml.path}")
    private String path;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private FileNameScanUtils fileNameScanUtils;

    @Autowired
    private SchemeStatusRepository ssr;
    private Log log;
    private JpaRepository jpaRepository;
    private RolerToRepository rolerToRepository = new RolerToRepository();
//    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @PostMapping(value = "/upload")
    public ResponseEntity upload(@RequestParam(value = "file") MultipartFile file,
                                 @RequestParam(value = "xmlName") String xmlName,
                                 @RequestParam(value = "date") String date,
                                 Principal principal) throws JSONException {

        HashMap result = new HashMap();
        if (file.isEmpty() || xmlName.isEmpty()) {
            result.put("code", 201);
            result.put("message", "Excel文件为空！");
            return ResponseEntity.ok(result);
        }

        String roler = userRepository.findByUsername(principal.getName()).getRoler();
        String rootPath = System.getProperty("user.dir") + FILE_SEPARATOR + path + FILE_SEPARATOR + roler + FILE_SEPARATOR;
        String filename = file.getOriginalFilename();
        Long size = file.getSize();
        System.out.println(filename + ":" + size);

        log = rolerToRepository.getLog(roler);
        jpaRepository = rolerToRepository.getRepository(roler);
        System.out.println(roler + "hello");
        if (log != null) {
            System.out.println("log不为空");
        }else {
            System.out.println("log为空");

        }
        log.setFilename(filename);
//        try {
//            log.setTime(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
//            System.out.println(simpleDateFormat.format(new Date()));
//        }catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("时间格式错误");
//        }
        log.setTime(new Date());
        log.setUsername(principal.getName());

        //Excel路径
        String excelFile = rootPath + "excel" + FILE_SEPARATOR + filename;
        File dest = new File(excelFile);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", 202);
            result.put("message", "上传失败！");
            log.setDataNums(0);
            log.setStatus("上传失败");
            jpaRepository.save(log);
            return ResponseEntity.ok(result);
        }
        //XML路径
        String xmlFile = path + "/" + roler + "/xml/" + xmlName;
        result = ParseXmlUtils.parseReadXml(xmlFile, dest.toPath(), filename, date, log, ssr);
        jpaRepository.save(log);
        if (log.getStatus().equals("入库成功")) {
            HashMap resultSuccess = new HashMap();
            resultSuccess.put("code", 200);
            resultSuccess.put("message", "上传成功");
            return ResponseEntity.ok(resultSuccess);
        }
        return ResponseEntity.ok(result);
    }
    /**
     * 下载文件接口
     *
     * @param fileName 要下载的文件名
     * @return
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity download(@PathVariable("fileName") String fileName,Principal principal) {
        try {
            String roler = userRepository.findByUsername(principal.getName()).getRoler();
//            String roler = "tongjiju";
            String rootPath = System.getProperty("user.dir") + FILE_SEPARATOR + path + FILE_SEPARATOR + roler
                    + FILE_SEPARATOR+ "template" + FILE_SEPARATOR;
            String fullFileName = getUploadFileAbsPath(fileName,rootPath); // 通过文件名获取文件路径
            if (isExist(fullFileName)) {
                Resource resource = resourceLoader.getResource("file:" + fullFileName);
                HttpHeaders headers = new HttpHeaders();
                headers.add(
                        "Cache-Control",
                        "no-cache, no-store, must-revalidate"
                );
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");
                headers.add(
                        "Content-Disposition",
                        "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8")
                );
                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentLength(resource.contentLength())
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查找此局路径下的所有表格模板
     *
     * @param
     * @return 所有模板的文件名
     */
    @GetMapping("/findExcels")
    public ResponseEntity<List<String>> findExcels(Principal principal) {
        String roler = userRepository.findByUsername(principal.getName()).getRoler();
//        String roler = "tongjiju";
        String absolutePath =  System.getProperty("user.dir") + FILE_SEPARATOR + path + FILE_SEPARATOR + roler
                + FILE_SEPARATOR+ "template";
        System.out.println(absolutePath);
        List<String> fileNames = new ArrayList<>();
        fileNameScanUtils.getAllFiles(absolutePath, fileNames);
        return ResponseEntity.ok(fileNames);
    }


    /**
     * 获取上传文件目录下资源的读取路径
     * @param fileName
     * @return
     */
    public static String getUploadFileAbsPath(String fileName,String location) {
        if (fileName == null || fileName.length() == 0) {
            return "";
        }
        try {
             String fullFileName = Paths.get(location, fileName).toString();
            if (isExist(fullFileName)) {
                return fullFileName;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return fileName;
    }
    public static boolean isExist(String fileName) {
        return new File(fileName).exists();
    }


}
