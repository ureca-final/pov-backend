package net.pointofviews.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.version("v1.0.0")
			.title("POV API")
			.description("유레카 융합 프로젝트 POV API 명세서입니다.");

		return new OpenAPI().info(info);
	}
}
