package ru.clevertec.ecl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.User;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.repository.UserRepository;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.util.matcherhelper.MatcherBuilder;


@Service
public class UserServiceImpl
        extends AbstractService<UserDto, User, UserRepository>
        implements UserService {

    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, MatcherBuilder<UserDto> filterMatcher, UserMapper mapper) {
        super(repository, filterMatcher);
        this.mapper = mapper;
    }

    @Override
    public UserDto save(UserDto e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserDto findById(long id) {
        return repository.findById(id)
                .map(mapper::userToDto)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Page<UserDto> getAll(UserDto params, Pageable pageable) {
        return repository.findAll(pageable).map(mapper::userToDto);
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserDto update(UserDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserDto findByName(String name) {
        return repository.findByName(name)
                .map(mapper::userToDto)
                .orElseThrow(NotFoundException::new);
//        Optional<User> user = repository.findByLogin(name);
//        if (user.isPresent()) {
//            return repository.findByLogin(name));
//        } else {
//            throw new NotFoundException();
//        }
    }
}
