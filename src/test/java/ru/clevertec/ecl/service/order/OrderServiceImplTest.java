package ru.clevertec.ecl.service.order;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.*;
import ru.clevertec.ecl.service.*;
import ru.clevertec.ecl.service.certificate.CertificateServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.tag.TagServiceTestConfiguration;
import ru.clevertec.ecl.service.user.UserServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogService;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {OrderServiceTestConfiguration.class, OrderMapperImpl.class, CertificateMapperImpl.class,
        TagMapperImpl.class,
        UserMapperImpl.class,
        CertificateServiceTestConfiguration.class,
        UserServiceTestConfiguration.class,
        CommitLogDbConfiguration.class,
        PrimaryDbConfiguration.class,
        CommitLogConfiguration.class,
        ClusterPropertiesConfiguration.class,
        TagServiceTestConfiguration.class,
        UserServiceTestConfiguration.class,
        CommonConfiguration.class})
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql", "/users.sql", "/orders.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderServiceImplTest {

    @Autowired
    OrderService service;
    @Autowired
    CertificateService certificateService;
    @Autowired
    CommitLogService commitLogService;
    @Autowired
    UserService userService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CertificateMapper certificateMapper;
    @Autowired
    UserMapper userMapper;

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
        secondNode = OrderDto.builder()
                .id(2)
                .certificate(exampleCertificateTwo)
                .price(5.6)
                .purchaseTime(LocalDateTime.parse("2012-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(2)
                .userId(1)
                .build();
    }

    @Test
    @Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql", "/users.sql", "/orders.sql"})
    @Sql(scripts = {"/set_sequence.sql"})
    public void saveTest(){
        OrderDto toSave = OrderDto.builder()
                .certificateId(1)
                .userId(1)
                .build();

        OrderDto expected = OrderDto.builder()
                .id(3)
                .certificate(exampleCertificateOne)
                .price(2.1)
                .purchaseTime(LocalDateTime.parse("2011-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(1)
                .userId(1)
                .build();

        OrderDto actual = service.save(toSave);

        assertTrue(compareOrdersExcludingPurchaseTime(expected, actual));
    }

    @Test
    void findById_existingId_equalDtos() {
        long id = 2;

        OrderDto actual = service.findById(id);

        assertTrue(compareOrdersExcludingPurchaseTime(secondNode, actual));
    }

    @Test
    void findById_nonExistingId_exc() {
        long id = 0;

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.findById(id)
        );

        assertNotNull(thrown);
    }

    @Test
    void getAll_paginationNotSpecified_allPages() {
        List<OrderDto> expected = Arrays.asList(firstNode, secondNode);

        List<OrderDto> actual = service.getAll(new OrderDto(),
                PageRequest.of(0, 2)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_firstPageWithOneNodePerPage_firstPageElement() {
        List<OrderDto> expected = Arrays.asList(firstNode);

        List<OrderDto> actual = service.getAll(new OrderDto(),
                PageRequest.of(0, 1)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void delete_existingId_noExceptionsThrown() {
        long id = 1;
        assertDoesNotThrow(() -> service.delete(id));
    }

    @Test
    void delete_nonExistingId_exception() {
        long id = 0;
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.delete(id)
        );
        assertNotNull(thrown);
    }

    @Test
    void update_changeCertificate_orderWithNewCertificate() {
        OrderDto updated = OrderDto.builder()
                .id(1)
                .certificate(exampleCertificateThree)
                .price(10.5)
                .purchaseTime(LocalDateTime.parse("2011-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .user(exampleUser)
                .certificateId(3)
                .userId(1)
                .build();

        OrderDto actual = service.update(updated);

        assertEquals(updated, actual);
    }

    @Test
    void update_nonExistingId_notFoundException() {
        long id = 0;
        firstNode.setId(id);
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.update(firstNode)
        );

        assertNotNull(thrown);
    }

    @Test
    void findByUserId_existingUser_allUserOrders() {
        long userId = 1;

        List<OrderDto> expectedContent = new ArrayList<>(Arrays.asList(firstNode, secondNode));
        Page<OrderDto> actual = service.findByUserId(userId, Pageable.unpaged());

        assertEquals(expectedContent, actual.getContent());
    }

    @Test
    void findByUserId_nonExistingUser_emptyContent() {
        long userId = 0;

        Page<OrderDto> actual = service.findByUserId(userId, Pageable.unpaged());

        assertEquals(0, actual.getContent().size());
    }

    @Test
    void findOrdersWithCertificate_existingCertificate_allOrdersWithGivenCertificate() {
        Set<OrderDto> expected = new HashSet<>(Arrays.asList(secondNode));

        Set<OrderDto> actual = service.findOrdersWithCertificateById(exampleCertificateTwo.getId());

        assertEquals(expected, actual);
    }

    @Test
    void findOrdersWithCertificate_nonExistingCertificate_emptySet() {
        CertificateDto containingCertificate = CertificateDto.builder()
                .id(0)
                .build();
        Set<OrderDto> actual = service.findOrdersWithCertificateById(containingCertificate.getId());

        assertEquals(0, actual.size());
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
    private static OrderDto secondNode;
    private static UserDto exampleUser;
}