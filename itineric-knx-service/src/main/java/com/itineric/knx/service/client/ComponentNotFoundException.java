package com.itineric.knx.service.client;

public class ComponentNotFoundException extends Exception
{
  private static final long serialVersionUID = 1L;

  public ComponentNotFoundException(final String message)
  {
    super(message);
  }
}
