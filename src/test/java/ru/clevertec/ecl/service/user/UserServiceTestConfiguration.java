package ru.clevertec.ecl.service.user;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.repository.entityrepository.UserRepository;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.service.impl.UserServiceImpl;

@TestConfiguration
@EnableConfigurationProperties(ClusterProperties.class)
@ActiveProfiles("test")
public class UserServiceTestConfiguration {

    @Bean
    public UserService userService(ClusterProperties properties, UserRepository repository, UserMapper mapper) {
        return new UserServiceImpl(properties, repository, mapper);
    }
}
