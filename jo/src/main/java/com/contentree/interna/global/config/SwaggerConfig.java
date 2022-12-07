package com.contentree.interna.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@EnableWebMvc
public class SwaggerConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
        		.useDefaultResponseMessages(false)
        		.securityContexts(Arrays.asList(securityContext()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.contentree.interna"))
                .paths(PathSelectors.ant("/api/**"))
                .paths(PathSelectors.ant("/kakao/**"))
                .build().apiInfo(apiInfo());

    }

    private ApiInfo apiInfo() {
        String description = "중앙그룹 통합 회원 서비스";
        return new ApiInfoBuilder()
                .title("Jo! API")
                .description(description)
                .version("1.0")
                .build();
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes), new SecurityReference("Refresh", authorizationScopes));
    }
}