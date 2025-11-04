package br.com.marconefreitas.integrationtests.controllers;

import br.com.marconefreitas.config.TestConfigs;
import br.com.marconefreitas.integrationtests.AbstractIntegrationTest;
import br.com.marconefreitas.integrationtests.dto.PersonDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerCorsTest extends AbstractIntegrationTest {

    private static RequestSpecification requestSpecBuilder;
    private static ObjectMapper objectMapper;
    private static PersonDTO person;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        person = new PersonDTO();
    }

    @Test
    @Order(3)
    void findById() throws JsonProcessingException {
        requestSpecBuilder = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN,
                        TestConfigs.ORIGIN_3)
                .setBasePath("/api/v1/person")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content =  given(requestSpecBuilder)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("{id}", person.getId())
                .then().statusCode(200)
                .extract().body().asString();
        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;
        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertTrue(createdPerson.getId() > 0);
    }

    @Test
    @Order(1)
    void create() throws JsonProcessingException {
        mockPerson();
        requestSpecBuilder = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN,
                           TestConfigs.ORIGIN_3)
                .setBasePath("/api/v1/person")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content =  given(requestSpecBuilder)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                .post()
                .then().statusCode(200)
                .extract().body().asString();
        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertTrue(createdPerson.getId() > 0);
        Assertions.assertEquals("Richard", createdPerson.getFirstName() );
    }

    @Test
    @Order(2)
    void createWithWrongOrigin() throws JsonProcessingException {
        mockPerson();
        requestSpecBuilder = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN,
                           TestConfigs.ORIGIN_2)
                .setBasePath("/api/v1/person")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content =  given(requestSpecBuilder)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                .post()
                .then().statusCode(403)
                .extract().body().asString();

        Assertions.assertEquals("Invalid CORS request", content );
    }

    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York");
        person.setGender("Male");
        person.setEnabled(true);
    }

}