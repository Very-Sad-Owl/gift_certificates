package ru.clevertec.ecl.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.TagRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.impl.TagServiceImpl;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;

@TestConfiguration
@EnableConfigurationProperties(ClusterProperties.class)
@ActiveProfiles("test")
public class TagServiceTestConfiguration {

    @Bean
    public TagService tagService(ClusterProperties props, TagRepository repo, TagMapper mapper,
                                 CommitLogWorker commitLogWorker,
                                 ObjectMapper om, @Lazy CertificateService certificateService) {
        return new TagServiceImpl(props, repo, certificateService, mapper, commitLogWorker, om);
    }
}
