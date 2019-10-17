package com.bdi.sselab.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Created with qml
 * @author:qml
 * @Date:2019/5/28
 * @Time:13:14
 */
@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories(
        basePackages = "com.bdi.sselab.repository.StatisticBureau",
        entityManagerFactoryRef = "dataEntityManagerFactory",
        transactionManagerRef = "dataTransactionManager"
)
public class DataDBConfig {
    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.data")
    public DataSourceProperties dataDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataDataSource() {
        DataSourceProperties dataDataSourceProperties = dataDataSourceProperties();
        return DataSourceBuilder.create()
                .driverClassName(dataDataSourceProperties.getDriverClassName())
                .url(dataDataSourceProperties.getUrl())
                .username(dataDataSourceProperties.getUsername())
                .password(dataDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    public PlatformTransactionManager dataTransactionManager() {
        EntityManagerFactory factory = dataEntityManagerFactory().getObject();
        return new JpaTransactionManager(factory);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dataEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataDataSource());
        factory.setPackagesToScan("com.bdi.sselab.domain.StatisticBureau");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();

        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.hbm2ddl.auto"));
        jpaProperties.put("hibernate.show-sql", env.getProperty("spring.jpa.hibernate.show-sql"));
        jpaProperties.put("hibernate.physical_naming_strategy","org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

//    @Bean
//    public DataSourceInitializer dataDataSourceInitializer() {
//        DataSourceInitializer dsInitializer = new DataSourceInitializer();
////        dsInitializer.setDataSource(dataDataSource());
////        ResourceDatabasePopulator dbPopulor= new ResourceDatabasePopulator();
////        dbPopulor.addScript(new ClassPathResource("init.sql"));
////        dsInitializer.setDatabasePopulator(dbPopulor);
////        dsInitializer.setEnabled(env.getProperty("spring.datasource.data.initialize", Boolean.class, false));
//        return dsInitializer;
//    }
}
