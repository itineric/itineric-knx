package com.itineric.knx.service.client;

public interface KnxService
{
  void start() throws Exception;

  void stop();

  boolean isRunning();

  KnxRawService getKnxRawService() throws Exception;

  KnxComponentService getKnxComponentService() throws Exception;
}
