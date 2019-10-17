package com.bdi.sselab.repository.StatisticBureau;

import com.bdi.sselab.domain.StatisticBureau.SocailContributionProportion;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author:Yankaikai
 * @Description:县职工社会保障缴费比例仓库类
 * @Date:Created in 2019/5/27
 */
public interface SocailContributionProportionRepository extends CommonRepository<SocailContributionProportion,Long> {
    @Query(value = "select socail_contribution_proportion.date from socail_contribution_proportion",nativeQuery = true)
    List<String> getAllDate();
}
