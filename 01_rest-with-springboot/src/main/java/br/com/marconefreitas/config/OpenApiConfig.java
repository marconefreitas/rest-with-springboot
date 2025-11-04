package br.com.marconefreitas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenApi(){
        return new OpenAPI().info(new Info()
                .title("REST API's Restful from 0 with Java, SpringBoot and etc")
                .version("v1")
                .description("Anything")
                .license(new License().name("Apache 2.0"))
        );
    }


}
