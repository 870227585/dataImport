package com.bdi.sselab.dataLoad;

import com.bdi.sselab.hbase.hbaseUtils.FilterUtil;
import com.bdi.sselab.hbase.hbaseUtils.ScannUtil;
import com.bdi.sselab.hbase.table.HBaseTable;
import com.bdi.sselab.hbase.table.HBaseTableManager;
import com.bdi.sselab.utils.ConnectMysqlUtils;
import com.bdi.sselab.utils.ReadConf;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ChenXiang
 * @Date 2019/05/02,14:07
 * @Description
 */
public class FromHbaseToMysql {
    private static ResultScanner getDataFromHbase(String tableName, List<String> cols) throws IOException {
        if (!HBaseTableManager.isTableExists(tableName)) {
            System.out.println("表" + tableName + "尚未导入");
            return null;
        }
        List<String> cols2 = new ArrayList<>();
        for(String col:cols){
            if(col.endsWith("/long")){
                cols2.add(col.split("/long")[0]);
            }
            else if(col.endsWith("/int")){
                cols2.add(col.split("/int")[0]);
            }
            else if(col.endsWith("/double")){
                cols2.add(col.split("/double")[0]);
            }
            else {
                cols2.add(col);

            }
        }
        HBaseTable hBaseTable = new HBaseTable(tableName);
        FilterList colFilters = FilterUtil.createColFilters(cols2);
        Scan scan = ScannUtil.getAScan();
        scan.setFilter(colFilters);
        return hBaseTable.getScanner(scan);
    }


    public static void loadDataToMysql(Map<String, List<String>> map) {
        ResultScanner scanner = null;
        ConnectMysqlUtils connectMysqlUtils = new ConnectMysqlUtils();
        String[] keys = ReadConf.getProperty("PRIMARY_KEY").split("/");
        Map<String, String> colToData = new HashMap<>();
        for (String tableName : map.keySet()) {
            try {
                System.out.println("insert into table"+tableName);
                scanner = getDataFromHbase(tableName, map.get(tableName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null == scanner) {
                continue;
            }
//            System.out.println("result.size(): " + scanner.iterator().next());
            for (Result result : scanner) {
                for (Cell cell : result.rawCells()) {
                    //String[] rowKey = Bytes.toString(CellUtil.cloneRow(cell)).split("-");
                    //String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
                    //String date = rowKey[0];
                    //System.out.println(rowKey);
                    //colToData.put(keys[0], date);
                    //String key = rowKey[1];
                    //把总计和小计等数据去掉
//                    if (key.equals("小计")||key.equals("总计")){
//                        colToData.clear();
//                        continue;
//                    }
                    //colToData.put(keys[1], key);
                    colToData.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
//                    if (Bytes.toString(CellUtil.cloneValue(cell)).equals("小计")||Bytes.toString(CellUtil.cloneValue(cell)).equals("总计")){
//                        colToData.clear();
//                        continue;
//                    }
                }
//                System.out.println(colToData);
                connectMysqlUtils.writeDataIntoMysql(tableName, colToData);
                /**
                 * 清空清空
                 */
                colToData.clear();
            }
        }
        try {
            scanner.close();
        } catch (NullPointerException e) {
            System.out.println("scanner为空");
            e.printStackTrace();
        }

    }
}
