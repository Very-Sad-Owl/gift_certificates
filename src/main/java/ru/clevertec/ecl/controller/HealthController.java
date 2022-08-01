package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.util.health.HealthCheckerService;
import ru.clevertec.ecl.util.health.Status;

import java.util.Collection;

import static ru.clevertec.ecl.interceptor.common.UrlPaths.ACTION_HEALTH_CHECK;

/**
 * Controller class for performing health checking operations.
 *
 * Provides REST interface for health check requests.
 *
 * See also {@link org.springframework.web.bind.annotation.RestController}.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HealthController {

    /**
     * Service class object to perform health checking logic.
     */
    private final HealthCheckerService healthCheckerService;

    /**
     * Performs health check on cluster's nodes.
     *
     * @param port node's ports whose replicas(including given node) needed to be checked. If port is
     *             not specified, method checks all cluster.
     * @return Collection of nodes' statuses
     *
     * See also {@link Status}
     */
    @GetMapping(value = ACTION_HEALTH_CHECK)
    public Collection<Status> healthCheckCluster(@RequestParam(required = false, defaultValue = "0") int port) {
        return healthCheckerService.healthCheckEndpoint(port).values();
    }

}
