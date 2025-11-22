package br.com.marconefreitas.integrationtests.controllers;

import br.com.marconefreitas.config.TestConfigs;
import br.com.marconefreitas.integrationtests.AbstractIntegrationTest;
import br.com.marconefreitas.integrationtests.dto.AccountCredentialsDTO;
import br.com.marconefreitas.integrationtests.dto.TokenDTO;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerJsonTest extends AbstractIntegrationTest {

    private static TokenDTO token;

    @BeforeAll
    static void setUp() {
        token = new TokenDTO();
    }

    @Test
    @Order(1)
    void signIn() {
        AccountCredentialsDTO cred = new AccountCredentialsDTO("marcone", "admin123");

        token =  given()
                .log().all()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cred)
                .when()
                .post()
                .then().statusCode(200)
                .extract()
                .body().as(TokenDTO.class);

        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());

    }

    @Test
    @Order(2)
    void refresh() {

        token =  given()
                .log().all()
                .basePath("/auth/refresh/")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .pathParam("username", token.getUsername())
                .when()
                .put("{username}")
                .then().statusCode(200)
                .extract()
                .body().as(TokenDTO.class);
    }
}
