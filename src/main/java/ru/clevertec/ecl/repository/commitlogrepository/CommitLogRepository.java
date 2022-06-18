package ru.clevertec.ecl.repository.commitlogrepository;

import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.commitlogentities.NodeStatus;

@Repository
public interface CommitLogRepository extends CommonCommitLogRepository<NodeStatus> {
     NodeStatus findByNodeTitle(String title);
//    void updateNodeStatusByTitle(@Param("time")LocalDateTime time, @Param("status") boolean status, @Param("node") String node);
}
