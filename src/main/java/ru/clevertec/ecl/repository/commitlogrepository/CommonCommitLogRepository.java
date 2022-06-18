package ru.clevertec.ecl.repository.commitlogrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ru.clevertec.ecl.entity.AbstractEntity;
import ru.clevertec.ecl.entity.commitlogentities.AbstractCommitLog;

import static ru.clevertec.ecl.repository.UsedQuery.*;

@NoRepositoryBean
public interface CommonCommitLogRepository<E extends AbstractCommitLog> extends JpaRepository<E, Long> {
}
