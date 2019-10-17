package com.bdi.sselab.hbase.table;


import com.bdi.sselab.hbase.config.HBaseConfig;
import com.bdi.sselab.utils.ReadConf;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author ChenXiang
 * @Date 2019/04/17,15:44
 */
@Component
public class HBaseTableManager {

    private static final String CF = "COLUMN_FAMILY";
    public final static String columnFamily = ReadConf.getProperty(CF);
    private static final Object CONSTANT_VALUE = new Object();
    private static final String SEPARATOR = "SEPARATOR";
    private static Map<String, Object> existingTables = new ConcurrentHashMap<>();
    private static Admin admin;
//    private static HBaseTableManager manager=null;

    static {
        try {
            admin = HBaseConfig.getConn().getAdmin();
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                existingTables.put(tableName.toString(), CONSTANT_VALUE);
            }
        } catch (IOException e) {
            System.out.println("获取HBase已存在表失败");
            e.printStackTrace();
        }
    }

//    public static synchronized HBaseTableManager getManager(){
//        if (null==manager){
//            manager=new HBaseTableManager();
//        }
//        return manager;
//    }

    public static String getColumnFamily() {
        return columnFamily;
    }

    /**
     * @param tableName
     * @return boolean
     * @methodname isTableExists
     * @description 判断指定表是否已经存在
     * @author ChenXiang
     * @date 16:09,2019/4/17
     */
    public static boolean isTableExists(String tableName) {
        return existingTables.containsKey(tableName);
    }


    /**
     * @param tableName
     * @return void
     * @methodname addTableName
     * @description 向表名缓存中加入一个新表名
     * @author ChenXiang
     * @date 20:13,2019/4/17
     */
    public static void addTableName(String tableName) {
        existingTables.put(tableName, CONSTANT_VALUE);
    }


    /**
     * @param tableName
     * @return void
     * @methodname createTableByName
     * @description 根据表名创建表
     * @author ChenXiang
     * @date 20:30,2019/4/17
     */
    public static void createTableByName(String tableName) {
        if (!isTableExists(tableName)) {
            synchronized (existingTables) {
                if (isTableExists(tableName)) {
                    return;
                }
                /**
                 * HTableDescriptor已过期，将在HBase 3.0.0移除
                 */
                TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
                ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily));
                ColumnFamilyDescriptor columnFamilyDescriptor = columnFamilyDescriptorBuilder.build();
                builder.setColumnFamily(columnFamilyDescriptor);
                try {
                    admin.createTable(builder.build());
                    addTableName(tableName);
                } catch (TableExistsException e) {
                    System.out.println("HBase表：" + tableName + " 已经存在");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("HBase表：" + tableName + " 创建失败");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param tableName
     * @return boolean，true表示表不存在或者生工删除，false表示删除失败
     * @methodname dropTable
     * @description 按表名删除表
     * @author ChenXiang
     * @date 20:44,2019/4/17
     */
    public static boolean dropTable(String tableName) {
        try {
            if (!isTableExists(tableName)) {
                return true;
            } else {
                admin.disableTable(TableName.valueOf(tableName));
                admin.deleteTable(TableName.valueOf(tableName));
                existingTables.remove(tableName);
                return true;
            }
        } catch (IOException e) {
            System.out.println("删除表：" + tableName + " 失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param tableName
     * @return org.apache.hadoop.hbase.client.Table
     * @methodname getConnectionByTableName
     * @description 根据表名获取对应的连接
     * @author ChenXiang
     * @date 11:05,2019/4/15
     */
    public static Table getConnectionByTableName(String tableName) {
        Table table = null;
        try {
            if (isTableExists(tableName)) {
                if (HBaseConfig.getConn().getAdmin().tableExists(TableName.valueOf(tableName))) {
                    return HBaseConfig.getConn().getTable(TableName.valueOf(tableName));
                } else {
                    System.out.println("hbase中表" + tableName + "被异常删除，重新创建");
                    existingTables.remove(tableName);
                    createTableByName(tableName);
                    return HBaseConfig.getConn().getTable(TableName.valueOf(tableName));
                }
            } else {
                createTableByName(tableName);
                return HBaseConfig.getConn().getTable(TableName.valueOf(tableName));
            }
        }catch (IOException e) {
            System.out.println("HBase获取表连接失败");
            e.printStackTrace();
        }
        return null;
    }
}
