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
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class habaseToMysql {

        private static ResultScanner getDataFromHbase(String tableName, List<String> cols) throws IOException {
            if (!HBaseTableManager.isTableExists(tableName)) {
                System.out.println("表" + tableName + "尚未导入");
                return null;
            }
            List<String> cols2 = new ArrayList<>();
            for (String col : cols) {
                if (col.endsWith("/long")) {
                    cols2.add(col.split("/long")[0]);
                } else if (col.endsWith("/int")) {
                    cols2.add(col.split("/int")[0]);
                } else if (col.endsWith("/double")) {
                    cols2.add(col.split("/double")[0]);
                } else {
                    cols2.add(col);

                }
            }
            cols2.add("loadTime");
            HBaseTable hBaseTable = new HBaseTable(tableName);
            FilterList colFilters = FilterUtil.createColFilters(cols2);
            Scan scan = ScannUtil.getAScan();
            scan.setFilter(colFilters);
            return hBaseTable.getScanner(scan);
        }

        public static void loadDataToMysql(Map<String, List<String>> map) {
            //实例化数据导入表
            ConnectMysqlUtils connectMysqlUtils1 = new ConnectMysqlUtils("");
            //实例化导入信息状态表
            ConnectMysqlUtils connectMysqlUtils2 = new ConnectMysqlUtils();
            //System.out.println("Connect: "+connectMysqlUtils1+" "+connectMysqlUtils2);
            ResultScanner scanner = null;
            Map<String, String> colToData = new HashMap<>();
            for (String tableName : map.keySet()) {
                int count_table = 0;
                double time_table = 0;
                ResultSet resultSet = connectMysqlUtils2.getSchemeStatus("system_manager", tableName);
                //判断表是否有更新
                try {
                    double sysLoadTime = resultSet.getDouble(4);
                    ResultScanner scanner_copy = getDataFromHbase(tableName, map.get(tableName));
                    //查询hbase表中行数，删除要覆盖的数据。
                    count_table = rowCount(scanner_copy, connectMysqlUtils1, tableName, sysLoadTime);
                    System.out.println("count_table: " + count_table);
                    System.out.println("resultSet: " + resultSet.getInt(3));
                    if (resultSet.getByte(5) == 0) {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    scanner = getDataFromHbase(tableName, map.get(tableName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == scanner) {
                    System.out.println("scanner is null");
                    continue;
                }
                for (Result result1 : scanner) {
                    System.out.println("update into table");
                    for (Cell cell : result1.rawCells()) {
                        try {
                            colToData.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
                            String loadTimeKey = colToData.get("loadTime").substring(0,12);
                            colToData.remove("loadTime");
                            if (Double.valueOf(loadTimeKey) < resultSet.getDouble(4)) {
                                continue;
                            }

                            if (Double.valueOf(loadTimeKey) > time_table) {
                                time_table = Double.valueOf(loadTimeKey);
                            }
                            //colToData.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));

                        } catch (Exception e) {
                            continue;
                            //e.printStackTrace();
                        }

                        if (colToData.size() == 0) {
                            continue;
                        }
//                  //将符合条件的数据导入到mysql中
                        System.out.println("导入数据到Mysql");
                        connectMysqlUtils1.writeDataIntoMysql(tableName, colToData);
                        /**
                         * 清空清空
                         */
                        // connectMysqlUtils2.updateStatus("system_manager",tableName,count_table,time_table,"0");
                        colToData.clear();
                    }
                    //更新表状态信息
                    connectMysqlUtils2.updateStatus("system_manager", tableName, count_table, time_table, "0");
                }
                try {
                    if (scanner != null) {
                        scanner.close();
                    }
                } catch (NullPointerException e) {
                    System.out.println("scanner为空");
                    e.printStackTrace();
                }

            }
        }

    /**
     * 功能有二：
     * 一 查看hbase行数
     * 二 删除原有记录 根据hbase中loadTime属性，将loadTime值大于schemem_statue中timer的date值取出存入HashSet,删除mysql数据表中对应data值的行。
     * @param scanner
     * @param connectMysqlUtils
     * @param tableName
     * @param sysLoadTime
     * @return
     * @throws Exception
     */
    public static int rowCount(ResultScanner scanner,ConnectMysqlUtils connectMysqlUtils,String tableName,Double sysLoadTime) throws Exception{
        int rowCount =0;
        HashSet<String> set = new HashSet<String>();
        Map<String, String> firstLine = new HashMap<>();
        for(Result result:scanner){
            rowCount++;
            double loadTime = Double.valueOf(Bytes.toString(result.getValue(Bytes.toBytes("f"),Bytes.toBytes("loadTime"))).substring(0,12));
            String date = Bytes.toString(result.getValue(Bytes.toBytes("f"),Bytes.toBytes("date")));
           // System.out.println("loadTime: "+loadTime);
            if(loadTime>=sysLoadTime){
               // System.out.println("加数据");
                set.add(date);
            }
        }

        for(String dataValue:set){
            //System.out.println("dataValue: "+dataValue);
            if(dataValue!=null){
               // System.out.println("需要删除的data: "+dataValue);
                connectMysqlUtils.deletDataFromMysql(tableName,dataValue);
            }
        }
        return rowCount;
    }


    public static void main(String[] args){
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("subjectname");
        list.add("budgetarrange");
        map.put("caizhengju_draft_budget_for_county_general_public_revenue",list);
        loadDataToMysql(map);
    }

}
