package com.bdi.sselab.dataLoad;

import com.bdi.sselab.utils.ConnectMysqlUtils;
import com.bdi.sselab.utils.ReadConf;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author ChenXiang
 * @Date 2019/05/04,21:02
 * @Description
 */
public class CreateMysqlTable {
    //private static final List<String> PRIMARY_KEY= Arrays.asList(ReadConf.getProperty("PRIMARY_KEY").split("/"));
    private static final List<String> date= Arrays.asList(ReadConf.getProperty("PRIMARY_KEY").split("/")[0]);
    public static boolean createMysqlTable(Map<String, List<String>> map) {
        ConnectMysqlUtils connectMysqlUtils = new ConnectMysqlUtils("");
        boolean flag = false;
        for (String string : map.keySet()) {
            List<String> cols=map.get(string);
            //cols.addAll(date);
            //cols.addAll(PRIMARY_KEY);
            //只要有创建成功的表就返回成功
            flag = connectMysqlUtils.createDataTable(string,cols)||flag;
            if (false == flag) {
                continue;
            }
        }
        return flag;
    }
}
