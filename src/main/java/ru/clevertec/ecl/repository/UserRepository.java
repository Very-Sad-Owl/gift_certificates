package ru.clevertec.ecl.repository;

import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CommonRepository<User> {
    Optional<User> findByName(String login);
}
