package ru.clevertec.ecl.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.service.health.HealthCheckerConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {ClusterProperties.class, HealthController.class,
        HealthCheckerConfiguration.class, CommonConfiguration.class, CommitLogDbConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "application-test.yml")
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class HealthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClusterProperties properties;

    @Test
    public void healthCheckEndpointTest_onlyCurrentIsActive_statusList() {
        List<Map<String, Object>> expected = properties.getCluster()
                .entrySet().stream()                    // Stream over entry set
                .flatMap(entrySet -> entrySet.getValue().stream())
                .map(v -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap();
                    map.put("port", v);
                    if (v != properties.getPort()) {
                        map.put("ok", false);
                    } else {
                        map.put("ok", true);
                    }
                    return map;
                })
                .collect(Collectors.toList());

        Object actual = this.restTemplate
                .getForObject("http://localhost:" + port + "/status", Object.class);

        assertTrue(((List<Map<String, Object>>)actual).containsAll(expected));
    }
}
