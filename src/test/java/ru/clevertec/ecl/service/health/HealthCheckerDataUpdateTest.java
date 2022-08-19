package ru.clevertec.ecl.service.health;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {HealthCheckerConfiguration.class,
        CommitLogDbConfiguration.class,
        ClusterPropertiesConfiguration.class,
        CommonConfiguration.class
})
public class HealthCheckerDataUpdateTest {

    @Autowired
    private HealthCheckerService healthCheckerService;

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
        node8092Status = new Status(8092, false);

        node8070 = NodeStatus.builder()
                .id(1)
                .nodeTitle("node8070")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8070Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();
        node8071 = NodeStatus.builder()
                .id(2)
                .nodeTitle("node8071")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8071Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-11-03T10:15:30"))
                .build();
        node8072 = NodeStatus.builder()
                .id(3)
                .nodeTitle("node8072")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8072Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();
        node8080 = NodeStatus.builder()
                .id(4)
                .nodeTitle("node8080")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8080Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();
        node8081 = NodeStatus.builder()
                .id(5)
                .nodeTitle("node8081")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8081Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();
        node8082 = NodeStatus.builder()
                .id(6)
                .nodeTitle("node8082")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8082Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();
        node8090 = NodeStatus.builder()
                .id(7)
                .nodeTitle("node8090")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8090Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-11-03T10:15:30"))
                .build();
        node8091 = NodeStatus.builder()
                .id(8)
                .nodeTitle("node8091")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8091Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:11:10"))
                .build();
        node8092 = NodeStatus.builder()
                .id(9)
                .nodeTitle("node8092")
                .lastUpdated(LocalDateTime.parse("2011-12-03T10:15:30"))
                .nodeStatus(node8092Status.isOk())
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();
    }

    @Test
    public void updateNodeStatusTest_nodeStatusObject_updatedEntity() {
        NodeStatus newStatus = NodeStatus.builder()
                .id(5)
                .nodeStatus(false)
                .nodeTitle("node8081")
                .lastUpdated(node8081.getLastUpdated())
                .recommendedToUpdateFrom(node8081.getRecommendedToUpdateFrom())
                .build();

        NodeStatus updated = healthCheckerService.updateNodeStatus(newStatus);

        assertEquals(newStatus, updated);
    }

    @Test
    public void updateNodeStatusTest_nodeStatusDataFromFalseToFalse_updatedEntity() {
        NodeStatus updated = healthCheckerService.updateNodeStatus(8071, false);

        assertTrue(updated.getId() == node8071.getId()
                && !updated.isNodeStatus()
                && updated.getRecommendedToUpdateFrom().equals(node8071.getRecommendedToUpdateFrom())
                && updated.getLastUpdated().isAfter(node8071.getLastUpdated()));
    }

    @Test
    public void updateNodeStatusTest_nodeStatusDataFromTrueToFalse_updatedEntity() {
        NodeStatus updated = healthCheckerService.updateNodeStatus(8081, false);

        assertTrue(updated.getId() == 5
                && !updated.isNodeStatus()
                && updated.getLastUpdated().isAfter(node8081.getLastUpdated())
                && updated.getRecommendedToUpdateFrom().equals(node8081.getLastUpdated()));
    }

    @Test
    public void updateNodeStatusTest_nodeStatusDataFromTrueToTrue_updatedEntity() {
        NodeStatus updated = healthCheckerService.updateNodeStatus(8080, true);

        assertTrue(updated.getId() == 4
                && updated.isNodeStatus()
                && updated.getLastUpdated().isAfter(node8080.getLastUpdated())
                && updated.getRecommendedToUpdateFrom().equals(updated.getLastUpdated()));
    }

    @Test
    public void updateNodeStatusTest_nonExistingNode_notFoundException() {
        int nodeToUpdate = 0;

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            healthCheckerService.updateNodeStatus(nodeToUpdate, true);
        });

        assertNotNull(thrown);
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
    private static NodeStatus node8070;
    private static NodeStatus node8071;
    private static NodeStatus node8072;
    private static NodeStatus node8080;
    private static NodeStatus node8081;
    private static NodeStatus node8082;
    private static NodeStatus node8090;
    private static NodeStatus node8091;
    private static NodeStatus node8092;
    LocalDateTime now = LocalDateTime.now();


}
