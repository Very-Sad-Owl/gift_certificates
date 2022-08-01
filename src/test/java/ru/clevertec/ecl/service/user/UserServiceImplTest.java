package ru.clevertec.ecl.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.mapper.UserMapperImpl;
import ru.clevertec.ecl.service.*;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {UserServiceTestConfiguration.class, UserMapperImpl.class,
        PrimaryDbConfiguration.class,
        CommitLogConfiguration.class,
        CommitLogDbConfiguration.class,
        ClusterPropertiesConfiguration.class,
        CommonConfiguration.class})
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceImplTest {

    @Autowired
    UserService service;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ClusterProperties properties;

    @Test
    public void findByIdTest_existingId_userWithDefinedId() {
        UserDto expected = UserDto.builder().id(1).name("oleg").surname("olegov").build();

        UserDto actual = service.findById(1);

        assertEquals(expected, actual);
    }

    @Test
    public void findByIdTest_nonExistingId_notFoundException() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.findById(0)
        );

        assertSame(thrown.getClass(), NotFoundException.class);
    }

    @Test
    public void getAllTest_unpaged_allUsers() {
        Page<UserDto> actual = service.getAll(UserDto.builder().build(), Pageable.unpaged());

        assertEquals(actual.getSize(), 2);
    }

    @Test
    public void getAllTest_firstPageOneNode_oneUser() {
        Page<UserDto> actual = service.getAll(UserDto.builder().build(), PageRequest.of(0, 1));

        assertEquals(actual.getSize(), 1);
    }

    @Test
    public void saveTest_unsupportedOperation() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> service.save(UserDto.builder().name("xd").surname("xdd").build())
        );
        assertNotNull(thrown);
    }

    @Test
    public void updateTest_unsupportedOperation() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> service.update(UserDto.builder().id(1).name("xd").surname("xdd").build())
        );
        assertNotNull(thrown);
    }

    @Test
    public void deleteTest_unsupportedOperation() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> service.delete(1));
        assertNotNull(thrown);
    }
}