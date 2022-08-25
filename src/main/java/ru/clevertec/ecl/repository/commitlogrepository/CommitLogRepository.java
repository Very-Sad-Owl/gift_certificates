package ru.clevertec.ecl.repository.commitlogrepository;

import org.springframework.stereotype.Repository;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository for {@link CommitLog} entity.
 *
 * @author Olga Mailychko
 *
 */
@Repository
public interface CommitLogRepository extends CommonCommitLogRepository<CommitLog> {
     List<CommitLog> findAllByActionTimeAfterAndPerformedOnNodeIn(LocalDateTime actionTime, Collection<Integer> performedOnNode);

     List<CommitLog> findAllByActionTimeAfterAndPerformedOnNodeInAndTableTitle
             (LocalDateTime actionTime, Collection<Integer> performedOnNode, String tableTitle);
     List<CommitLog> findAllByEntityIdAfterAndTableTitleAndPerformedOnNodeIn(@Positive long entityId, String tableTitle,
                                                                             Collection<Integer> performedOnNode);
}
