package com.bdi.sselab.excel;
import com.bdi.sselab.dataLoad.DoLoad;
import com.bdi.sselab.domain.DataReadList;
import com.bdi.sselab.domain.log.Log;
import com.bdi.sselab.domain.log.SchemeStatus;
import com.bdi.sselab.hbase.table.HBaseTable;
import com.bdi.sselab.hbase.table.HBaseTableManager;
import com.bdi.sselab.repository.departmentLog.SchemeStatusRepository;
import org.apache.hadoop.hbase.client.Put;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author:Yankaikai
 * @Description:
 * @Date:Created in 2019/5/4
 */
public class ReadDataInHbase {
    // 模板路径
    public Path templatePath;
    private static Workbook workbook;
    // Excel解析完成后的数据对象。
    private List<DataReadList> datas;

    private SchemeStatusRepository schemeStatusRepository;

    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    private Map<CellRangeAddress,String> mergeMap;
    public ReadDataInHbase(Path templatePath, List<DataReadList> lists, Log log, SchemeStatusRepository ssr){
        this.templatePath=templatePath;
        this.datas=lists;
        this.schemeStatusRepository = ssr;
        mergeMap=new HashMap<>();
        readStructureData(log);
    }
    public ReadDataInHbase(){}
    /**
     * @Author：Yankaikai
     * @Description:判断文件的类型
     * @Date: 2019/3/22
     * @param: 无
     */
    public Workbook getReadWorkBookType(Path templatePath){
        try {
            if(templatePath.toString().toLowerCase().endsWith("xlsx")){
                workbook=new XSSFWorkbook(new FileInputStream(templatePath.toFile()));
                return workbook;
            }else if(templatePath.toString().toLowerCase().endsWith("xls")){
                workbook=new HSSFWorkbook(new FileInputStream(templatePath.toFile()));
                return workbook;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
    * 结构化数据的读取操作：多Sheet读取
    * */
    private void readStructureData(Log log){
        int nums = 0, num = 0;
        try {
            for(int i=0;i<datas.size();i++){
                Sheet sheet=workbook.getSheetAt(i);
                DataReadList data=datas.get(i);
                num = readTableData(data,sheet);
                if ( num > 1) {
                    nums += num;
                }else {
                    System.out.println();
                }
                //更新数据库中表状态
                updataSchemeStatus(data);
            }
            workbook.close();
            log.setStatus("入库成功");
            log.setDataNums(nums);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
    * 读取Excel中的数据
    * */
    public int readTableData(DataReadList data,Sheet sheet){
        try {
            System.out.println("开始读取数据....");
            List<CellRangeAddress> merges=sheet.getMergedRegions();
            for(CellRangeAddress cellRangeAddress:merges){
                Row row=sheet.getRow(cellRangeAddress.getFirstRow());
                row.getCell(cellRangeAddress.getFirstColumn()).setCellType(CellType.STRING);
                mergeMap.put(cellRangeAddress,row.getCell(cellRangeAddress.getFirstColumn()).getStringCellValue());
              /*  System.out.println(row.getCell(cellRangeAddress.getFirstColumn()).getCellTypeEnum());
                CellType cellType = row.getCell(cellRangeAddress.getFirstColumn()).getCellTypeEnum();
                if (cellType.equals(CellType.NUMERIC)) {
                    mergeMap.put(cellRangeAddress,String.valueOf(row.getCell(cellRangeAddress.getFirstColumn()).getNumericCellValue()));
                }else if(cellType.equals(CellType.STRING)) {
                    mergeMap.put(cellRangeAddress,row.getCell(cellRangeAddress.getFirstColumn()).getStringCellValue());
                }else {
                    System.out.println(cellType);
                }*/
            }
            int i = 3;
            int num = 0;
            int beginRow=data.getBeginRow();
            boolean haveTableFinal=data.isHaveTableFinal();
            String hbaseName=data.getHbaseName();
            HBaseTable hBaseTable=new HBaseTable(hbaseName);
            while (!HBaseTableManager.isTableExists(hbaseName) && i>0 ) {
                System.out.println("Hbase"+hbaseName+"表创建中!");
                Thread.sleep(10);
                i--;
            }
            if (i>0) {
                System.out.println("Hbase"+hbaseName+"表创建成功!");
            }else {
                System.out.println("Hbase" + hbaseName + "表未创建成功");
            }
            if(haveTableFinal){
                List<Integer> checkHeadRows=data.getCheckHeadRows();
                int endRow=checkHeadRows.get(checkHeadRows.size()-1);
                for(int m=beginRow;m<=endRow;m++){
                    Row row=sheet.getRow(m);
                    if(row!=null){
                        readData(row,data,hBaseTable,m);
                        num++;
                    }else {
                        break;
                    }
                }
                System.out.println("结构化数据入库成功!");
            }else {
                while (true){
                    Row row=sheet.getRow(beginRow);
                    if(row!=null){
                        boolean flag=readData(row,data,hBaseTable,beginRow);
                        beginRow++;
                        num++;
                        if(flag){
                            break;
                        }
                    }else {
                        break;
                    }
                }
                System.out.println("结构化数据入库成功!");
            }
            return num;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
      /**
      * 读取表格中的一行数据
      * */
    public boolean  readData(Row row,DataReadList data,HBaseTable hBaseTable,int beginRow){
        int endCols=data.getEndCol();
        int rowkey=data.getRowKey();
        List<String> tableProperty=data.getTableProperty();
        List<Put> listsPut=new ArrayList<>();
        // Excel表的日期
        String date=data.getDate();
        int num=0;
        // 读取一行数据
        for(int j=0;j<=endCols;j++){
            if(row.getCell(j)!=null){
                // 读取当前的行键
//                row.getCell(rowkey).setCellType(Cell.CELL_TYPE_STRING);
//                String rowKeyData= row.getCell(rowkey).getStringCellValue();
//                if(rowKeyData.equals("")){
//                    continue;
//                }
                row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                String readData="";
                if(mergeMap.size()!=0) {
                    for (Map.Entry<CellRangeAddress, String> entry : mergeMap.entrySet()) {
                        if (entry.getKey().containsColumn(j) && entry.getKey().containsRow(beginRow)) {
                            readData = entry.getValue();
                            break;
                        }else {
                            readData = row.getCell(j).getStringCellValue();
                        }
                    }
                }else {
                    readData = row.getCell(j).getStringCellValue();
                }
                String s = date + beginRow;
                System.out.println(s);
                System.out.print(readData+"- ");
                Put put=hBaseTable.toPut(tableProperty.get(j),s,readData);
                listsPut.add(put);
                if(readData==null||readData.equals("")){
                    num++;
                }
            }
        }
//        System.out.println();
        boolean flag=false;
        if(num==endCols+1 || listsPut.isEmpty()){
            flag=true;
        }
        if (!listsPut.isEmpty()) {
            //添加数据上传时间
            String loadTime = df.format(new Date()) + beginRow;
            listsPut.add(hBaseTable.toPut("loadTime", date + beginRow, loadTime));
            //添加数据填报日期
            listsPut.add(hBaseTable.toPut("date",date + beginRow, date));

        }

        // 入库
        if (flag != true) {
            hBaseTable.insert(listsPut);
        }
        return  flag;
    }

    public void updataSchemeStatus(DataReadList data) {
        String tableName = data.getHbaseName();
//        System.out.println("tableName：  " + tableName);
        if (schemeStatusRepository.existsByName(tableName)) {
            SchemeStatus ss = schemeStatusRepository.findByName(tableName);
            ss.setStatus(true);
            DoLoad.status = 1;
            schemeStatusRepository.save(ss);
        }
        DoLoad.status = 1;
    }
}
