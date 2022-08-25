package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.baseentities.User;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;

/**
 * Repository for {@link User} entity.
 *
 * @author Olga Mailychko
 *
 */
@Repository
public interface UserRepository extends CommonRepository<User> {
}
