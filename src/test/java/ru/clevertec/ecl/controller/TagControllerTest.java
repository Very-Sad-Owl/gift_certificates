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
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.GlobalDefaultExceptionHandler;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapperImpl;
import ru.clevertec.ecl.mapper.TagMapperImpl;
import ru.clevertec.ecl.service.*;
import ru.clevertec.ecl.service.certificate.CertificateServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.tag.TagServiceTestConfiguration;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {ClusterPropertiesConfiguration.class, PrimaryDbConfiguration.class, CommitLogDbConfiguration.class,
        TagMapperImpl.class, CommitLogConfiguration.class, TagController.class, TagServiceTestConfiguration.class,
        CertificateServiceTestConfiguration.class, CertificateMapperImpl.class, GlobalDefaultExceptionHandler.class,
        CommonConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "application-test.yml")
@AutoConfigureMockMvc
@EnableAutoConfiguration
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/tags.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TagControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ClusterProperties properties;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CertificateService certificateService;

    @BeforeAll
    static void setUp() {
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
                .id(4)
                .name("new tag")
                .build();
        newTagToSave = TagDto.builder()
                .name("saved")
                .build();
        duplicateTag = TagDto.builder()
                .name("holiday")
                .build();
    }

    @SneakyThrows
    @Test
    public void findAllEndpointTest_defaultSort_statusOkAndJsonContent() {
        List<TagDto> expectedContent = Stream.of(exTagOne, exTagTwo, exTagThree).collect(Collectors.toList());
        String expectedContentJson = objectMapper.writeValueAsString(expectedContent);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/tags/findAll", Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedContentJson));
    }

    @SneakyThrows
    @Test
    public void findAllEndpointTest_sortByNameDesc_statusOkAndOrderedJsonContent() {
        List<TagDto> expectedContent = Stream.of(exTagThree, exTagTwo, exTagOne).collect(Collectors.toList());
        String expectedContentJson = objectMapper.writeValueAsString(expectedContent);

        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/tags/findAll?sort=name,desc", Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedContentJson));
    }

    @SneakyThrows
    @Test
    public void findByIdEndpointTest_existingTag_statusOkAndFoundContent() {
        ResponseEntity<TagDto> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/tags/find?id=1", TagDto.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && exTagOne.equals(actual.getBody()));
    }

    @Test
    public void findByIdEndpointTest_nonExistingTag_error() {
        ResponseEntity<Object> actual = this.restTemplate
                .getForEntity("http://localhost:" + port + "/tags/find?id=0", Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {"/set_sequence.sql"})
    public void saveTagEndpoint() {
        final String baseUrl = "http://localhost:" + port + "/tags/save";
        URI uri = new URI(baseUrl);
        TagDto toSave = TagDto.builder().name("christian").build();
        TagDto expected = TagDto.builder().id(4).name("christian").build();
        String expectedJson = objectMapper.writeValueAsString(expected);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TagDto> request = new HttpEntity<>(toSave, headers);

        ResponseEntity<Object> actual = this.restTemplate.postForEntity(uri, request, Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.CREATED)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedJson));
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = {"/tags.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/set_sequence.sql"})
    public void saveTagEndpoint_duplicateTag_errorResponse() {
        final String baseUrl = "http://localhost:" + port + "/tags/save";
        URI uri = new URI(baseUrl);
        TagDto toSave = TagDto.builder().name("holiday").build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TagDto> request = new HttpEntity<>(toSave, headers);

        ResponseEntity<Object> actual = this.restTemplate.postForEntity(uri, request, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void updateTagEndpoint() {
        final String baseUrl = "http://localhost:" + port + "/tags/update";
        URI uri = new URI(baseUrl);
        TagDto toUpdate = TagDto.builder().id(1).name("updated").build();
        String expectedJson = objectMapper.writeValueAsString(toUpdate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TagDto> request = new HttpEntity<>(toUpdate, headers);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, Object.class);

        assertTrue(actual.getStatusCode().equals(HttpStatus.OK)
                && objectMapper.writeValueAsString(actual.getBody()).contains(expectedJson));
    }

    @SneakyThrows
    @Test
    public void updateEndpointTest_nonExistingTag_errorResponse() {
        final String baseUrl = "http://localhost:" + port + "/tags/update";
        URI uri = new URI(baseUrl);
        TagDto toUpdate = TagDto.builder().id(0).name("updated").build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TagDto> request = new HttpEntity<>(toUpdate, headers);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void updateEndpointTest_duplicateTagName_errorResponse() {
        final String baseUrl = "http://localhost:" + port + "/tags/update";
        URI uri = new URI(baseUrl);
        TagDto toUpdate = TagDto.builder().id(1).name("holiday").build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TagDto> request = new HttpEntity<>(toUpdate, headers);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.PUT, request, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void deleteTagEndpoint() {
        String baseUrl = "http://localhost:" + port + "/tags/delete?id=1";
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @SneakyThrows
    @Test
    public void deleteTagEndpoint_nonExistingTag_errorResponse() {
        String baseUrl = "http://localhost:" + port + "/tags/delete?id=0";
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> actual = restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static TagDto newTag;
    private static TagDto newTagToSave;
    private static TagDto duplicateTag;
}
