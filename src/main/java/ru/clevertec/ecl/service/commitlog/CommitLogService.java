package ru.clevertec.ecl.service.commitlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.repository.commitlogrepository.CommitLogRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class performing operations on commit logging.
 *
 * @author Olga Mailychko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommitLogService {

    /**
     * DAO
     */
    private final CommitLogRepository repository;
    /**
     * Properties of whole cluster
     */
    private final ClusterProperties clusterProperties;
    private final ObjectMapper mapper;

    /**
     * Performs saving logic on {@link CommitLog} entity
     *
     * @param commitLog entity to save
     * @return saved {@link CommitLog} object
     */
    public CommitLog writeAction(CommitLog commitLog) {
        return repository.save(commitLog);
    }

    /**
     * Method finds all {@link CommitLog} nodes after given id from given node port values and related to given table
     *
     * @param idFrom          id to retrieve from
     * @param nodes           {@link Collection} of node's port values to search in
     * @param table alias presented in {@link ru.clevertec.ecl.service.common.DatabaseConstants}
     *              of entity all found nodes must be related to
     * @return {@link List} of found {@link CommitLog} nodes
     */
    public List<CommitLog> getActionsAfterIdForTable(long idFrom, String table, List<Integer> nodes) {
        return repository.findAllByEntityIdAfterAndTableTitleAndPerformedOnNodeIn(idFrom, table, nodes);
    }

    /**
     * Forms {@link CommitLog} object from given data
     *
     * @param method action performed on entity represented by {@link Action} enum constant
     * @param value  entity affected by action
     * @param table  table in storage entity is stored in
     * @return result {@link CommitLog} node
     */
    @SneakyThrows
    public CommitLog formLogNode(Action method, AbstractModel value, String table) {
        return CommitLog.builder()
                .action(method)
                .actionTime(LocalDateTime.now())
                .jsonValue(mapper.writeValueAsString(value))
                .entityId(value.getId())
                .tableTitle(table)
                .performedOnNode(clusterProperties.getPort())
                .build();
    }

    /**
     * Merges save and update actions on same entity. If there are save and update actions performed on same
     * entity, 'update' action becomes 'save' action while 'save' action removes from list.
     *
     * @param actions list of actions to perform
     * @return result updated {@link CommitLog} node
     */
    public List<CommitLog> mergeUpdateAndSaveActions(List<CommitLog> actions) {
        List<Long> saveActions = actions.stream()
                .filter(action -> action.getAction().equals(Action.SAVE))
                .map(CommitLog::getEntityId)
                .collect(Collectors.toList());
        List<Long> updateActions = actions.stream()
                .filter(action -> action.getAction().equals(Action.UPDATE))
                .map(CommitLog::getEntityId)
                .collect(Collectors.toList());
        List<Long> savedEntitiesIdToMerge = saveActions.stream()
                .filter(updateActions::contains)
                .collect(Collectors.toList());
        return actions.stream()
                .map(action -> {
                    if (savedEntitiesIdToMerge.contains(action.getEntityId()) &&
                            action.getAction().equals(Action.SAVE)) {
                        action = null;
                    } else if (savedEntitiesIdToMerge.contains(action.getEntityId()) &&
                            action.getAction().equals(Action.UPDATE)) {
                        action.setAction(Action.SAVE);
                    }
                    return action;
                })
                .collect(Collectors.toList())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
