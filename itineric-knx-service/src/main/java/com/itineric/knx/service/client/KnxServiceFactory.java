package com.itineric.knx.service.client;

import java.util.Properties;

import com.itineric.knx.service.core.KnxServiceImpl;

public abstract class KnxServiceFactory
{
  public static KnxService getKnxService(final Properties properties) throws Exception
  {
    return new KnxServiceImpl(properties);
  }
}
