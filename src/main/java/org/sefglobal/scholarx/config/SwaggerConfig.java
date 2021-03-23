package org.sefglobal.scholarx.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(
        RequestHandlerSelectors.basePackage("org.sefglobal.scholarx.controller")
      )
      .paths(PathSelectors.any())
      .build()
      .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    Contact contact = new Contact(
      "SEF",
      "https://sefglobal.org",
      "sustainableedufoundation@gmail.com"
    );
    return new ApiInfo(
      "ScholarX API",
      "Backend APIs of ScholarX platform.",
      "0.0.1-SNAPSHOT",
      null,
      contact,
      "MIT License",
      "https://github.com/sef-global/scholarx/blob/master/LICENSE",
      Collections.emptyList()
    );
  }
}
