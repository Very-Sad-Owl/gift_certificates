package ru.clevertec.ecl.service.commitlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    CommitLogService service;
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
                .entityId(1)
                .jsonValue("xd")
                .actionTime(LocalDateTime.parse("2011-12-03T10:15:30"))
                .build();

        CommitLog saved = service.writeAction(toSave);

        assertEquals(toSave, saved);
    }

    @SneakyThrows
    @Test
    public void formLogNodeTest_fieldData_logNodeObject() {
        Action method = Action.SAVE;
        TagDto value = TagDto.builder().id(111).name("some tag").build();
        String table = ALIAS_TAGS;

        CommitLog expected = CommitLog.builder()
                .id(111)
                .action(method)
                .jsonValue(objectMapper.writeValueAsString(Tag.builder().id(111).name("some tag").build()))
                .tableTitle(table)
                .entityId(111)
                .performedOnNode(8070)
                .build();

        CommitLog actual = service.formLogNode(method, value, table);

        assertEquals(expected, actual);
    }


    @Test
    public void getActionsAfterAfterIdTest_from0TagsTableNodes7070s_allActionsFromGivenId() {
        List<CommitLog> actual = service.getActionsAfterIdForTable(0, ALIAS_TAGS,
                Arrays.asList(8070, 8071, 8072));

        assertEquals(3, actual.size());
    }

    @Test
    public void getActionsAfterAfterIdTest_fromId1OnOrdersOnNodes8080s_allActionsFromGivenId() {
        List<CommitLog> expected = Arrays.asList(
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{\"id\":2, surname: \"xdd2\"}").build(),
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8080)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd3\"}")
                        .build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8081)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":2, surname: \"xdd}\"")
                        .build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd33}\"")
                        .build()
        );
        List<CommitLog> actual = service
                .getActionsAfterIdForTable(1, ALIAS_ORDERS, Arrays.asList(8080, 8081, 8082));

        assertEquals(expected, actual);
    }

    @Test
    public void getActionsAfterAfterIdTest_AfterId10OnTableCertificatesOnNodes8090s_emptyData() {
        List<CommitLog> actual = service
                .getActionsAfterIdForTable(1, ALIAS_CERTIFICATES, Arrays.asList(8090, 8091, 8092));

        assertTrue(actual.isEmpty());
    }

    @Test
    public void mergeUpdateAndSaveActionsTest_saveAndUpdateNodesOnSameEntity_mergedActions() {
        List<CommitLog> actions = Arrays.asList(
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{\"id\":2, surname: \"xdd2\"}").build(),
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8080)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd3\"}")
                        .build(),
                CommitLog.builder().action(Action.UPDATE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8081)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":2, surname: \"xdd}\"")
                        .build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd33}\"")
                        .build()
        );

        List<CommitLog> expected = Arrays.asList(
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8080)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd3\"}")
                        .build(),
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8081)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":2, surname: \"xdd}\"")
                        .build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd33}\"")
                        .build()
        );

        List<CommitLog> actual = service.mergeUpdateAndSaveActions(actions);

        assertEquals(expected, actual);
    }

    @Test
    public void mergeUpdateAndSaveActionsTest_noActionsTiMerge_sameActions() {
        List<CommitLog> expected = Arrays.asList(
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{\"id\":2, surname: \"xdd2\"}").build(),
                CommitLog.builder().action(Action.SAVE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8080)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd3\"}")
                        .build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_ORDERS).entityId(2)
                        .performedOnNode(8081)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":2, surname: \"xdd}\"")
                        .build(),
                CommitLog.builder().action(Action.DELETE).tableTitle(ALIAS_ORDERS).entityId(3)
                        .performedOnNode(8082)
                        .actionTime(LocalDateTime.parse("2013-12-03T10:15:15"))
                        .jsonValue("{id\":3, surname: \"xdd33}\"")
                        .build()
        );

        List<CommitLog> actual = service.mergeUpdateAndSaveActions(expected);

        assertEquals(expected, actual);
    }
}
