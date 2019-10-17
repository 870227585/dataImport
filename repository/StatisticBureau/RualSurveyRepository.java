package com.bdi.sselab.repository.StatisticBureau;
import com.bdi.sselab.domain.StatisticBureau.RualSurvey;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface RualSurveyRepository extends CommonRepository<RualSurvey,Long> {

    @Query(value = "select rual_survey.date from rual_survey",nativeQuery = true)
    List<String> getAllDate();
}
