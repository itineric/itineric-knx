package com.itineric.knx.rest;

public class RestApiException extends Exception
{
  private static final long serialVersionUID = 1L;

  public RestApiException()
  {
  }

  public RestApiException(final String message)
  {
    super(message);
  }

  public RestApiException(final String message, final Throwable cause)
  {
    super(message, cause);
  }
}
