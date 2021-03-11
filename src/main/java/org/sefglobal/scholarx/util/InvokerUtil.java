package org.sefglobal.scholarx.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.sefglobal.scholarx.exception.HTTPClientCreationException;

public class InvokerUtil {

  public static HttpResponse execute(HttpRequestBase executor, int retryCount)
      throws IOException, HTTPClientCreationException {
    if (retryCount == 0) {
      return null;
    }

    CloseableHttpClient client = getHTTPClient();

    return client.execute(executor);
  }

  public static CloseableHttpClient getHTTPClient() throws HTTPClientCreationException {
    SSLContextBuilder builder = new SSLContextBuilder();
    try {
      builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
      SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(
          builder.build());
      return HttpClients.custom().setSSLSocketFactory(
          sslSF).build();
    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
      throw new HTTPClientCreationException("Error occurred while retrieving http client", e);
    }
  }

}
