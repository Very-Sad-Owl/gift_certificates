package ru.clevertec.ecl.service;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;

import static ru.clevertec.ecl.config.common.ConfigPaths.*;
//@TestConfiguration
//@ActiveProfiles("test")
//@EnableJpaRepositories(basePackages = MAIN_ENTITIES_REPOSITORY_PATH,
//        entityManagerFactoryRef = "primaryEntityManagerFactory",
//        transactionManagerRef= "primaryTransactionManager")
@TestConfiguration
@EnableJpaRepositories(basePackages = MAIN_ENTITIES_REPOSITORY_PATH)
@EntityScan(MAIN_ENTITIES_PATH)
@EnableTransactionManagement
@AutoConfigureDataJpa
@ActiveProfiles("test")
public class PrimaryDbConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource1.configuration")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

}
