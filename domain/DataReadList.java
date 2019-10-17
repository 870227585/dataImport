package com.bdi.sselab.domain;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
/**
 * @Author:Yankaikai
 * @Description:xml解析完成后的数据对象
 * @Date:Created in 2019/3/20
 */
@Data
@AllArgsConstructor
public class DataReadList {
    // 读取数据的开始行
    private int beginRow;
    // 最终的列
    private int endCol;
    // HBase中表名
    private String hbaseName;
    // 行键所在的列
    private int  rowKey;
   /**
    * 结构化数据表属性
   * */
    private List<String> tableProperty;
    /**
     * 表头数据的检查内容
    * */
    // excel和头部的检查内容
    private List<String> headCheckContent;
    //  表头数据检查的行
    private List<Integer> checkHeadRows;
    //  表头数据检查的列
    private List<Integer> checkHeadCols;

    /**
     * 结构数据的检查内容
     * */
    //  结构化数据检查的内容的行
    private List<Integer> checkDataRows;
    // 结构化数据检查内容的列
    private List<Integer> checkDataCols;
    // 结构化数据的检查数据
    private List<String> dataCheckContent;
    // 是否有表尾
    private boolean haveTableFinal;

    // 表的时间
    private String date;


    public DataReadList(){
    }
}
