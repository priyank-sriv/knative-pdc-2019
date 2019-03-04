package com.appdirect.demo.functions.domain.exception;

public final class ParserException extends RuntimeException {

  public ParserException(String message) {
    super(message);
  }

  public ParserException(String message, Throwable cause) {
    super(message, cause);
  }
}
