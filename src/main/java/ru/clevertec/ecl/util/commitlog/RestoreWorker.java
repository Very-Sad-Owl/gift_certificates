package ru.clevertec.ecl.util.commitlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.util.health.HealthCheckerService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.clevertec.ecl.service.common.DatabaseConstants.*;

/**
 * A class performing data restoration from commit log.
 *
 * See also {@link CommitLogWorker}
 *
 * @author Olga Mailychko
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RestoreWorker {
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;
    private final TagService tagService;
    private final CertificateService certificateService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final CommitLogWorker commitLogWorker;
    private final HealthCheckerService healthCheckerService;
    private final Environment environment;
    private final ClusterProperties clusterProperties;

    /**
     * Performs missing actions on {@link ru.clevertec.ecl.entity.baseentities.Tag}
     *
     * @param actionsOnTag {@link List} of {@link CommitLog} to perform
     */
    @SneakyThrows
    public void restoreTagData(List<CommitLog> actionsOnTag) {
        boolean isDifferencePointReached = false;
        long currentSequenceValue = tagService.getSequenceCurrVal();
        for (CommitLog action : actionsOnTag) {
            if (action.getAction().equals(Action.SAVE) || action.getAction().equals(Action.UPDATE)) {
                TagDto value = objectMapper.readValue(action.getJsonValue(), TagDto.class);
                if (value.getId() != currentSequenceValue + 1 && currentSequenceValue != 1) {
                    tagService.updateSequence(value.getId() - 1);
                }
                if (isDifferencePointReached) {
                    tagService.save(value);
                } else {
                    try {
                        tagService.findById(value.getId()).equals(value);
                    } catch (NotFoundException e) {
                        TagDto saved = tagService.save(value);
                        isDifferencePointReached = true;
                        log.info("uncommited nodes start reached");
                    }
                }
            } else if (action.getAction().equals(Action.DELETE)) {
                long id = objectMapper.readValue(action.getJsonValue(), Long.class);
                try {
                    tagService.findById(id);
                } catch (NotFoundException e) {
                    tagService.delete(id);
                    isDifferencePointReached = true;
                    log.info("uncommited nodes start reached");
                }
            }
        }
    }

    /**
     * Performs missing actions on {@link ru.clevertec.ecl.entity.baseentities.Certificate}
     *
     * @param actionsOnCertificate {@link List} of {@link CommitLog} to perform
     */
    @SneakyThrows
    public void restoreCertificateData(List<CommitLog> actionsOnCertificate) {
        boolean isDifferencePointReached = false;
        long currentSequenceValue = certificateService.getSequenceCurrVal();
        for (CommitLog action : actionsOnCertificate) {
            if (action.getAction().equals(Action.SAVE) || action.getAction().equals(Action.UPDATE)) {
                CertificateDto value = objectMapper.readValue(action.getJsonValue(), CertificateDto.class);
                if (value.getId() != currentSequenceValue + 1 && currentSequenceValue != 1) {
                    certificateService.updateSequence(value.getId() - 1);
                }
                if (isDifferencePointReached) {
                    certificateService.save(value);
                } else {
                    try {
                        certificateService.findById(value.getId()).equals(value);
                    } catch (NotFoundException e) {
                        certificateService.save(value);
                        isDifferencePointReached = true;
                        log.info("uncommited nodes start reached");
                    }
                }
            } else if (action.getAction().equals(Action.DELETE)) {
                long id = objectMapper.readValue(action.getJsonValue(), Long.class);
                try {
                    certificateService.findById(id);
                } catch (NotFoundException e) {
                    certificateService.delete(id);
                    isDifferencePointReached = true;
                    log.info("uncommited nodes start reached");
                }
            }
        }
    }

    /**
     * Performs missing actions on {@link ru.clevertec.ecl.entity.baseentities.Order}
     *
     * @param actionsOnOrder {@link List} of {@link CommitLog} to perform
     */
    @SneakyThrows
    public void restoreOrderData(List<CommitLog> actionsOnOrder) {
        boolean isDifferencePointReached = false;
        long currentSequenceValue = orderService.getSequenceCurrVal();
        for (CommitLog action : actionsOnOrder) {
            if (action.getAction().equals(Action.SAVE) || action.getAction().equals(Action.UPDATE)) {
                OrderDto value = objectMapper.readValue(action.getJsonValue(), OrderDto.class);
                if (value.getId() != currentSequenceValue + 1 && currentSequenceValue != 1) {
                    orderService.updateSequence(value.getId() + 1);
                }
                if (clusterProperties.definePortById(value.getId()) != clusterProperties.getPort()) return;
                if (isDifferencePointReached) {
                    orderService.save(value);
                } else {
                    try {
                        orderService.findById(value.getId()).equals(value);
                    } catch (NotFoundException e) {
                        orderService.save(value);
                        isDifferencePointReached = true;
                        log.info("uncommited nodes start reached");
                    }
                }
            } else if (action.getAction().equals(Action.DELETE)) {
                long id = objectMapper.readValue(action.getJsonValue(), Long.class);
                try {
                    orderService.findById(id);
                } catch (NotFoundException e) {
                    orderService.delete(id);
                    isDifferencePointReached = true;
                    log.info("uncommited nodes start reached");
                }
            }
        }
    }

    /**
     * Method restoring missed data on application startup
     *
     * @param event occured {@link ApplicationReadyEvent}
     */
    @EventListener
    @Transactional
    public void appReady(ApplicationReadyEvent event) {
        if(Arrays.binarySearch(environment.getActiveProfiles(), "test") == -1 && !ddlAuto.equals("create")) {

            NodeStatus currentStatus = healthCheckerService.getCurrentNodeStatus(clusterProperties.getPort());
            List<CommitLog> actionsToPerform = commitLogWorker.readActionsToPerform(currentStatus);

            Map<String, List<CommitLog>> sortedActions = commitLogWorker.sortNodesByTable(actionsToPerform);

            restoreTagData(sortedActions.get(ALIAS_TAGS));
            restoreCertificateData(sortedActions.get(ALIAS_CERTIFICATES));
            restoreOrderData(sortedActions.get(ALIAS_ORDERS));

            currentStatus.setNodeStatus(true);
            currentStatus.setLastUpdated(LocalDateTime.now());
            currentStatus.setRecommendedToUpdateFrom(currentStatus.getLastUpdated());
            healthCheckerService.updateNodeStatus(currentStatus);
        }
    }
}
