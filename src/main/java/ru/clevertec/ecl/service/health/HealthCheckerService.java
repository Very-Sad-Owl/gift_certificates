package ru.clevertec.ecl.service.health;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.exception.ServerIsDownException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.repository.commitlogrepository.NodeStatusRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A class performing health checking logic.
 *
 * @author Olga Mailychko
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class HealthCheckerService {
    private final ClusterProperties clusterProperties;
    private static final String APP_URL_PATTERN = "http://localhost:%d/actuator/health";
    private static final String NODE_TITLE_PREFIX = "node";
    private final RestTemplate restTemplate;
    private final NodeStatusRepository logRepository;
    private final ObjectMapper objectMapper;

    /**
     * Scheduled method performing health check every day at 12pm
     */
    @Scheduled(cron = "0 12 * * * ?")
    public void healthCheck() {
        List<Map<Integer, Status>> collected = clusterProperties.getCluster().keySet().stream()
                .map(this::healthCheckEndpoint)
                .collect(Collectors.toList());
        collected.stream()
                .forEach(val -> {
                    if (isAnyAlive(val.values())) {
                        log.info(NODE_TITLE_PREFIX + clusterProperties
                                .defineNodeByPort(val.keySet().stream().findFirst().get()) + "'s is ok");
                        val.values()
                                .forEach(el ->
                                        {
                                            if (el.isOk()) {
                                                log.info(NODE_TITLE_PREFIX + el.getPort() + " is ok");
                                                updateNodeStatus(el.getPort(), true);
                                            } else {
                                                log.warn(NODE_TITLE_PREFIX + el.getPort() + " is down");
                                                updateNodeStatus(el.getPort(), false);
                                            }
                                        }
                                );
                    } else {
                        int mainNode = clusterProperties
                                .defineNodeByPort(val.keySet().stream().findFirst().get());
                        log.error(NODE_TITLE_PREFIX + mainNode + "'s are down");
                        clusterProperties.getCluster().get(mainNode)
                                .forEach(el -> updateNodeStatus(el, false));
                    }
                });
    }

    /**
     * Method finding if there is any positive {@link Status} object in given collection
     *
     * @param statuses collection of {@link Status} object to check
     */
    public boolean isAnyAlive(Collection<Status> statuses) {
        return statuses.stream().anyMatch(Status::isOk);
    }

    /**
     * Method performing health check
     *
     * @param nodePort port of node to perform health check on(including replicas). If value = 0, health check performs
     *                 on all nodes in cluster.
     * @return {@link Map} where key is node's port and value is status of this node
     */
    public Map<Integer, Status> healthCheckEndpoint(Integer nodePort) {
        List<Integer> toCheck;
        if (nodePort == 0) {
            toCheck = clusterProperties.getCluster().values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            toCheck.remove((Object) clusterProperties.getPort());
        } else {
            toCheck = clusterProperties.getCluster().values().stream()
                    .filter(value -> value.contains(nodePort))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        Map<Integer, Status> statuses = toCheck.stream()
                .map(node -> CompletableFuture.supplyAsync(() ->
                        {
                            try {
                                JsonNode object = restTemplate
                                        .getForObject(String.format(APP_URL_PATTERN, node),
                                                JsonNode.class);
                                ((ObjectNode) object).put(NODE_TITLE_PREFIX, node);
                                return new Status(object);
                            } catch (Exception e) {
                                Status status = new Status();
                                status.setOk(false);
                                status.setPort(node);
                                return status;
                            }
                        }
                        )
                )
                .map(CompletableFuture::join)
                .map(Status.class::cast)
                .collect(Collectors.toMap(Status::getPort, p -> p, (p1, p2) -> p1));
        statuses.put(clusterProperties.getPort(), new Status(clusterProperties.getPort(), true));

        return statuses;
    }

    /**
     * Method finds currently available nodes excluding those which unavailable after last health check
     *
     * @return {@link List} of available node's ports
     */
    public Map<Integer, List<Integer>> checkAlive() {
        List<NodeStatus> allDown = logRepository.findAllByNodeStatus(false);
        return getCurrentAvailable(allDown, 0);
    }

    /**
     * Method finds currently available nodes excluding those which unavailable after last health check
     *
     * @param port node's port related to some main node. Only replicas of this main node(including main node itself)
     *             will be checked.
     * @return {@link List} of available node's ports
     */
    public Map<Integer, List<Integer>> checkAlive(int port) {
        List<String> nodes = clusterProperties.getCluster()
                .get(clusterProperties.defineNodeByPort(port))
                .stream().map(n -> "node" + n)
                .collect(Collectors.toList());
        List<NodeStatus> allDown = logRepository.findAllByNodeStatusAndNodeTitleIn(false, nodes);
        return getCurrentAvailable(allDown, port);
    }

    /**
     * Method finds all currently available nodes within replicas of given node's port
     *
     * @param alreadyUnavailable list of already unavailable nodes
     * @param forPorts           some node's port belonging to cluster. If its value != 0, method checks only nodes within
     *                           main node's replicas where node with given port belongs, otherwise checks whole cluster.
     * @return {@link List} of available node's ports
     */
    @SneakyThrows
    public Map<Integer, List<Integer>> getCurrentAvailable(List<NodeStatus> alreadyUnavailable, int forPorts) {
        List<Integer> unavailable = alreadyUnavailable.stream().map(NodeStatus::getNodeTitle)
                .map(v -> v.replace("node", ""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        String jsonCluster = objectMapper.writeValueAsString(clusterProperties.getCluster());
        Map<Integer, List<Integer>> allNode = objectMapper.readValue(jsonCluster, new TypeReference<Map<Integer, List<Integer>>>() {
        });
        if (forPorts != 0) {
            allNode = allNode.entrySet()
                    .stream()
                    .filter(k -> k.getKey() == clusterProperties.defineNodeByPort(forPorts))
                    .map(k -> {
                        k.getValue().removeAll(unavailable);
                        return k;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            allNode = allNode.entrySet()
                    .stream()
                    .map(k -> {
                        k.getValue().removeAll(unavailable);
                        return k;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        for (List<Integer> el : allNode.values()) {
            if (el.isEmpty()) {
                throw new ServerIsDownException();
            }
        }

        final List<Integer> available = new ArrayList<>();
        allNode.values().stream()
                .flatMap(List::stream)
                .map(n -> CompletableFuture.supplyAsync(() -> {
                            if (n != clusterProperties.getPort()) {
                                restTemplate
                                        .getForObject(String.format(APP_URL_PATTERN, n),
                                                Object.class);
                            }
                            return n;
                        })
                                .thenApplyAsync(v -> {
                                    available.add(v);
                                    return v;
                                })
                                .exceptionally(e -> {
                                    available.add(0);
                                    return 0;
                                })

                )
                .map(CompletableFuture::join)
                .collect(Collectors.toList());


        Map<Integer, List<Integer>> availableNow = allNode.entrySet().stream()
                .map(v -> {
                    v.getValue().retainAll(available);
                    if (v.getValue().isEmpty()) {
                        throw new ServerIsDownException();
                    }
                    return v;
                })
                .map(v -> {
                    int newKey;
                    if (forPorts != 0) {
                        newKey = forPorts;
                    } else {
                        newKey = v.getValue().get(0);
                    }
                    v.getValue().remove((Object) newKey);
                    return new AbstractMap.SimpleEntry<>(newKey, v.getValue());
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        return availableNow;
    }

    /**
     * Method finds any available node from nodes belonging to certain main one.
     *
     * @param port port whose neighbors must be checked
     * @return any available node's port
     */
    public int findAnyAliveNodeFromReplicas(int port) {
        Map<Integer, List<Integer>> available = checkAlive(port);
        return available.get(port).isEmpty() ? port : available.get(port).get(0);
    }

    public List<Integer> findAliveNodesFromSubclusterInList(int port, List<Integer> available) {
        List<Integer> availableCopy = new ArrayList<>(available);
        List<Integer> toSearchFor = clusterProperties.getCluster().get(clusterProperties.defineNodeByPort(port));
        availableCopy.retainAll(toSearchFor);
        if (availableCopy.isEmpty()) throw new ServerIsDownException();
        return availableCopy;

    }

    /**
     * @param port port of node whose status needs to be found
     * @return current node's status represented as {@link NodeStatus} object
     * @throws NotFoundException if there is no anu node with such port in title
     */
    public NodeStatus getCurrentNodeStatus(int port) {
        return logRepository.findByNodeTitle(NODE_TITLE_PREFIX + port).orElseThrow(NotFoundException::new);
    }

    /**
     * Method updates some {@link NodeStatus} entity's value
     *
     * @param nodeStatus {@link NodeStatus} object containing needed data
     * @return updated node's status represented as {@link NodeStatus} object
     */
    public NodeStatus updateNodeStatus(NodeStatus nodeStatus) {
        logRepository.findByNodeTitle(nodeStatus.getNodeTitle()).orElseThrow(NotFoundException::new);
        return logRepository.save(nodeStatus);
    }

    /**
     * Method updates some {@link NodeStatus} entity's value
     *
     * @param titles collection on node titles to find in
     * @return returns last updated node in given range of titles
     */
    public NodeStatus findLastUpdatedByNode(List<String> titles) {
        return logRepository.findFirstByNodeTitleInOrderByLastUpdatedDesc(titles);
    }

    /**
     * Method updates some {@link NodeStatus} entity's value
     *
     * @param port      port of node to update
     * @param newStatus boolean value indicating whether node is up(true) or down(false)
     * @return updated node's status represented as {@link NodeStatus} object
     */
    @Transactional
    public NodeStatus updateNodeStatus(int port, boolean newStatus) {
        NodeStatus currentStatus = getCurrentNodeStatus(port);
        if (currentStatus.isNodeStatus() && !newStatus) {
            currentStatus.setNodeStatus(newStatus);
            currentStatus.setRecommendedToUpdateFrom(currentStatus.getLastUpdated());
            currentStatus.setLastUpdated(LocalDateTime.now());
        }
        if (!currentStatus.isNodeStatus() && !newStatus) {
            currentStatus.setLastUpdated(LocalDateTime.now());
        }
        if (currentStatus.isNodeStatus() && newStatus) {
            currentStatus.setLastUpdated(LocalDateTime.now());
            currentStatus.setRecommendedToUpdateFrom(currentStatus.getLastUpdated());
        }
        return logRepository.save(currentStatus);
    }

}
