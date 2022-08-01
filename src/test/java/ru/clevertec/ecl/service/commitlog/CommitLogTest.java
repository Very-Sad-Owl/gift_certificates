package ru.clevertec.ecl.service.commitlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.clevertec.ecl.service.common.DatabaseConstants.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {CommitLogConfiguration.class,
        CommitLogDbConfiguration.class,
        ClusterPropertiesConfiguration.class,
        CommonConfiguration.class})
public class CommitLogTest {

    @Autowired
    CommitLogWorker service;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ClusterProperties properties;

    @Test
    public void writeActionTest() {
        CommitLog toSave = CommitLog.builder()
                .action(Action.DELETE)
                .performedOnNode(8070)
                .tableTitle(ALIAS_TAGS)
                .actionTime(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();

        CommitLog saved = service.writeAction(toSave);

        assertEquals(toSave, saved);
    }

    @SneakyThrows
    @Test
    public void formLogNodeTest_fieldData_logNodeObject() {
        Action method = Action.SAVE;
        String jsonValue = objectMapper.writeValueAsString(Tag.builder().id(111).name("some tag").build());
        String table = ALIAS_TAGS;

        CommitLog expected = CommitLog.builder()
                .id(111)
                .action(method)
                .jsonValue(objectMapper.writeValueAsString(Tag.builder().id(111).name("some tag").build()))
                .tableTitle(table)
                .performedOnNode(8070)
                .build();

        CommitLog actual = service.formLogNode(method, jsonValue, table);

        assertEquals(expected, actual);
    }

    @Test
    public void readActionsToPerformTest_forTags_allActionsOnTagsFromGivenDate() {
        NodeStatus currentStatus = NodeStatus.builder()
                .recommendedToUpdateFrom(LocalDateTime.parse("2012-12-03T10:15:04"))
                .build();
        CommitLog firstNode = CommitLog.builder().id(2).action(Action.DELETE).performedOnNode(8070)
                .tableTitle(ALIAS_TAGS).actionTime(LocalDateTime.parse("2012-12-03T10:15:05"))
                .jsonValue("1").build();
        CommitLog secondNode = CommitLog.builder().id(3).action(Action.UPDATE).performedOnNode(8070)
                .tableTitle(ALIAS_TAGS).actionTime(LocalDateTime.parse("2013-12-03T10:15:14"))
                .jsonValue("{\"id\":1}").build();

        List<CommitLog> expected = Arrays.asList(secondNode, firstNode);

        List<CommitLog> actual = service.readActionsToPerform(currentStatus, ALIAS_TAGS);

        assertEquals(expected, actual);
    }

    @Test
    public void readActionsToPerformTest_forAllTables_allActionsFromGivenDate() {
        NodeStatus currentStatus = NodeStatus.builder()
                .recommendedToUpdateFrom(LocalDateTime.parse("2011-12-03T10:15:39"))
                .build();

        List<CommitLog> actual = service.readActionsToPerform(currentStatus);

        assertEquals(0, actual.size());
    }

    @Test
    public void getAllActionsAfterTimeTest_timeAndNodes_correspondingData() {
        List<CommitLog> expected = Arrays.asList(
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_TAGS).jsonValue("{\"id\":1}")
                        .actionTime(LocalDateTime.parse("2011-12-03T10:15:40")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_TAGS).jsonValue("1")
                        .actionTime(LocalDateTime.parse("2012-12-03T10:15:05")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_TAGS).jsonValue("{\"id\":1}")
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:14")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_ORDERS).jsonValue("{\"id\":2, surname: \"xdd\"}")
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_ORDERS).jsonValue("{\"id\":1, surname: \"xdd\"}")
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15")).performedOnNode(8090).build()
        );
        List<CommitLog> actual = service
                .getAllActionsOnNodesAfterTime(LocalDateTime.parse("2011-12-03T10:15:30"), Arrays.asList(8070, 8090));

        assertEquals(expected, actual);
    }

    @Test
    public void getAllActionsAfterTimeTest_timeAndNonExistingNodes_emptyData() {
        List<CommitLog> actual = service
                .getAllActionsOnNodesAfterTime(LocalDateTime.parse("2011-12-03T10:15:30"), Arrays.asList(8030, 8031));

        assertTrue(actual.isEmpty());
    }

    @Test
    public void sortNodesByTableTest_tagAndUserNodes_sortedMap() {
        List<CommitLog> allActions = Arrays.asList(
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_TAGS).jsonValue("{name: \"xd\"}")
                        .actionTime(LocalDateTime.parse("2011-12-03T10:15:40")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_TAGS).jsonValue("{name: \"xd\"}")
                        .actionTime(LocalDateTime.parse("2012-12-03T10:15:05")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_TAGS).jsonValue("{name: \"xd\"}")
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:14")).performedOnNode(8070).build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_ORDERS).jsonValue("{name: \"xd\", surname: \"xdd\"}")
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15")).performedOnNode(8070).build()
        );

        Map<String, List<CommitLog>> sortedNodes = service.sortNodesByTable(allActions);

        assertTrue(sortedNodes.get(ALIAS_TAGS).size() == 3 && sortedNodes.get(ALIAS_ORDERS).size() == 1);
    }
}
