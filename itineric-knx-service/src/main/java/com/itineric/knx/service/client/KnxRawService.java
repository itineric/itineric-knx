package com.itineric.knx.service.client;

public interface KnxRawService
{
  Object read(Class<?> type, String groupAddress)
    throws Exception;

  void write(String groupAddress, boolean value)
    throws Exception;
}
