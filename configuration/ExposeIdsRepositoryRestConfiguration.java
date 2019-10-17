package com.bdi.sselab.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.List;

/**
 * 暴露实体主键 id 配置类
 * ref: https://stackoverflow.com/questions/30912826/expose-all-ids-when-using-spring-data-rest?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */
@Configuration
public class ExposeIdsRepositoryRestConfiguration extends RepositoryRestConfigurerAdapter {

    @Resource(name = "systemEntityManagerFactory")
    private EntityManagerFactory entityManagerFactory1;

    @Resource(name = "dataEntityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    /**
     * 暴露所有实体 id
     *
     * @param config RepositoryRestConfiguration
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        List<Class<?>> entityClasses = getAllManagedEntityTypes();
        for (Class<?> entityClass : entityClasses) {
            config.exposeIdsFor(entityClass);
        }
    }

    /**
     * 获取所有平台数据实体类型
     *
     * @return 类型列表
     */
    private List<Class<?>> getAllManagedEntityTypes() {
        List<Class<?>> entityClasses = new ArrayList<>();
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        for (ManagedType<?> managedType : metamodel.getManagedTypes()) {
            Class<?> javaType = managedType.getJavaType();
            if (javaType.isAnnotationPresent(Entity.class)) {
                entityClasses.add(managedType.getJavaType());
            }
        }
        return entityClasses;
    }

    /**
     * 获取所有系统相关实体类型
     *
     * @return 类型列表
     */
    private List<Class<?>> getAllSystemManagedEntityTypes() {
        List<Class<?>> entityClasses = new ArrayList<>();
        Metamodel metamodel = entityManagerFactory1.getMetamodel();
        for (ManagedType<?> managedType : metamodel.getManagedTypes()) {
            Class<?> javaType = managedType.getJavaType();
            if (javaType.isAnnotationPresent(Entity.class)) {
                entityClasses.add(managedType.getJavaType());
            }
        }
        return entityClasses;
    }
}
