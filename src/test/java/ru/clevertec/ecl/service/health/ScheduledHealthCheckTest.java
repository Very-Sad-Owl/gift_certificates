package ru.clevertec.ecl.service.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.ecl.exception.GlobalDefaultExceptionHandler;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.util.health.Status;
import ru.clevertec.ecl.util.health.HealthCheckerService;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@ActiveProfiles("test")
@SpringBootTest(classes = {HealthCheckerConfiguration.class,
        CommitLogDbConfiguration.class,
        ClusterPropertiesConfiguration.class,
        CommonConfiguration.class, GlobalDefaultExceptionHandler.class
})
public class ScheduledHealthCheckTest {

    @Autowired
    private HealthCheckerService healthCheckerService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeAll
    public static void setup() {
        node8070Status = new Status(8070, true);
        node8071Status = new Status(8071, false);
        node8072Status = new Status(8072, true);

        node8080Status = new Status(8080, true);
        node8081Status = new Status(8081, true);
        node8082Status = new Status(8082, true);

        node8090Status = new Status(8090, false);
        node8091Status = new Status(8091, false);
        node8092Status = new Status(8092, true);
    }

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @SneakyThrows
    @Test
    public void healthCheckTest_forAllNodes_loggingMessages(CapturedOutput capturedOutput) {
        ObjectNode node8070Answer = JsonNodeFactory.instance.objectNode();
        node8070Answer.put("isOK", "true");
        ObjectNode node8071Answer = JsonNodeFactory.instance.objectNode();
        node8071Answer.put("isOK", "false");
        ObjectNode node8072Answer = JsonNodeFactory.instance.objectNode();
        node8072Answer.put("isOK", "true");
        ObjectNode node8080Answer = JsonNodeFactory.instance.objectNode();
        node8080Answer.put("isOK", "true");
        ObjectNode node8081Answer = JsonNodeFactory.instance.objectNode();
        node8081Answer.put("isOK", "true");
        ObjectNode node8082Answer = JsonNodeFactory.instance.objectNode();
        node8082Answer.put("isOK", "true");
        ObjectNode node8090Answer = JsonNodeFactory.instance.objectNode();
        node8090Answer.put("isOK", "false");
        ObjectNode node8091Answer = JsonNodeFactory.instance.objectNode();
        node8091Answer.put("isOK", "false");
        ObjectNode node8092Answer = JsonNodeFactory.instance.objectNode();
        node8092Answer.put("isOK", "true");

        List<String> expected = Stream.of(
                "node8070 is ok",
                "node8071 is down",
                "node8072 is ok",
                "node8080 is ok",
                "node8081 is ok",
                "node8082 is ok",
                "node8090 is down",
                "node8091 is down",
                "node8092 is ok")
                .collect(Collectors.toList());

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8070/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8070Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8071/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8071Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8072/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8072Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8080/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8080Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8081/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8081Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8082/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8082Answer))
                );
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8090/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8090Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8091/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8091Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8092/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8092Answer))
                );

        healthCheckerService.healthCheck();

        mockServer.reset();

        List<Boolean> collect = expected.stream()
                .map(v -> capturedOutput.getOut().contains(v))
                .collect(Collectors.toList());
        assertFalse(collect.contains(false));
    }

    @SneakyThrows
    @Test
    public void healthCheckTest_forOneDownNode_loggingMessages(CapturedOutput capturedOutput) {
        ObjectNode node8070Answer = JsonNodeFactory.instance.objectNode();
        node8070Answer.put("isOK", "true");
        ObjectNode node8071Answer = JsonNodeFactory.instance.objectNode();
        node8071Answer.put("isOK", "false");
        ObjectNode node8072Answer = JsonNodeFactory.instance.objectNode();
        node8072Answer.put("isOK", "true");
        ObjectNode node8080Answer = JsonNodeFactory.instance.objectNode();
        node8080Answer.put("isOK", "true");
        ObjectNode node8081Answer = JsonNodeFactory.instance.objectNode();
        node8081Answer.put("isOK", "true");
        ObjectNode node8082Answer = JsonNodeFactory.instance.objectNode();
        node8082Answer.put("isOK", "true");
        ObjectNode node8090Answer = JsonNodeFactory.instance.objectNode();
        node8090Answer.put("isOK", "false");
        ObjectNode node8091Answer = JsonNodeFactory.instance.objectNode();
        node8091Answer.put("isOK", "false");
        ObjectNode node8092Answer = JsonNodeFactory.instance.objectNode();
        node8092Answer.put("isOK", "false");

        List<String> expected = Stream.of(
                "node8070 is ok",
                "node8071 is down",
                "node8072 is ok",
                "node8080 is ok",
                "node8081 is ok",
                "node8082 is ok",
                "node8090 is down",
                "node8091 is down",
                "node8092 is down")
                .collect(Collectors.toList());

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8070/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8070Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8071/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8071Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8072/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8072Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8080/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8080Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8081/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8081Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8082/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8082Answer))
                );
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8090/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8090Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8091/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(node8091Answer))
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8092/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        healthCheckerService.healthCheck();

        mockServer.reset();

        List<Boolean> collect = expected.stream()
                .map(v -> capturedOutput.getOut().contains(v))
                .collect(Collectors.toList());
        assertFalse(collect.contains(false));
    }


    private static Status node8070Status;
    private static Status node8071Status;
    private static Status node8072Status;
    private static Status node8080Status;
    private static Status node8081Status;
    private static Status node8082Status;
    private static Status node8090Status;
    private static Status node8091Status;
    private static Status node8092Status;


}
