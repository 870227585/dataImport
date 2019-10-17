package com.bdi.sselab.hbase.config;

import com.bdi.sselab.utils.ReadConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * @Description
 * @Author ChenXiang
 * @Date 2019/04/15,09:58
 */
public class HBaseConfig {
    private static Configuration conf = null;
    private static Connection conn = null;

    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.master", ReadConf.getProperty("HBASE_MASTER"));
        conf.set("hbase.zookeeper.quorum", ReadConf.getProperty("HBASE_ZOOKEEPER_QUORUM"));
        conf.set("hbase.zookeeper.property.clientport", ReadConf.getProperty("ZK_PORT"));
        try {
            conn = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HBaseConfig() {
    }

    /**
     * 获取HBase配置
     *
     * @return
     */
    public static Configuration getConf() {
        return conf;
    }

    /**
     * 获取HBase连接
     *
     * @return
     */
    public static Connection getConn() {
//        try {
//            conn = ConnectionFactory.createConnection(conf);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return conn;
    }

}
