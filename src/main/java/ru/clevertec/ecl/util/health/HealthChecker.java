package ru.clevertec.ecl.util.health;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.ecl.config.ClusterProperties;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.repository.commitlogrepository.CommitLogRepository;
//import ru.clevertec.ecl.repository.commitlogrepository.CommitLogRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class HealthChecker {

    private final ClusterProperties clusterProperties;
    private int currentPort;
    private static final String APP_URL_PATTERN = "http://localhost:%d/actuator/health";
    private static final String NODE_TITLE_PREFIX = "node";
    private final RestTemplate restTemplate;
    private final CommitLogRepository logRepository;


//    @Scheduled(fixedDelay = 9000)
//    public void healthCheck() {
//        List<Map<Integer, Status>> collected = clusterProperties.getCluster().keySet().stream()
//                .map(this::healthCheckEndpoint)
//                .collect(Collectors.toList());
//        collected.stream()
//                .forEach(val -> {
//                    if (isAnyAlive(val.values())) {
//                        log.info(NODE_TITLE_PREFIX + clusterProperties
//                                .defineNodeByPort(val.keySet().stream().findFirst().get()) + "'s is ok");
//                        val.values()
//                                .forEach(el ->
//                                        {
//                                            if (el.isOk()) {
//                                                log.info(NODE_TITLE_PREFIX + el.getPort() + " is ok");
//                                                updateNodeStatus(el.getPort(), true);
//                                            } else {
//                                                log.warn(NODE_TITLE_PREFIX + el.getPort() + " is down");
//                                                updateNodeStatus(el.getPort(), false);
//                                            }
//                                        }
//                                );
//                    } else {
//                        int mainNode = clusterProperties
//                                .defineNodeByPort(val.keySet().stream().findFirst().get());
//                        log.error(NODE_TITLE_PREFIX + mainNode + "'s are down");
//                        clusterProperties.getCluster().get(mainNode)
//                                .forEach(el -> updateNodeStatus(el, false));
//                    }
//                });
//    }

    private boolean isAnyAlive(Collection<Status> statuses) {
        return statuses.stream().anyMatch(Status::isOk);
    }

    public Map<Integer, Status> healthCheckEndpoint(Integer nodePort) {
        List<Integer> toCheck;
        if (nodePort == 0) {
            toCheck = clusterProperties.getCluster().values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
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
                                                .getForObject(String.format(APP_URL_PATTERN, node), JsonNode.class);
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

        return statuses;
    }

    private NodeStatus getCurrentNodeStatus(int node) {
//        logRepository.createConnection();
        return logRepository.findByNodeTitle(NODE_TITLE_PREFIX + node);
    }

    @Transactional
    public void updateNodeStatus(int node, NodeStatus newStatus) {
//        logRepository.createConnection();
        boolean currentStatus = getCurrentNodeStatus(node).isNodeStatus();
        if (!currentStatus) {
            logRepository.save(newStatus);
        }
        if (!currentStatus && newStatus.isNodeStatus()) {
            logRepository.save(newStatus);
        }
    }

}
