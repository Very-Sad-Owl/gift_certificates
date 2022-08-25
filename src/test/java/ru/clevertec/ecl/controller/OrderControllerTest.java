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
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.exception.GlobalDefaultExceptionHandler;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.*;
import ru.clevertec.ecl.service.*;
import ru.clevertec.ecl.service.certificate.CertificateServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.order.OrderServiceTestConfiguration;
import ru.clevertec.ecl.service.tag.TagServiceTestConfiguration;
import ru.clevertec.ecl.service.user.UserServiceTestConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {ClusterPropertiesConfiguration.class, PrimaryDbConfiguration.class, CommitLogDbConfiguration.class,
        TagMapperImpl.class, CertificateMapperImpl.class, CommitLogConfiguration.class, CertificateController.class,
        CertificateServiceTestConfiguration.class, TagServiceTestConfiguration.class, OrderMapperImpl.class,
        UserMapperImpl.class, UserServiceTestConfiguration.class, CommonConfiguration.class,
        OrderServiceTestConfiguration.class, OrderController.class, GlobalDefaultExceptionHandler.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "application-test.yml")
@AutoConfigureMockMvc
@EnableAutoConfiguration
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql", "/users.sql", "/orders.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class OrderControllerTest {

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
        LocalDateTime timeCreatedAndUpdated =
                LocalDateTime.parse("2011-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        exTagOne = TagDto.builder()
                .id(1)
                .name("birthday")
                .build();
        exTagTwo = TagDto.builder()
                .id(2)
                .name("christmas")
                .build();
        exTagThree = TagDto.builder()
                .id(3)
                .name("holiday")
                .build();
        exampleCertificateOne = CertificateDto.builder()
                .id(1)
                .name("hpb")
                .description("happy birthday")
                .price(2.1)
                .duration(60)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagOne)))
                .build();
        exampleCertificateTwo = CertificateDto.builder()
                .id(2)
                .name("mc")
                .description("merry christmas")
                .price(5.6)
                .duration(10)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Arrays.asList(exTagTwo, exTagThree)))
                .build();
        exampleCertificateThree = CertificateDto.builder()
                .id(3)
                .name("may 1")
                .description("may the first")
                .price(10.5)
                .duration(1)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagThree)))
                .build();
        exampleUser = UserDto.builder().id(1).name("oleg").surname("olegov").build();
        firstNode = OrderDto.builder()
                .id(1)
                .certificate(exampleCertificateOne)
                .price(2.1)
                .purchaseTime(LocalDateTime.parse("2011-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(1)
                .userId(1)
                .build();
        updatedFirstNode = OrderDto.builder()
                .id(1)
                .certificate(exampleCertificateThree)
                .price(10.5)
                .purchaseTime(LocalDateTime.parse("2011-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(3)
                .userId(1)
                .build();
        secondNode = OrderDto.builder()
                .id(2)
                .certificate(exampleCertificateTwo)
                .price(5.6)
                .purchaseTime(LocalDateTime.parse("2012-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(2)
                .userId(1)
                .build();
        newNode = OrderDto.builder()
                .certificate(exampleCertificateThree)
                .price(10.5)
                .purchaseTime(LocalDateTime.parse("2013-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(3)
                .userId(1)
                .build();
        newNodeSaved = OrderDto.builder()
                .id(3)
                .certificate(exampleCertificateThree)
                .price(10.5)
                .purchaseTime(LocalDateTime.parse("2013-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(3)
                .userId(1)
                .build();
        withNonExistingId = OrderDto.builder()
                .id(0)
                .certificate(exampleCertificateThree)
                .price(10.5)
                .purchaseTime(LocalDateTime.parse("2013-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(3)
                .userId(1)
                .build();
    }

    @SneakyThrows
    @Test
    public void findOrderEndpointTest_existingOrder_statusOkAndFoundContent() {
        String expectedJson = objectMapper.writeValueAsString(firstNode);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/orders/find?id=1", Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedJson));
    }

    @SneakyThrows
    @Test
    public void findOrderEndpointTest_nonExistingOrder_errorResponse() {
        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/orders/find?id=0", Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql", "/users.sql", "/orders.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/set_sequence.sql"})
    public void saveOrderEndpoint() {
        final String baseUrl = "http://localhost:" + port + "/orders/buy";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderDto> request = new HttpEntity<>(newNode, headers);

        ResponseEntity<OrderDto> actual = this.restTemplate.postForEntity(uri, request, OrderDto.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.CREATED)
                && compareOrdersExcludingPurchaseTime(newNodeSaved, actual.getBody()));
    }

    @SneakyThrows
    @Test
    public void updateOrderEndpoint_existingId_updatedEntity() {
        final String baseUrl = "http://localhost:" + port + "/orders/update";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderDto> request = new HttpEntity<>(updatedFirstNode, headers);

        ResponseEntity<OrderDto> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, OrderDto.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && compareOrdersExcludingPurchaseTime(updatedFirstNode, actual.getBody()));
    }

    @SneakyThrows
    @Test
    public void updateOrderEndpoint_nonExistingId_errorResponse() {
        final String baseUrl = "http://localhost:" + port + "/orders/update";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderDto> request = new HttpEntity<>(withNonExistingId, headers);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void deleteOrderEndpoint_existingId_statusOk() {
        String baseUrl = "http://localhost:" + port + "/orders/delete?id=1";
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void deleteOrderEndpoint_npnExistingId_errorResponse() {
        String baseUrl = "http://localhost:" + port + "/orders/delete?id=0";
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    private static boolean compareOrdersExcludingPurchaseTime(OrderDto firstOrder, OrderDto secondOrder) {
        return firstOrder.getId() == secondOrder.getId()
                && firstOrder.getCertificate().equals(secondOrder.getCertificate())
                && firstOrder.getPrice() == secondOrder.getPrice()
                && firstOrder.getCertificateId() == secondOrder.getCertificateId()
                && firstOrder.getUser().equals(secondOrder.getUser())
                && firstOrder.getUserId() == secondOrder.getUserId();
    }

    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static CertificateDto exampleCertificateOne;
    private static CertificateDto exampleCertificateTwo;
    private static CertificateDto exampleCertificateThree;
    private static OrderDto firstNode;
    private static OrderDto updatedFirstNode;
    private static OrderDto secondNode;
    private static OrderDto newNode;
    private static OrderDto newNodeSaved;
    private static OrderDto withNonExistingId;
    private static UserDto exampleUser;
}
