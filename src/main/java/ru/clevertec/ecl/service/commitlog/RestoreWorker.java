package ru.clevertec.ecl.service.commitlog;

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
import ru.clevertec.ecl.entity.commitlogentities.CommitLogComparator;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.health.HealthCheckerService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.service.common.DatabaseConstants.*;

/**
 * A class performing data restoration from commit log.
 * <p>
 * See also {@link CommitLogService}
 *
 * @author Olga Mailychko
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
    private final CommitLogService commitLogService;
    private final HealthCheckerService healthCheckerService;
    private final Environment environment;
    private final ClusterProperties clusterProperties;

    public Map<String, List<CommitLog>> readActionsToPerform() {
        List<Integer> nodesToFindIn = new ArrayList<>(clusterProperties.getCluster()
                .get(clusterProperties.defineNodeByPort(clusterProperties.getPort())));
        List<String> nodeTitlesToFindIn = nodesToFindIn
                .stream()
                .map(node -> "node" + node)
                .collect(Collectors.toList());

        NodeStatus lastUpdatedByNode = healthCheckerService.findLastUpdatedByNode(nodeTitlesToFindIn);
        int portToFind = Integer.parseInt(lastUpdatedByNode
                .getNodeTitle().replace("node", ""));
        nodesToFindIn = new ArrayList<>();
        nodesToFindIn.add(portToFind);

        Map<String, List<CommitLog>> actions = new HashMap<>();

        long tagSequenceValue = tagService.getSequenceCurrVal();
        long certificateSequenceValue = certificateService.getSequenceCurrVal();
        long orderSequenceValue = orderService.getSequenceCurrVal();

        List<CommitLog> actionsOnTag = commitLogService
                .getActionsAfterIdForTable(
                        tagSequenceValue == 1 ? 0 : tagSequenceValue,
                        ALIAS_TAGS,
                        nodesToFindIn)
                .stream()
                .distinct()
                .sorted(new CommitLogComparator())
                .collect(Collectors.toList());
        List<CommitLog> actionsOnCertificate = commitLogService
                .getActionsAfterIdForTable(
                        certificateSequenceValue == 1 ? 0 : certificateSequenceValue,
                        ALIAS_CERTIFICATES, nodesToFindIn)
                .stream()
                .distinct()
                .sorted(new CommitLogComparator())
                .collect(Collectors.toList());
        List<CommitLog> actionsOnOrder = commitLogService
                .getActionsAfterIdForTable(
                        orderSequenceValue == 1 ? 0 : orderSequenceValue,
                        ALIAS_ORDERS, nodesToFindIn)
                .stream()
                .distinct()
                .sorted(new CommitLogComparator())
                .collect(Collectors.toList());
        actionsOnOrder = commitLogService.mergeUpdateAndSaveActions(actionsOnOrder).stream()
                .distinct()
                .collect(Collectors.toList());
        actionsOnCertificate = commitLogService.mergeUpdateAndSaveActions(actionsOnCertificate).stream()
                .distinct()
                .collect(Collectors.toList());
        actionsOnTag = commitLogService.mergeUpdateAndSaveActions(actionsOnTag).stream()
                .distinct()
                .collect(Collectors.toList());

        actions.put(ALIAS_TAGS, actionsOnTag);
        actions.put(ALIAS_CERTIFICATES, actionsOnCertificate);
        actions.put(ALIAS_ORDERS, actionsOnOrder);

        return actions;
    }

    /**
     * Performs missing actions on {@link ru.clevertec.ecl.entity.baseentities.Tag}
     *
     * @param actionsOnTag {@link List} of {@link CommitLog} to perform
     */
    @SneakyThrows
    public void restoreTagData(List<CommitLog> actionsOnTag) {
        long currentSequenceValue = tagService.getSequenceCurrVal();
        for (CommitLog action : actionsOnTag) {
            TagDto value = objectMapper.readValue(action.getJsonValue(), TagDto.class);
            if (action.getAction().equals(Action.SAVE) || action.getAction().equals(Action.UPDATE)) {
                if (value.getId() != currentSequenceValue + 1 && value.getId() > 1) {
                    tagService.updateSequence(value.getId() - 1);
                    currentSequenceValue = value.getId() - 1;
                }
                TagDto saved = tagService.save(value);
                log.info("tag node(" + saved + ") has been restored from commit log");
            } else if (action.getAction().equals(Action.DELETE)) {
                tagService.delete(action.getEntityId());
                log.info("tag node(" + value + ") has been removed");
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
        long currentSequenceValue = certificateService.getSequenceCurrVal();
        for (CommitLog action : actionsOnCertificate) {
            CertificateDto value = objectMapper.readValue(action.getJsonValue(), CertificateDto.class);
            if (action.getAction().equals(Action.SAVE) || action.getAction().equals(Action.UPDATE)) {
                if (value.getId() != currentSequenceValue + 1 && value.getId() > 1) {
                    certificateService.updateSequence(value.getId() - 1);
                    currentSequenceValue = value.getId() - 1;
                }
                CertificateDto saved = certificateService.save(value);
                log.info("certificate node(" + saved + ") has been restored from commit log");
            } else if (action.getAction().equals(Action.DELETE)) {
                certificateService.delete(action.getEntityId());
                log.info("certificate node(" + value + ") has been removed");
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
        long currentSequenceValue = orderService.getSequenceCurrVal();
        for (CommitLog action : actionsOnOrder) {
            OrderDto value = objectMapper.readValue(action.getJsonValue(), OrderDto.class);
            if (clusterProperties.definePortById(value.getId()) != clusterProperties.defineNodeByPort(clusterProperties.getPort())) {
                continue;
            }
            if (action.getAction().equals(Action.SAVE) || action.getAction().equals(Action.UPDATE)) {
                if (value.getId() != currentSequenceValue + 1 && value.getId() > 1) {
                    orderService.updateSequence(value.getId() - 1);
                    currentSequenceValue = value.getId() - 1;
                } else if (value.getId() == 2) {
                    orderService.updateSequence(1);
                    currentSequenceValue = 1;
                }
                OrderDto saved = orderService.save(value);
                log.info("certificate node(" + saved + ") has been restored from commit log");
            } else if (action.getAction().equals(Action.DELETE)) {
                orderService.delete(action.getEntityId());
                log.info("order node(" + value + ") has been removed");
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
        if (Arrays.binarySearch(environment.getActiveProfiles(), "test") == -1 && !ddlAuto.equals("create")) {

            NodeStatus currentStatus = healthCheckerService.getCurrentNodeStatus(clusterProperties.getPort());
            Map<String, List<CommitLog>> actionsToPerform = readActionsToPerform();

            restoreTagData(actionsToPerform.get(ALIAS_TAGS));
            restoreCertificateData(actionsToPerform.get(ALIAS_CERTIFICATES));
            restoreOrderData(actionsToPerform.get(ALIAS_ORDERS));

            currentStatus.setNodeStatus(true);
            currentStatus.setLastUpdated(LocalDateTime.now());
            currentStatus.setRecommendedToUpdateFrom(currentStatus.getLastUpdated());
            healthCheckerService.updateNodeStatus(currentStatus);
        }
    }
}
