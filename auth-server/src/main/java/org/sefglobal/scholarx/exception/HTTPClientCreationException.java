package org.sefglobal.scholarx.exception;

public class HTTPClientCreationException extends Exception{

  public HTTPClientCreationException(String message) {
    super(message);
  }

  public HTTPClientCreationException(String message, Throwable cause) {
    super(message, cause);
  }

  public HTTPClientCreationException(Throwable cause) {
    super(cause);
  }

  public HTTPClientCreationException(String message, Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
