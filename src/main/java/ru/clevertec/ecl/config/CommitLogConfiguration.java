package ru.clevertec.ecl.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import static ru.clevertec.ecl.config.common.ConfigPaths.*;

/**
 * Database configuration for performing commit log logic.
 *
 * @author Olga Mailychko
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = COMMIT_LOG_REPOSITORY_PATH,
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef= "secondaryTransactionManager")
public class CommitLogConfiguration {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    @ConfigurationProperties("spring.datasource.commitlog")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.commitlog.configuration")
    public DataSource secondaryDataSource() {
        return secondaryDataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean build = builder
                .dataSource(secondaryDataSource())
                .packages(COMMIT_LOG_ENTITIES_PATH)
                .build();
        build.setJpaPropertyMap(new HashMap<>(Collections.singletonMap("hibernate.hbm2ddl.auto", ddlAuto)));
        return build;
    }

    @Bean
    public PlatformTransactionManager secondaryTransactionManager(
            final @Qualifier("secondaryEntityManagerFactory")
                    LocalContainerEntityManagerFactoryBean commitLogEntityManagerFactory) {
        return new JpaTransactionManager(commitLogEntityManagerFactory.getObject());
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("secondaryDataSource") final DataSource dataSource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("/node_statuses.sql"));
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
}
