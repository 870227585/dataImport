package com.bdi.sselab.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

/**
 * 投影配置类
 * ref: https://stackoverflow.com/questions/29376090/how-to-apply-spring-data-projections-in-a-spring-mvc-controllers
 */
@Configuration
public class ProjectionConfiguration {

    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }
}
