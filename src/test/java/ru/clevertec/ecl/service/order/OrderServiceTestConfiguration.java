package ru.clevertec.ecl.service.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.OrderMapper;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.repository.entityrepository.OrderRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.service.impl.OrderServiceImpl;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;

@TestConfiguration
@EnableConfigurationProperties(ClusterProperties.class)
@ActiveProfiles("test")
public class OrderServiceTestConfiguration {

    @Bean
    public OrderService orderService(ClusterProperties properties, OrderRepository repository, OrderMapper mapper,
                                     CertificateMapper certificateMapper, UserMapper userMapper,
                                     CertificateService certificateService, CommitLogWorker commitLogWorker,
                                     ObjectMapper objectMapper, UserService userService) {
        return new OrderServiceImpl(properties, repository, mapper, certificateMapper, userMapper,
                certificateService, commitLogWorker, objectMapper, userService);
    }
}
