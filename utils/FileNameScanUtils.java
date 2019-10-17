package com.bdi.sselab.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zjh
 * @Date 2019/03/27,17:16
 * @Description 根据登录权限获取可以拿到的xml名字
 */
@Component
public class FileNameScanUtils {

    /**
     * @methodname getAllFiles
     * @description 根据局文件夹名获取所有的xml文件名
     * @author zjh
     * @date 9:28,2019/3/28
     * @param path
     * @return
     */
    public void getAllFiles(String path, List<String> fileList) {

        File bureauPath = new File(path);
        if(bureauPath.isDirectory()) {
            File[] files = bureauPath.listFiles();
            if (files != null) {
                for(int i=0; i<files.length; i++) {
                    if(files[i].isFile())
                        fileList.add(files[i].getName());
                    else
                        getAllFiles(files[i].getAbsolutePath(), fileList);
                }
            }
        }
    }

    public static void main(String[] args) {
        List<String> fileList = new ArrayList<>();
        new FileNameScanUtils().getAllFiles("F:/项目/dataImport/src/template/rensheju/xml", fileList);

            for(String file : fileList) {
            System.out.println(file);
        }
    }
}
