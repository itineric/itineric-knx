package com.itineric.knx.rest;

public class ParameterException extends RestApiException
{
  private static final long serialVersionUID = 1L;

  public ParameterException()
  {
  }

  public ParameterException(final String message)
  {
    super(message);
  }

  public ParameterException(final String message, final Throwable cause)
  {
    super(message, cause);
  }
}
