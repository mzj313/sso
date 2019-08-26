package com.chinasofti.oauth2.cache.redis.conversion;

public class InvalideMapException extends RuntimeException
{
  public InvalideMapException(String message)
  {
    super(message);
  }

  public InvalideMapException(String message, Throwable cause) {
    super(message, cause);
  }
}