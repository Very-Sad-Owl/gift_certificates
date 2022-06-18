//package ru.clevertec.ecl.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DatabaseConfiguration {
//
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix="spring.datasource.orders")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix="spring.datasource.commitlog")
//    public DataSource commitLogDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @Autowired
//    @Primary
//    DataSourceTransactionManager primaryTransactionalManager(@Qualifier("primaryDataSource") DataSource datasource) {
//        return new DataSourceTransactionManager(datasource);
//    }
//
//    @Bean
//    @Autowired
//    DataSourceTransactionManager commitLogTransactionalManager(@Qualifier ("commitLogDataSource") DataSource datasource) {
//        return new DataSourceTransactionManager(datasource);
//    }
//}
