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
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.GlobalDefaultExceptionHandler;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapperImpl;
import ru.clevertec.ecl.mapper.TagMapperImpl;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.service.PrimaryDbConfiguration;
import ru.clevertec.ecl.service.certificate.CertificateServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.tag.TagServiceTestConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {ClusterPropertiesConfiguration.class, PrimaryDbConfiguration.class, CommitLogDbConfiguration.class,
        TagMapperImpl.class, CertificateMapperImpl.class, CommitLogConfiguration.class, CertificateController.class,
        CertificateServiceTestConfiguration.class, TagServiceTestConfiguration.class, GlobalDefaultExceptionHandler.class,
        CommonConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "application-test.yml")
@AutoConfigureMockMvc
@EnableAutoConfiguration
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CertificateControllerTest {

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
        newTag = TagDto.builder()
                .name("religion")
                .build();
        newTagSaved = TagDto.builder()
                .id(4)
                .name("religion")
                .build();
        exampleOne = CertificateDto.builder()
                .id(1)
                .name("hpb")
                .description("happy birthday")
                .price(2.1)
                .duration(60)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagOne)))
                .build();
        exampleOneUpdated = CertificateDto.builder()
                .id(1)
                .name("hpb!!!")
                .description("happy birthday")
                .price(2.1)
                .duration(60)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagOne)))
                .build();
        exampleTwo = CertificateDto.builder()
                .id(2)
                .name("mc")
                .description("merry christmas")
                .price(5.6)
                .duration(10)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Arrays.asList(exTagTwo, exTagThree)))
                .build();
        exampleThree = CertificateDto.builder()
                .id(3)
                .name("may 1")
                .description("may the first")
                .price(10.5)
                .duration(1)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagThree)))
                .build();
        newCertificate = CertificateDto.builder()
                .name("happy easter")
                .description("XDXDXD")
                .price(777)
                .duration(2)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Arrays.asList(exTagThree, newTag)))
                .build();
        newCertificateSaved = CertificateDto.builder()
                .id(4)
                .name("happy easter")
                .description("XDXDXD")
                .price(777)
                .duration(2)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Arrays.asList(exTagThree, newTagSaved)))
                .build();
        withNonExistingId = CertificateDto.builder()
                .id(0)
                .name("happy easter")
                .description("XDXDXD")
                .price(777)
                .duration(2)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Arrays.asList(exTagThree, newTagSaved)))
                .build();
    }

    @SneakyThrows
    @Test
    public void findAllEndpointTest_defaultSort_statusOkAndJsonContent() {
        List<CertificateDto> expectedContent = Stream.of(exampleOne, exampleTwo, exampleThree)
                .collect(Collectors.toList());
        String expectedContentJson = objectMapper.writeValueAsString(expectedContent);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/certificates/findAll", Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedContentJson));
    }

    @SneakyThrows
    @Test
    public void findAllEndpointTest_sortByPriceDesc_statusOkAndOrderedJsonContent() {
        List<CertificateDto> expectedContent = Stream.of(exampleThree, exampleTwo, exampleOne)
                .collect(Collectors.toList());
        String expectedContentJson = objectMapper.writeValueAsString(expectedContent);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/certificates/findAll?sort=price,desc",
                        Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedContentJson));
    }

    @SneakyThrows
    @Test
    public void findByIdEndpointTest_existingCertificate_statusOkAndFoundContent() {
        ResponseEntity<CertificateDto> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/certificates/find?id=1", CertificateDto.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && exampleOne.equals(actual.getBody()));
    }

    @SneakyThrows
    @Test
    public void findByIdEndpointTest_nonExistingCertificate_errorResponse() {
        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/certificates/find?id=0", Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql"})
    @Sql(scripts = {"/set_sequence.sql"})
    public void saveCertificateEndpoint() {
        final String baseUrl = "http://localhost:" + port + "/certificates/save";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CertificateDto> request = new HttpEntity<>(newCertificate, headers);

        ResponseEntity<CertificateDto> actual = this.restTemplate.postForEntity(uri, request, CertificateDto.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.CREATED)
                && actual.getBody().getId() == 4 && actual.getBody().getTags().equals(newCertificateSaved.getTags()));
    }


    @SneakyThrows
    @Test
    public void updateCertificateEndpoint_existingId_updatedEntity() {
        final String baseUrl = "http://localhost:" + port + "/certificates/update";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CertificateDto> request = new HttpEntity<>(exampleOneUpdated, headers);

        ResponseEntity<CertificateDto> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, CertificateDto.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && exampleOneUpdated.getName().equals(actual.getBody().getName()));
    }

    @SneakyThrows
    @Test
    public void updateCertificateEndpoint_nonExistingId_errorResponse() {
        final String baseUrl = "http://localhost:" + port + "/certificates/update";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CertificateDto> request = new HttpEntity<>(withNonExistingId, headers);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void deleteCertificateEndpoint_withExistingId_statusOk() {
        String baseUrl = "http://localhost:" + port + "/certificates/delete?id=1";
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void deleteCertificateEndpoint_nonExistingId_errorResponse() {
        String baseUrl = "http://localhost:" + port + "/certificates/delete?id=0";
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static TagDto newTag;
    private static TagDto newTagSaved;
    private static CertificateDto exampleOne;
    private static CertificateDto exampleOneUpdated;
    private static CertificateDto exampleTwo;
    private static CertificateDto exampleThree;
    private static CertificateDto newCertificate;
    private static CertificateDto newCertificateSaved;
    private static CertificateDto withNonExistingId;
}
