//package ru.clevertec.ecl.config;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import ru.clevertec.ecl.entity.commitlogentities.AbstractCommitLog;
//import ru.clevertec.ecl.entity.AbstractEntity;
//
//import javax.sql.DataSource;
//import java.util.Objects;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackageClasses = AbstractCommitLog.class,
//        entityManagerFactoryRef = "commitLogEntityManagerFactory",
//        transactionManagerRef = "commitLogTransactionManager"
//)
//public class CommitLogJpaConfiguration {
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean commitLogEntityManagerFactory(
//            @Qualifier("commitLogDataSource") DataSource dataSource,
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(dataSource)
//                .packages(AbstractCommitLog.class)
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager commitLogTransactionManager(
//            @Qualifier("commitLogEntityManagerFactory") LocalContainerEntityManagerFactoryBean todosEntityManagerFactory) {
//        return new JpaTransactionManager(Objects.requireNonNull(todosEntityManagerFactory.getObject()));
//    }
//
//}
