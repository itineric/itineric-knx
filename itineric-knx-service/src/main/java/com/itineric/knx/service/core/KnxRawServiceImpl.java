package com.itineric.knx.service.core;

import java.util.Properties;

import com.itineric.knx.service.client.KnxRawService;
import com.itineric.knx.service.client.type.Percent;
import tuwien.auto.calimero.exception.KNXException;

public class KnxRawServiceImpl implements KnxRawService
{
  private final KnxServiceImpl _knxService;
  private final KnxLink _knxLink;

  public KnxRawServiceImpl(final KnxServiceImpl knxService,
                           final KnxLink knxLink,
                           final Properties properties)
    throws KNXException, InterruptedException
  {
    _knxService = knxService;
    _knxLink = knxLink;
  }

  @Override
  public Object read(final Class<?> type, final String groupAddress)
    throws Exception
  {
    _knxService.checkRunning();
    final Object result;
    if (boolean.class.equals(type)
      || Boolean.class.equals(type))
    {
      result = _knxLink.readBool(groupAddress);
    }
    else if (Percent.class.equals(type))
    {
      result = _knxLink.readPercent(groupAddress);
    }
    else
    {
      throw new KNXException("Unexpected state type [" + type + "]");
    }
    return result;
  }

  @Override
  public void write(final String groupAddress, final boolean value)
    throws Exception
  {
    _knxService.checkRunning();
    _knxLink.write(groupAddress, value);
  }
}
