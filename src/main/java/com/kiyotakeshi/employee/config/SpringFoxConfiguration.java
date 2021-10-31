package com.kiyotakeshi.employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

// @see https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
@Configuration
public class SpringFoxConfiguration {

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.OAS_30)
				.select()
//				.apis(RequestHandlerSelectors.basePackage("com.coderkan.controllers"))
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.apiInfo(metaData());
	}

	private ApiInfo metaData() {
		return new ApiInfoBuilder()
				.title("employee service with redis")
				.description("Spring Boot REST API using redis")
				.version("0.0.1")
				.contact(new Contact("kiyotakeshi", "https://github.com/kiyotakeshi", "kiyotatakeshi.work@gmail.com"))
				.build();
	}
}