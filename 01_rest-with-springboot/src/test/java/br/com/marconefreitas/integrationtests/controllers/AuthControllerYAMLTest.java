package br.com.marconefreitas.integrationtests.controllers;

import br.com.marconefreitas.config.TestConfigs;
import br.com.marconefreitas.integrationtests.AbstractIntegrationTest;
import br.com.marconefreitas.integrationtests.dto.AccountCredentialsDTO;
import br.com.marconefreitas.integrationtests.dto.TokenDTO;
import br.com.marconefreitas.mapper.YAMLMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerYAMLTest extends AbstractIntegrationTest {

    private static TokenDTO token;
    private static YAMLMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        token = new TokenDTO();
    }

    @Test
    @Order(1)
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


        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());

    }

    @Test
    @Order(2)
    void refresh() throws JsonProcessingException {

      token =  given()
              .config(RestAssuredConfig
                      .config()
                      .encoderConfig(EncoderConfig
                              .encoderConfig()
                              .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE,
                                      ContentType.TEXT)))
                .log().all()
                .basePath("/auth/refresh/")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .pathParam("username", token.getUsername())
                .when()
                .put("{username}")
                .then().statusCode(200)
                .extract()
                .body().as(TokenDTO.class, objectMapper);

        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());
    }
}
