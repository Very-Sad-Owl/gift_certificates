package ru.clevertec.ecl.service.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.CertificateRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.impl.CertificateServiceImpl;
import ru.clevertec.ecl.service.commitlog.CommitLogService;

@TestConfiguration
@EnableConfigurationProperties(ClusterProperties.class)
@ActiveProfiles("test")
public class CertificateServiceTestConfiguration {

    @Bean
    public CertificateService certificateService(ClusterProperties properties, CertificateRepository repository,
                                                 CertificateMapper mapper,
                                                 TagMapper tagMapper, TagService tagService,
                                                 CommitLogService commitLogService,
                                                 ObjectMapper objectMapper,
                                                 @Lazy OrderService orderService) {
        return new CertificateServiceImpl(properties, repository, mapper, tagMapper,
                tagService, commitLogService, objectMapper, orderService);
    }
}
