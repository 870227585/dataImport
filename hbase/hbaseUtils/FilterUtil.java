package com.bdi.sselab.hbase.hbaseUtils;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;

/**
 * @Author ChenXiang
 * @Date 2019/05/04,21:24
 * @Description
 */
public class FilterUtil {

    /**
     * @methodname createColFilters
     * @description 根据传入的列名，生成对应的列名过滤器列表
     * @author ChenXiang
     * @date 22:42,2019/5/4
     * @param list
     * @return org.apache.hadoop.hbase.filter.FilterList
     */
    public static FilterList createColFilters(List<String> list){
        FilterList colFilters=new FilterList(FilterList.Operator.MUST_PASS_ONE);
        for (String string:list){
            colFilters.addFilter(createColFilter(string));
        }
        System.out.println("filters: "+colFilters);
        return colFilters;
    }

    /**
     * @methodname createColFilter
     * @description 返回一个列名过滤器
     * @author ChenXiang
     * @date 22:43,2019/5/4
     * @param col
     * @return org.apache.hadoop.hbase.filter.Filter
     */
    public static Filter createColFilter(String col){
        return new QualifierFilter(CompareOperator.EQUAL,new BinaryComparator(Bytes.toBytes(col)));
    }

    public static FilterList mustPassAllFilters(Filter... filters){
        return new FilterList(FilterList.Operator.MUST_PASS_ALL,filters);
    }
}