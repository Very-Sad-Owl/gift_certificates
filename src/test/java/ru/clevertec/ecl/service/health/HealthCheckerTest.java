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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.exception.GlobalDefaultExceptionHandler;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.exception.ServerIsDownException;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.util.health.Status;
import ru.clevertec.ecl.util.health.HealthCheckerService;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {HealthCheckerConfiguration.class,
        CommitLogDbConfiguration.class,
        ClusterPropertiesConfiguration.class,
        CommonConfiguration.class, GlobalDefaultExceptionHandler.class
})
public class HealthCheckerTest {

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

    @Test
    public void getCurrentNodeStatusTest_existingNode_status() {
        NodeStatus expected = NodeStatus.builder().nodeStatus(true).nodeTitle("node8070")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();

        NodeStatus actual = healthCheckerService.getCurrentNodeStatus(8070);

        assertEquals(expected, actual);
    }

    @Test
    public void getCurrentNodeStatusTest_nonExistingNode_notFoundException() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            healthCheckerService.getCurrentNodeStatus(0);
        });

        assertNotNull(thrown);
    }

    @Test
    public void isAnyAliveTest_noAliveNodes_false() {
        List<Status> nodeStatuses = Arrays.asList(
                new Status(8070, false),
                new Status(8080, false),
                new Status(8090, false)
        );

        assertFalse(healthCheckerService.isAnyAlive(nodeStatuses));
    }

    @Test
    public void isAnyAliveTest_someAliveNodes_true() {
        List<Status> nodeStatuses = Arrays.asList(
                new Status(8070, false),
                new Status(8080, true),
                new Status(8090, true)
        );

        assertTrue(healthCheckerService.isAnyAlive(nodeStatuses));
    }

    @Test
    public void isAnyAliveTest_emptyList_false() {
        List<Status> nodeStatuses = new ArrayList<>();

        assertFalse(healthCheckerService.isAnyAlive(nodeStatuses));
    }

    @Test
    public void isAnyAliveTest_someAliveElements_true() {
        List<Status> nodes = Arrays.asList(node8070Status, node8071Status, node8072Status);
        assertTrue(healthCheckerService.isAnyAlive(nodes));
    }

    @Test
    public void isAnyAliveTest_noAliveElements_false() {
        List<Status> nodes = Arrays.asList(node8071Status, node8090Status, node8091Status);
        assertFalse(healthCheckerService.isAnyAlive(nodes));
    }

    @SneakyThrows
    @Test
    public void findAnyAliveNodeFromReplicas_availablePortExists_anyAvailablePort() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8070/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8072/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        int actual = healthCheckerService.findAnyAliveNodeFromReplicas(8070);
        assertEquals(8070, actual);
    }

    @SneakyThrows
    @Test
    public void findAnyAliveNodeFromReplicas_availablePortNotExists_serverIsDownException() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8092/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ServerIsDownException thrown = assertThrows(ServerIsDownException.class, () -> {
            healthCheckerService.checkAlive(8090);
        });

        mockServer.verify();
        mockServer.reset();

        assertNotNull(thrown);
    }

    @SneakyThrows
    @Test
    public void checkAlive_oneNodesAreDown_ServerIsDownException() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8080/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8081/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8082/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8092/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ServerIsDownException thrown = assertThrows(ServerIsDownException.class, () -> {
            healthCheckerService.checkAlive();
        });

        mockServer.verify();
        mockServer.reset();

        assertNotNull(thrown);
    }

    @SneakyThrows
    @Test
    public void checkAlive_nodes8070_aliveNodesList() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8070/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8072/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        List<Integer> actual = healthCheckerService.checkAlive(8070);

        mockServer.verify();
        mockServer.reset();

        assertEquals(2, actual.size());
    }

    @SneakyThrows
    @Test
    public void checkAlive_allNodes_aliveNodesList() {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8080/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8081/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8082/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8092/actuator/health")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        List<Integer> actual = healthCheckerService.checkAlive();

        mockServer.verify();
        mockServer.reset();

        assertEquals(3, actual.size());
    }

    @SneakyThrows
    @Test
    public void healthCheckEndpointTest_forSpecifiedNodes_8070sPortsData() {
        ObjectNode node8070Answer = JsonNodeFactory.instance.objectNode();
        node8070Answer.put("isOK", "true");
        ObjectNode node8071Answer = JsonNodeFactory.instance.objectNode();
        node8071Answer.put("isOK", "false");
        ObjectNode node8072Answer = JsonNodeFactory.instance.objectNode();
        node8072Answer.put("isOK", "true");

        Map<Integer, Status> expected = Stream.of(new Object[][]{
                {8070, node8070Status},
                {8071, node8071Status},
                {8072, node8072Status}
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (Status) data[1]));

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

        Map<Integer, Status> actual = healthCheckerService.healthCheckEndpoint(8070);

        mockServer.verify();
        mockServer.reset();

        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    public void healthCheckEndpointTest_forAllNodes_AllPortsData() {
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

        Map<Integer, Status> expected = Stream.of(new Object[][]{
                {8070, node8070Status},
                {8071, node8071Status},
                {8072, node8072Status},
                {8080, node8080Status},
                {8081, node8081Status},
                {8082, node8082Status},
                {8090, node8090Status},
                {8091, node8091Status},
                {8092, node8092Status}
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (Status) data[1]));

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

        Map<Integer, Status> actual = healthCheckerService.healthCheckEndpoint(0);

        mockServer.reset();

        assertEquals(expected, actual);
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
