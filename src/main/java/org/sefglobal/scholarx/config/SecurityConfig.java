package org.sefglobal.scholarx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import org.sefglobal.scholarx.oauth.AuthAccessTokenResponseConverter;
import org.sefglobal.scholarx.oauth.OAuthAuthenticationSuccessHandler;
import org.sefglobal.scholarx.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  @Lazy
  CustomOidcUserService customOidcUserService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .cors()
            .and()
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/api/admin/**")
            .hasAnyAuthority("ADMIN")
            .antMatchers("/api/programs")
            .permitAll()
            .antMatchers("/api/programs/*")
            .permitAll()
            .antMatchers("/api/programs/*/mentors")
            .permitAll()
            .antMatchers("/api/mentors/*")
            .permitAll()
            .antMatchers("/api/**")
            .authenticated()
            .anyRequest()
            .permitAll()
            .and()
            .logout()
            .logoutSuccessHandler(new LogoutSuccessHandler() {
              @Override
              public void onLogoutSuccess(HttpServletRequest request,
                                          HttpServletResponse httpServletResponse,
                                          Authentication authentication) throws IOException, ServletException {
                String url = request.getHeader("Referer");
                String[] urlArray = url.split("/");
                String baseURL = urlArray[0] + "//" + urlArray[2];
                httpServletResponse.sendRedirect(baseURL);
              }
            })
            .permitAll()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            )
            .and()
            .oauth2Login()
            .failureHandler(new AuthFailureHandler())
            .successHandler(successHandler())
            .permitAll()
            .userInfoEndpoint().oidcUserService(customOidcUserService).and()
            .tokenEndpoint()
            .accessTokenResponseClient(authorizationCodeTokenResponseClient());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
    configuration.setAllowedOrigins(ImmutableList.of("https://scholarx.sefglobal.org"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Configuration
  public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedMethods("*");
    }
  }

  private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
    OAuth2AccessTokenResponseHttpMessageConverter converter = new OAuth2AccessTokenResponseHttpMessageConverter();
    converter.setTokenResponseConverter(new AuthAccessTokenResponseConverter());

    RestTemplate restTemplate = new RestTemplate(
            Arrays.asList(new FormHttpMessageConverter(), converter)
    );
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

    DefaultAuthorizationCodeTokenResponseClient responseClient = new DefaultAuthorizationCodeTokenResponseClient();
    responseClient.setRestOperations(restTemplate);

    return responseClient;
  }

  private static class AuthFailureHandler
          implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    )
            throws IOException, ServletException {
      Map<String, Object> data = new HashMap<>();
      response.setContentType("application/json");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      data.put("exception", exception.getMessage());
      response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }
  }

  @Bean
  public AuthenticationSuccessHandler successHandler() {
    return new OAuthAuthenticationSuccessHandler();
  }
}
