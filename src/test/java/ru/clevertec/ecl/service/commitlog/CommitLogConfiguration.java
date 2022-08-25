package ru.clevertec.ecl.service.commitlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.repository.commitlogrepository.CommitLogRepository;

@TestConfiguration
@ActiveProfiles("test")
public class CommitLogConfiguration {

    @Bean
    public CommitLogService commitLogService(CommitLogRepository repository, ClusterProperties properties,
                                             ObjectMapper objectMapper) {
        return new CommitLogService(repository, properties, objectMapper);
    }
}
