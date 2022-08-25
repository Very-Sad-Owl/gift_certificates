package ru.clevertec.ecl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.exception.GlobalDefaultExceptionHandler;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.UserMapperImpl;
import ru.clevertec.ecl.service.*;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.user.UserServiceTestConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {ClusterPropertiesConfiguration.class, PrimaryDbConfiguration.class, CommitLogDbConfiguration.class,
        UserMapperImpl.class, CommitLogConfiguration.class, UserController.class, UserServiceTestConfiguration.class,
        CommonConfiguration.class, GlobalDefaultExceptionHandler.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "application-test.yml")
@AutoConfigureMockMvc
@EnableAutoConfiguration
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ClusterProperties properties;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        firstUser = UserDto.builder().id(1).name("oleg").surname("olegov").build();
        secondUser = UserDto.builder().id(2).name("olga").surname("mailychko").build();
    }

    @SneakyThrows
    @Test
    public void findAllEndpointTest_defaultSort_statusOkAndJsonContent() {
        List<UserDto> expectedContent = Stream.of(firstUser, secondUser).collect(Collectors.toList());
        String expectedContentJson = objectMapper.writeValueAsString(expectedContent);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/users/findAll", Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedContentJson));
    }

    @SneakyThrows
    @Test
    public void findAllEndpointTest_sortBySurnameAsc_statusOkAndJsonContent() {
        List<UserDto> expectedContent = Stream.of(secondUser, firstUser).collect(Collectors.toList());
        String expectedContentJson = objectMapper.writeValueAsString(expectedContent);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/users/findAll?sort=surname,asc",
                        Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedContentJson));
    }

    @SneakyThrows
    @Test
    public void findByIdEndpointTest_existingUser_statusOkAndFoundContent() {
        String expectedJson = objectMapper.writeValueAsString(secondUser);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/users/find?id=2", Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedJson));
    }

    @SneakyThrows
    @Test
    public void findByIdEndpointTest_nonExistingUser_errorResponse() {
        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/users/find?id=0", Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    private static UserDto firstUser;
    private static UserDto secondUser;
}
