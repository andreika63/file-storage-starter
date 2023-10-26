package com.kay.filestorage.config;

import com.kay.filestorage.persistence.PersistenceManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = PersistenceConfig.pakage,
        entityManagerFactoryRef = "storageEntityManager",
        transactionManagerRef = "storageTransactionManager"
)

@ConditionalOnMissingBean(PersistenceManager.class)
public class PersistenceConfig {

    public static final String pakage = "com.kay.filestorage.persistence";

    @ConditionalOnMissingBean
    @Bean
    public LocalContainerEntityManagerFactoryBean storageEntityManager(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(pakage);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        em.setJpaPropertyMap(properties);
        return em;
    }

    @ConditionalOnMissingBean
    @Bean
    public DataSource userDataSource(FileStorageProperties properties) {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:%s/h2database".formatted(properties.getPath()));
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    @ConditionalOnMissingBean
    @Bean
    public PlatformTransactionManager storageTransactionManager(DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(storageEntityManager(dataSource).getObject());
        return transactionManager;
    }
}
