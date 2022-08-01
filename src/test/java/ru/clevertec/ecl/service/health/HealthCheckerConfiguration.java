package ru.clevertec.ecl.service.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.repository.commitlogrepository.NodeStatusRepository;
import ru.clevertec.ecl.util.health.HealthCheckerService;

@TestConfiguration
@ActiveProfiles("test")
public class HealthCheckerConfiguration {

    @Bean
    @Primary
    public HealthCheckerService healthCheckerService(ClusterProperties cp, RestTemplate rt, NodeStatusRepository ns, ObjectMapper objectMapper) {
        return new HealthCheckerService(cp, rt, ns, objectMapper);
    }
}
