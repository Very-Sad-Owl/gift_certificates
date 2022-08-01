package ru.clevertec.ecl.util.commitlog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.entity.commitlogentities.CommitLogComparator;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.repository.commitlogrepository.CommitLogRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.service.common.DatabaseConstants.*;

/**
 * A class performing operations on commit logging.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommitLogWorker {

    /**
     * DAO
     */
    private final CommitLogRepository repository;
    /**
     * Properties of whole cluster
     */
    private final ClusterProperties clusterProperties;

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
     * Method finds all {@link CommitLog} nodes after given time from given node port values
     *
     * @param from {@link LocalDateTime} to start search from
     * @param nodes {@link Collection} of node's port values to search in
     * @return {@link List} of found {@link CommitLog} nodes
     */
    public List<CommitLog> getAllActionsOnNodesAfterTime(LocalDateTime from, Collection<Integer> nodes) {
        return repository.findAllByActionTimeAfterAndPerformedOnNodeIn(from, nodes);
    }

    /**
     * Method finds all {@link CommitLog} nodes after given time from given node port values and related to given table
     *
     * @param from {@link LocalDateTime} to start search from
     * @param nodes {@link Collection} of node's port values to search in
     * @param relatedToEntity title of entity all found nodes must be related to
     * @return {@link List} of found {@link CommitLog} nodes
     */
    public List<CommitLog> getActionsAfterTimeAndNodeTitles(LocalDateTime from, Collection<Integer> nodes, String relatedToEntity) {
        return repository.findAllByActionTimeAfterAndPerformedOnNodeInAndTableTitle(from, nodes, relatedToEntity);
    }

    /**
     * Forms {@link CommitLog} object from given data
     *
     * @param method action performed on entity represented by {@link Action} enum constant
     * @param jsonValue affected by action entity as JSON string
     * @param table table in storage entity is stored in
     * @return result {@link CommitLog} node
     */
    public CommitLog formLogNode(Action method, String jsonValue, String table) {
        return CommitLog.builder()
                .action(method)
                .actionTime(LocalDateTime.now())
                .jsonValue(jsonValue)
                .tableTitle(table)
                .performedOnNode(clusterProperties.getPort())
                .build();
    }

    /**
     * Finds {@link CommitLog} actions needed to be performed depending on current node's status only on specified table
     *
     * @param currentStatus current node's status represented as {@link NodeStatus} object
     * @param relatedTableTitle title of table(or entity) some writing action has been performed on
     * @return {@link List} of {@link CommitLog} nodes
     */
    public List<CommitLog> readActionsToPerform(NodeStatus currentStatus, String relatedTableTitle) {
        return this
                .getActionsAfterTimeAndNodeTitles
                        (currentStatus.getRecommendedToUpdateFrom(),
                                clusterProperties.getCluster()
                                        .get(clusterProperties
                                                .defineNodeByPort(clusterProperties.getPort())),
                                relatedTableTitle)
                .stream()
                .distinct()
                .sorted(new CommitLogComparator())
                .collect(Collectors.toList());
    }

    /**
     * Finds {@link CommitLog} actions needed to be performed depending on current node's status
     *
     * @param currentStatus current node's status represented as {@link NodeStatus} object
     * @return {@link List} of {@link CommitLog} nodes
     */
    public List<CommitLog> readActionsToPerform(NodeStatus currentStatus) {
        List<Integer> nodesToFindIn = new ArrayList<>(clusterProperties.getCluster()
                .get(clusterProperties.defineNodeByPort(clusterProperties.getPort())));
        nodesToFindIn.remove((Object)clusterProperties.getPort());
        return getAllActionsOnNodesAfterTime(currentStatus.getRecommendedToUpdateFrom(), nodesToFindIn)
                .stream()
                .distinct()
                .sorted(new CommitLogComparator())
                .collect(Collectors.toList());
    }

    /**
     * Sorts {@link List} of {@link CommitLog} objects by tables(or entities) affected
     *
     * @param actions {@link List} of {@link CommitLog} to perform sorting
     * @return {@link Map} where key is table(entity) alias name and value - actions performed on this table
     */
    public Map<String, List<CommitLog>> sortNodesByTable(List<CommitLog> actions) {
        Map<String, List<CommitLog>> sorted = new HashMap<>();
        sorted.put(ALIAS_CERTIFICATES, new ArrayList<>());
        sorted.put(ALIAS_ORDERS, new ArrayList<>());
        sorted.put(ALIAS_TAGS, new ArrayList<>());
        for (CommitLog action : actions) {
            switch (action.getTableTitle()) {
                case ALIAS_CERTIFICATES:
                    sorted.get(ALIAS_CERTIFICATES).add(action);
                    break;
                case ALIAS_ORDERS:
                    sorted.get(ALIAS_ORDERS).add(action);
                    break;
                case ALIAS_TAGS:
                    sorted.get(ALIAS_TAGS).add(action);
                    break;
            }
        }
        return sorted;
    }
}
