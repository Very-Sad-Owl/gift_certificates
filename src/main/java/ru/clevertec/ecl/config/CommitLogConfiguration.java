package ru.clevertec.ecl.config;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static ru.clevertec.ecl.config.common.ConfigPaths.COMMIT_LOG_ENTITIES_PATH;
import static ru.clevertec.ecl.config.common.ConfigPaths.COMMIT_LOG_REPOSITORY_PATH;

/**
 * Database configuration for performing commit log logic.
 *
 * @author Olga Mailychko
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = COMMIT_LOG_REPOSITORY_PATH,
        entityManagerFactoryRef = "secondaryEntityManagerFactory"
)
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
    public DataSource secondaryDataSource(@Qualifier("secondaryDataSourceProperties")
                                                      DataSourceProperties dataSourceProperties) {
        PGXADataSource ds =new PGXADataSource();
        ds.setUrl(dataSourceProperties.getUrl());
        ds.setUser(dataSourceProperties.getUsername());
        ds.setPassword(dataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xads2");
        return xaDataSource;
    }

    @Bean
    public EntityManagerFactoryBuilder commitLogEntityManagerFactoryBuilder() {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), additionalJpaProperties(), null);
        return builder;
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            @Qualifier("commitLogEntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource(secondaryDataSourceProperties()))
                .packages(COMMIT_LOG_ENTITIES_PATH)
                .persistenceUnit("postgres")
                .properties(additionalJpaProperties())
                .jta(true)
                .build();
    }

    public Map<String, ?> additionalJpaProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("hibernate.hbm2ddl.auto", ddlAuto);
        map.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        map.put("hibernate.show_sql", "true");
        map.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        map.put("javax.persistence.transactionType", "JTA");
        map.put("hibernate.transaction.coordinator class", "jta");

        return map;
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
