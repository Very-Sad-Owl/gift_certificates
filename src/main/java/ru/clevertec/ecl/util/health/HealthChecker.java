package ru.clevertec.ecl.util.health;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.ecl.exception.ErrorResponse;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@EnableScheduling
public class HealthChecker {

    @Value("#{${cluster}}")
    private Map<String, Integer> cluster;
    @Value("${server.port}")
    private int currentPort;
    private static final String APP_URL_PATTERN = "http://localhost:%d/actuator/health";
    private final RestTemplate restTemplate;

    @Autowired
    public HealthChecker(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 9000)
    public void healthCheck() {
        String checkerEndpointUrl;
        for (Map.Entry<String, Integer> node : cluster.entrySet()) {
            JsonNode resp = JsonNodeFactory.instance.objectNode();
            checkerEndpointUrl = String.format(APP_URL_PATTERN, node.getValue());
            try {
                resp = restTemplate.getForObject(checkerEndpointUrl, JsonNode.class);
                log.info(resp.toString());
            } catch (Exception e) {
                ((ObjectNode) resp).put(node.getKey(), " is unavailable");
                log.warn(resp.toString());
            }
        }
    }

    public ResponseEntity<String> healthCheckEndpoint() {
        String checkerEndpointUrl;
        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, Integer> node : cluster.entrySet()) {
            JsonNode resp = JsonNodeFactory.instance.objectNode();
            try {
                checkerEndpointUrl = String.format(APP_URL_PATTERN, node.getValue());
                resp = restTemplate.getForObject(checkerEndpointUrl, JsonNode.class);
                ((ObjectNode) resp).put("node_name", node.getKey());
                body.append(resp);
            } catch (Exception e) {
                ((ObjectNode) resp).put(node.getKey(), " is unavailable");
                body.append(resp);
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(body.toString());
    }


}
