package ru.clevertec.ecl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.baseentities.Order;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.baseentities.User;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.repository.entityrepository.UserRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.UserService;

/**
 * Service class providing CRUD operations on {@link User}.
 *
 * See also {@link AbstractService}, {@link UserService}.
 *
 * @author Olga Mailychko
 *
 */
@Service
public class UserServiceImpl
        extends AbstractService<UserDto, User, UserRepository>
        implements UserService {

    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(ClusterProperties properties, UserRepository repository, UserMapper mapper) {
        super(properties, repository);
        this.mapper = mapper;
    }

    /**
     * Saving logic is not available for {@link User} entities.
     *
     * @throws UnsupportedOperationException is always thrown by this method
     */
    @Override
    public UserDto save(UserDto toSave) {
        throw new UnsupportedOperationException();
    }

    /**
     * Finds {@link User} object by id.
     *
     * @param id id of needed {@link User}
     * @return found {@link User} entity as {@link UserDto} object
     */
    @Override
    public UserDto findById(long id) {
        return repository.findById(id)
                .map(mapper::userToDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    /**
     * Finds all {@link User} entities from storage.
     *
     * @param filter {@link UserDto} object containing filtering fields
     * @param pageable {@link Pageable} object storing pagination data
     * @return paged collection of all found {@link User} entities represented as {@link UserDto} objects
     */
    @Override
    public Page<UserDto> getAll(UserDto filter, Pageable pageable) {
        return repository.findAll(pageable).map(mapper::userToDto);
    }

    /**
     * Removing logic is not available for {@link User} entities.
     *
     * @throws UnsupportedOperationException is always thrown by this method
     */
    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException();
    }

    /**
     * Editing logic is not available for {@link User} entities.
     *
     * @throws UnsupportedOperationException is always thrown by this method
     */
    @Override
    public UserDto update(UserDto dto) {
        throw new UnsupportedOperationException();
    }

}
