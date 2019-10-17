package com.bdi.sselab.domain.StatisticBureau;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Author:Yankaikai
 * @Description:农村贫困监测调查
 * @Date:Created in 2019/5/27
 */
@ApiModel(description = "农村贫困监测调查")
@Data
@AllArgsConstructor
@Entity
public class RualSurvey implements Comparable<RualSurvey>{
    @ApiModelProperty(value = "主键")
    @Id
    @GeneratedValue
    private Long id;
    // 一级指标名称
    @ApiModelProperty(value = "一级指标名称")
    private String firstIndexName;
    // 指标分类
    @ApiModelProperty(value = "指标分类")
    private String indexType;
    // 二级指标名称
    @ApiModelProperty(value = "二级指标名称")
    private String secondIndexName;
    // 统计数据
    @ApiModelProperty(value = "统计数据")
    private String statisticData;
    // 单位
    @ApiModelProperty("单位")
    private String unit;
    // 数据来源相关部门
    @ApiModelProperty("数据来源相关部门")
    private String dataSourceDepart;
    // 权重
    @ApiModelProperty("权重")
    private int weight;
    @ApiModelProperty("日期")
    private String date;
    public RualSurvey(){
    }
    @Override
    public int compareTo(RualSurvey o) {
        return weight-o.getWeight();
    }
}
