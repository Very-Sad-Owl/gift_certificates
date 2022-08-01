package ru.clevertec.ecl.service.commitlog;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.repository.commitlogrepository.CommitLogRepository;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;

@TestConfiguration
@ActiveProfiles("test")
public class CommitLogConfiguration {

    @Bean
    public CommitLogWorker commitLogService(CommitLogRepository repository, ClusterProperties properties) {
        return new CommitLogWorker(repository, properties);
    }
}
