package com.bdi.sselab.hbase.table;

import com.bdi.sselab.hbase.hbaseUtils.FilterUtil;
import com.bdi.sselab.hbase.hbaseUtils.ScannUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ChenXiang
 * @Date 2019/04/29,10:46
 * @Description
 */
@Component
public class HBaseTable {
    private Table table;
    private String cf;

    public HBaseTable(String tableName) {
        table = HBaseTableManager.getConnectionByTableName(tableName);
        cf = HBaseTableManager.columnFamily;
    }
    public HBaseTable(){
    }

    /**
     * @param colName
     * @param rowKey
     * @param value
     * @return org.apache.hadoop.hbase.client.Put
     * @methodname toPut
     * @description 根据列族等参数获取put对象
     * @author ChenXiang
     * @date 11:09,2019/4/29
     */
    public Put toPut(String colName, String rowKey, String value) {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(colName), Bytes.toBytes(value));
        return put;
    }

    public boolean insert(Object object) {
        try {
            if (object instanceof List) {
                List<Put> puts = (List<Put>) object;
                table.put(puts);
                return true;
            } else if (object instanceof Put) {
                Put put = (Put) object;
                table.put(put);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.out.println("数据导入HBase失败");
            e.printStackTrace();
            return false;
        }
    }

    public Result[] getRows(ArrayList<Get> gets){
        try {
            return table.get(gets);
        } catch (IOException e) {
            System.out.println("读取HBase数据失败");
            e.printStackTrace();
            return null;
        }
    }

    public ResultScanner getScanner(Scan scan){
        try {
            return table.getScanner(scan);
        } catch (IOException e) {
            System.out.println("获取scanner失败");
            e.printStackTrace();
            return null;
        }
    }
}
