package com.itineric.knx.service.client;

public enum ComponentType
{
  LIGHT("light"),
  COVER("cover");

  private final String _key;

  private ComponentType(final String key)
  {
    _key = key;
  }

  public String getKey()
  {
    return _key;
  }
}
