package com.bdi.sselab.hbase.hbaseUtils;

import org.apache.hadoop.hbase.client.Scan;

/**
 * @Author ChenXiang
 * @Date 2019/05/04,21:24
 * @Description 默认caching是5000
 */
public class ScannUtil {
    private static final int caching=30000;

    /**
     * @methodname getAScan
     * @description 返回一个纯粹的scan
     * @author ChenXiang
     * @date 21:29,2019/5/4
     * @param
     * @return org.apache.hadoop.hbase.client.Scan
     */
    public static Scan getAScan(){
        Scan scan=new Scan();
        scan.setCaching(caching);
        return scan;
    }

    /**
     * @methodname getAScanWithCaching
     * @description 返回一个指定缓存的scan
     * @author ChenXiang
     * @date 21:30,2019/5/4
     * @param caching
     * @return org.apache.hadoop.hbase.client.Scan
     */
    public static Scan getAScanWithCaching(int caching){
        Scan scan=new Scan();
        scan.setCaching(caching);
        return scan;
    }
}
