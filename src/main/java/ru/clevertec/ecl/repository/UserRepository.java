package ru.clevertec.ecl.repository;

import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Tag;
import ru.clevertec.ecl.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CommonRepository<User> {
    Optional<User> findByLogin(String login);
}
