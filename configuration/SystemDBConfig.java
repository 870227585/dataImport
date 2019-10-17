package com.bdi.sselab.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
 * @Time:12:41
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"com.bdi.sselab.repository.departmentLog", "com.bdi.sselab.repository.userDepart"},
        entityManagerFactoryRef = "systemEntityManagerFactory",
        transactionManagerRef = "systemTransactionManager"
)
public class SystemDBConfig {
    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.system")
    public DataSourceProperties systemDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource systemDataSource() {
        DataSourceProperties systemDataSourceProperties = systemDataSourceProperties();
        return DataSourceBuilder.create()
                .driverClassName(systemDataSourceProperties.getDriverClassName())
                .url(systemDataSourceProperties.getUrl())
                .username(systemDataSourceProperties.getUsername())
                .password(systemDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager systemTransactionManager() {
        EntityManagerFactory factory = systemEntityManagerFactory().getObject();
        return new JpaTransactionManager(factory);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean systemEntityManagerFactory() {
        String[] entityPackage = {"com.bdi.sselab.domain.log", "com.bdi.sselab.domain.user"};
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(systemDataSource());
        factory.setPackagesToScan(entityPackage);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();

        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        jpaProperties.put("hibernate.show-sql", env.getProperty("spring.jpa.show-sql"));
        jpaProperties.put("hibernate.physical_naming_strategy","org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

//    @Bean
//    public DataSourceInitializer systemDataSourceInitializer() {
//        DataSourceInitializer dsInitializer = new DataSourceInitializer();
////        dsInitializer.setDataSource(systemDataSource());
////        ResourceDatabasePopulator dbPopulor= new ResourceDatabasePopulator();
////        dbPopulor.addScript(new ClassPathResource("init.sql"));
////        dsInitializer.setDatabasePopulator(dbPopulor);
////        dsInitializer.setEnabled(env.getProperty("spring.datasource.system.initialize", Boolean.class, false));
//        return dsInitializer;
//    }
}
