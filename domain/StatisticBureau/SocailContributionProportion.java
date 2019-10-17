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
 * @Description:县职工社会保障缴费比例
 * @Date:Created in 2019/5/27
 */
@ApiModel(description = "县职工社会保障缴费比例")
@Data
@AllArgsConstructor
@Entity
public class SocailContributionProportion implements Comparable<SocailContributionProportion>{
    @Id
    @GeneratedValue
    @ApiModelProperty(value = "主键")
    private Long id;
    // 保险类型
    @ApiModelProperty(value = "保险类型")
    private String insuranceType;
    // 缴费比例类型:
    @ApiModelProperty(value = "缴费比例类型:个人和单位")
    private String payPercentType;
    // 计量单位
    @ApiModelProperty(value = "计量单位%")
    private String unitMeasurement;
    // 行政(参公) 单位
    @ApiModelProperty(value = "行政(参公) 单位")
    private String administrativeUnit;
    // 事业单位
    @ApiModelProperty(value = "事业单位")
    private String governmentUnit;
    // 国有企业
    @ApiModelProperty(value = "国有企业")
    private String stateEnterprise;
    // 其他企业
    @ApiModelProperty(value = "其他企业")
    private String otherEnterPrise;
    @ApiModelProperty(value = "数据来源单位")
    private String dataSourceUnit;
    // 日期
    @ApiModelProperty(value = "日期")
    private String date;
    // 权重
    @ApiModelProperty(value = "权重")
    private int weight;
    public SocailContributionProportion(){
    }

    @Override
    public int compareTo(SocailContributionProportion o) {
        return weight-o.getWeight();
    }
}
