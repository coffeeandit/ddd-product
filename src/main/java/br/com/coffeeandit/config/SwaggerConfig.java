package br.com.coffeeandit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("br.com.coffeeandit")).build()
                .consumes(getContentType()).produces(getContentType()).securitySchemes(getSecuritySchemes())
                .apiInfo(apiInfo());
    }

    private Set<String> getContentType() {
        final HashSet<String> mediaType = new HashSet<>();
        mediaType.add(MediaType.APPLICATION_JSON_UTF8_VALUE);
        return mediaType;
    }

    private List<SecurityScheme> getSecuritySchemes() {
        List<SecurityScheme> schemeList = new ArrayList<>();
        schemeList.add(new BasicAuth("basicAuth"));
        return schemeList;
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "API de Exemplo de Coffeeandit",
                "", "API 1.1.5", "Terms of service",
                new Contact("Coffeeandit", "coffeeandit.com.br", "coffeeandit@coffeeandit.com.br"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
