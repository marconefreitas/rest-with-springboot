package br.com.marconefreitas.integrationtests.controllers;

import br.com.marconefreitas.config.TestConfigs;
import br.com.marconefreitas.integrationtests.AbstractIntegrationTest;
import br.com.marconefreitas.integrationtests.dto.AccountCredentialsDTO;
import br.com.marconefreitas.integrationtests.dto.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerXMLTest extends AbstractIntegrationTest {

    private static TokenDTO token;
    private static XmlMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        token = new TokenDTO();
    }

    @Test
    @Order(1)
    void signIn() throws JsonProcessingException {
        AccountCredentialsDTO cred = new AccountCredentialsDTO("marcone", "admin123");

        var createdToken =  given()
                .log().all()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(cred)
                .when()
                .post()
                .then().statusCode(200)
                .extract()
                .body().asString();

        token = objectMapper.readValue(createdToken, TokenDTO.class);

        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());

    }

    @Test
    @Order(2)
    void refresh() throws JsonProcessingException {

      var createdToken =  given()
                .log().all()
                .basePath("/auth/refresh/")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .pathParam("username", token.getUsername())
                .when()
                .put("{username}")
                .then().statusCode(200)
                .extract()
                .body().asString();

        token = objectMapper.readValue(createdToken, TokenDTO.class);
        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());
    }
}
