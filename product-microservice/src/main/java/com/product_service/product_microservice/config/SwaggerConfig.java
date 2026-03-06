package com.product_service.product_microservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI productServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Microservice API")
                        .description("REST API for Product Management in E-commerce Application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("E-commerce Team")
                                .email("support@ecommerce.com")));
    }
}
