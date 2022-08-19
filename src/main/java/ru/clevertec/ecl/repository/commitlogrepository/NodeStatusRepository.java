package ru.clevertec.ecl.repository.commitlogrepository;

import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link NodeStatus} entity.
 *
 * @author Olga Mailychko
 *
 */
@Repository
public interface NodeStatusRepository extends CommonCommitLogRepository<NodeStatus> {
     Optional<NodeStatus> findByNodeTitle(String title);
     List<NodeStatus> findAllByNodeStatus(boolean nodeStatus);
     List<NodeStatus> findAllByNodeStatusAndNodeTitleIn(boolean nodeStatus, Collection<String> nodeTitle);
     NodeStatus findFirstByNodeTitleInOrderByLastUpdatedDesc(Collection<@NotBlank String> nodeTitle);
}
