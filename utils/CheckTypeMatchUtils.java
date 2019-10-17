package com.bdi.sselab.utils;
import com.bdi.sselab.domain.DataReadList;
import com.bdi.sselab.excel.ReadDataInHbase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.nio.file.Path;
import java.util.List;
/**
 * @Author:Yankaikai
 * @Description:判断上传的类型
 * @Date:Created in 2019/3/27
 */
public class CheckTypeMatchUtils {
    private static Workbook workbook;
    // Excel表格的sheet对象
    private static Sheet sheet;
    /**
    * @Author：Yankaikai
    * @Description:判断上传的Excel文件类型和当前的xml文件是否匹配。
    * @Date: 2019/3/27
    * @param: Excel文件路径(excelPath),xml文件的名字(xmlName)
    */
    public static boolean checkType(List<DataReadList> lists,Path excelPath){
        // 用来标识非结构化数据的检查结果
        boolean headFlag=false;
        // 用来标识结构化数据的检查结果
        boolean dataFlag=false;
        workbook = new ReadDataInHbase().getReadWorkBookType(excelPath);
        //多个sheel对象的读取。
        for(int m=0;m<lists.size();m++) {
            DataReadList data=lists.get(m);
            sheet = workbook.getSheetAt(m);
            // 非结构化数据的检查内容
            List<Integer> checkHeadRows = data.getCheckHeadRows();
            List<String> headCheckContent = data.getHeadCheckContent();
            List<Integer> checkHeadcols = data.getCheckHeadCols();
            // 结构化数据的检查内容列
            List<Integer> colsList = data.getCheckDataCols();
//            System.out.println("对应的execl中的列：" + colsList.toString());
            // 结构化数据的检查内容行
            List<Integer> checkDataRow = data.getCheckDataRows();
            // 检查内容
            List<String> dataCheckContent = data.getDataCheckContent();
            // 检查非结构化数据的内容
            if (checkHeadRows.size() == 0 || dataCheckContent.size() == 0 || checkHeadcols.size() == 0) {
                headFlag = true;
            } else {
                for (int i = 0; i < checkHeadRows.size(); i++) {
                    Row row = sheet.getRow(checkHeadRows.get(i));
                    if (row != null && row.getCell(checkHeadcols.get(i)) != null) {
                        row.getCell(checkHeadcols.get(i)).setCellType(Cell.CELL_TYPE_STRING);
                        String headData = row.getCell(checkHeadcols.get(i)).getStringCellValue();
                        if (headCheckContent.get(i).contains(headData)) {
                            headFlag = true;
                        } else {
                            headFlag = false;
                            return headFlag;
                        }
                    } else {
                        return headFlag;
                    }
                }
            }
            // 检查结构化数据的内容
            if (colsList.size() != 0 && dataCheckContent.size() != 0) {
                for (int j = 0; j < colsList.size(); j++) {
                    Row dataRow = sheet.getRow(checkDataRow.get(j));
                    if (dataRow != null) {
                        dataRow.getCell(colsList.get(j)).setCellType(Cell.CELL_TYPE_STRING);
                        String content = dataRow.getCell(colsList.get(j)).getStringCellValue();
                        System.out.println(dataCheckContent.get(j) + ":  " + content);
                        if (dataCheckContent.get(j).contains(content)) {
                            dataFlag = true;
                        } else {
                            dataFlag = false;
                            return dataFlag;
                        }
                    }else {
                        return dataFlag;
                    }
                }
            }
        }

        if(dataFlag&&headFlag){
            return true;
        }
        return false;
    }
}
