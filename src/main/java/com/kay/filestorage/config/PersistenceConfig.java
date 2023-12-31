package com.kay.filestorage.config;

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

@ConditionalOnMissingBean(name = FileStorageAutoConfig.PERSISTENCE_MANAGER)
public class PersistenceConfig {

    public static final String pakage = "com.kay.filestorage.persistence";

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

    @Bean
    public DataSource storageDataSource(FileStorageProperties properties) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:%s/h2database".formatted(properties.getPath()));
        dataSource.setUsername(properties.getH2User());
        dataSource.setPassword(properties.getH2Password());
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager storageTransactionManager(DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(storageEntityManager(dataSource).getObject());
        return transactionManager;
    }
}
