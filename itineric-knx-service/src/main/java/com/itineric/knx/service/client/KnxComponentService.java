package com.itineric.knx.service.client;

public interface KnxComponentService
{
  <T> T read(ComponentType componentType, String name)
    throws Exception;

  void write(ComponentType componentType, String name, boolean value)
    throws Exception;
}
