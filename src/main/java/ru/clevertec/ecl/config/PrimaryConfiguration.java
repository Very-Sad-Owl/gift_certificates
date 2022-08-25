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
import org.springframework.context.annotation.Primary;
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

import static ru.clevertec.ecl.config.common.ConfigPaths.MAIN_ENTITIES_PATH;
import static ru.clevertec.ecl.config.common.ConfigPaths.MAIN_ENTITIES_REPOSITORY_PATH;

/**
 * Database configuration business logic entities.
 *
 * @author Olga Mailychko
 */
@Configuration
@EnableJpaRepositories(basePackages = MAIN_ENTITIES_REPOSITORY_PATH,
        entityManagerFactoryRef = "primaryEntityManagerFactory"
)
@EnableTransactionManagement
public class PrimaryConfiguration {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.orders")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.orders.configuration")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties postgresDataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setUrl(postgresDataSourceProperties.getUrl());
        ds.setUser(postgresDataSourceProperties.getUsername());
        ds.setPassword(postgresDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xads1");
        return xaDataSource;
    }

    @Bean
    public EntityManagerFactoryBuilder primaryManagerFactoryBuilder() {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder
                (new HibernateJpaVendorAdapter(), additionalJpaProperties(), null);
        return builder;
    }

    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryManagerFactoryBuilder") EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean build = builder
                .dataSource(primaryDataSource(primaryDataSourceProperties()))
                .packages(MAIN_ENTITIES_PATH)
                .persistenceUnit("postgres")
                .properties(additionalJpaProperties())
                .jta(true)
                .build();
        return build;
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
    public DataSourceInitializer primaryDataSourceInitializer(@Qualifier("primaryDataSource") final DataSource dataSource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("/default_data.sql"));
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

}
