package org.sefglobal.scholarx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sefglobal.scholarx.oauth.AuthAccessTokenResponseConverter;
import org.sefglobal.scholarx.util.ProfileType;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .disable()
      .authorizeRequests()
      .antMatchers("/admin/**")
      .hasAnyAuthority("ADMIN")
      .antMatchers("/**")
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .logout()
      .permitAll()
      .and()
      .exceptionHandling()
      .authenticationEntryPoint(
        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
      )
      .and()
      .oauth2Login()
      .failureHandler(new AuthFailureHandler())
      .tokenEndpoint()
      .accessTokenResponseClient(authorizationCodeTokenResponseClient());
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
}
