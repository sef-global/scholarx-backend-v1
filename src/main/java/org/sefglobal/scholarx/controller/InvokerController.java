package org.sefglobal.scholarx.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.sefglobal.scholarx.exception.HTTPClientCreationException;
import org.sefglobal.scholarx.util.InvokerUtil;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvokerController {

  private final Environment environment;

  public InvokerController(Environment environment) {
    this.environment = environment;
  }

  @RequestMapping("/api/**")
  private void sendRequestToApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Todo: Change the gateway uri (probably http://scholarx-app)
    final String gatewayUri = "http://jsonplaceholder.typicode.com";
    // Extract the uri from the request
    // Todo: use request.getPathinfo
    String[] uriArr = request.getRequestURI().split("/", 3);
    if(uriArr.length < 3){
      response.sendError(400, "Bad Request, invalid URI");
      return;
    }
    String uri = "/" + uriArr[2];
    if (request.getQueryString() != null)
      uri += "?" + request.getQueryString();
    uri = gatewayUri + uri;

    // Extract other data from request
    String method = request.getMethod();
    String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    String contentType = request.getContentType();

    if (contentType == null || contentType.isEmpty()) contentType = ContentType.APPLICATION_JSON.toString();

    HttpRequestBase executor;
    if ("GET".equalsIgnoreCase(method)) {
      executor = new HttpGet(uri);
    } else if ("POST".equalsIgnoreCase(method)) {
      executor = new HttpPost(uri);
      StringEntity payloadEntity = new StringEntity(payload, ContentType.create(contentType));
      ((HttpPost) executor).setEntity(payloadEntity);
    } else if ("PUT".equalsIgnoreCase(method)) {
      executor = new HttpPut(uri);
      StringEntity payloadEntity = new StringEntity(payload, ContentType.create(contentType));
      ((HttpPut) executor).setEntity(payloadEntity);
    } else if ("DELETE".equalsIgnoreCase(method)) {
      executor = new HttpDelete(uri);
    } else {
      response.sendError(400, "Bad Request, method not supported");
      return;
    }

    String result = execute(executor, request, response);
    if (result != null && !result.isEmpty()) response.getWriter().write(result);
  }

  private String execute(HttpRequestBase executor, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    HttpResponse response;
    try {
        response = InvokerUtil.execute(executor, 3);
    } catch (HTTPClientCreationException e) {
      resp.sendError(500, "Internal Server Error");
      return null;
    }
    BufferedReader rd;
    if (response != null) {
      rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
    } else {
      resp.sendError(500, "Unable to renew tokens");
      return null;
    }
    StringBuilder resultBuffer = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      resultBuffer.append(line);
    }
    String result = resultBuffer.toString();
    resp.setStatus(response.getStatusLine().getStatusCode());
    resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    resp.setCharacterEncoding(Consts.UTF_8.name());
    rd.close();
    return result;
  }
}
