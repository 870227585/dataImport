package com.bdi.sselab.utils;

import java.io.*;
import java.util.Properties;

/**
 * 加载并返回HBase连接方面的配置
 *
 * @author QiHaiyang
 * @date 2018/5/7
 */
public class ReadConf {

//    private static final String CONF_PATH = System.getProperty("user.dir") + File.separator + "config" + File.separator + "config.properties";
    private static Properties property = new Properties();
    private static InputStream file;

    static {
        try {
//            file = new BufferedInputStream(new FileInputStream(CONF_PATH));
            file=ReadConf.class.getClassLoader().getResourceAsStream("conf.properties");
            property.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("未找到配置文件");
        }

    }

    private ReadConf() {
    }

    /**
     * 根据属性名获取属性的值
     *
     * @param key
     * @return String
     */
    public static String getProperty(String key) {
        return property.getProperty(key);
    }

    static class InnerClass {
        private static ReadConf readHBaseConf = new ReadConf();
    }

}
