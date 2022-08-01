package ru.clevertec.ecl.webutils.clusterproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;

@TestConfiguration
@EnableConfigurationProperties(ClusterProperties.class)
public class ClusterPropertiesConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "server")
    public ClusterProperties clusterProperties() {
        return new ClusterProperties();
    }
}
