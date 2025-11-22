package br.com.marconefreitas.integrationtests.controllers;

import br.com.marconefreitas.config.TestConfigs;
import br.com.marconefreitas.integrationtests.AbstractIntegrationTest;
import br.com.marconefreitas.integrationtests.dto.AccountCredentialsDTO;
import br.com.marconefreitas.integrationtests.dto.PersonDTO;
import br.com.marconefreitas.integrationtests.dto.TokenDTO;
import br.com.marconefreitas.integrationtests.wrappers.xml.PagedModel;
import br.com.marconefreitas.mapper.YAMLMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerYAMLTest extends AbstractIntegrationTest {

    private static RequestSpecification requestSpecBuilder;
    private static YAMLMapper objectMapper;
    private static PersonDTO person;

    private static TokenDTO token;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        person = new PersonDTO();
        token = new TokenDTO();
    }

    @Test
    @Order(0)
    void signIn() throws JsonProcessingException {
        AccountCredentialsDTO cred = new AccountCredentialsDTO("marcone", "admin123");

        token =  given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
                                        ContentType.TEXT)))
                .log().all()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(cred, objectMapper)
                .when()
                .post()
                .then().statusCode(200)
                .extract()
                .body().as(TokenDTO.class, objectMapper);

        requestSpecBuilder = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN,
                        TestConfigs.ORIGIN_3)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .setBasePath("/api/v1/person")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();


        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());

    }

    @Test
    @Order(1)
    void create() throws JsonProcessingException {
        mockPerson();


        PersonDTO createdPerson =  given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(requestSpecBuilder)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
                .when()
                .post()
                .then().statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract().body()
                .as(PersonDTO.class, objectMapper);
        person = createdPerson;

        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertTrue(createdPerson.getId() > 0);
        Assertions.assertEquals("Richard", createdPerson.getFirstName() );
    }

    @Test
    @Order(2)
    void update() throws JsonProcessingException {
        person.setLastName("Tested");

        var createdPerson =  given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(requestSpecBuilder)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
                .when().put()
                .then().statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract().body().
                as(PersonDTO.class, objectMapper);
        person = createdPerson;

        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertTrue(createdPerson.getId() > 0);
        Assertions.assertEquals("Tested", createdPerson.getLastName());
    }

    @Test
    @Order(3)
    void findById() throws JsonProcessingException {


        var createdPerson =  given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(requestSpecBuilder)
              //  .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .when()
                .get("{id}", person.getId())
                .then().statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract().body().as(PersonDTO.class, objectMapper);
        person = createdPerson;

        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertTrue(createdPerson.getId() > 0);
    }

    @Test
    @Order(4)
    void disablePerson() throws JsonProcessingException {


        var createdPerson =  given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(requestSpecBuilder)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .when()
                .patch("{id}", person.getId())
                .then().statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract().body().as(PersonDTO.class, objectMapper);
        person = createdPerson;

        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertTrue(createdPerson.getId() > 0);
        Assertions.assertFalse(createdPerson.getEnabled());
    }

    @Test
    @Order(5)
    void deletePerson() throws JsonProcessingException {

        given(requestSpecBuilder)
                .when()
                .delete("{id}", person.getId())
                .then().statusCode(204);

    }

    @Test
    @Order(6)
    void findAll() throws JsonProcessingException {


        var content =  given(requestSpecBuilder)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("firstName", "Marcone")
                .queryParam("page", 0, "size", 10, "direction", "ASC" )
                .when().get("findByName/{firstName}")
                .then().statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract().body().as(PagedModel.class, objectMapper);

        List<PersonDTO> lista = content.getContent();
        PersonDTO personOne = lista.get(0);
        person = personOne;

        Assertions.assertNotNull(personOne.getId());
        Assertions.assertTrue(personOne.getId() > 0);
        Assertions.assertEquals("Marcone", personOne.getFirstName());
        Assertions.assertEquals("Freitas", personOne.getLastName());
    }

    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York");
        person.setGender("Male");
        person.setEnabled(true);
    }

}
