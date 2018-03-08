package com.itineric.knx.service.core;

import static com.itineric.knx.service.core.ConfigurationConstants.KEY__HOST;
import static com.itineric.knx.service.core.ConfigurationConstants.KEY__PORT;

import java.util.Properties;

import com.itineric.knx.service.client.KnxComponentService;
import com.itineric.knx.service.client.KnxRawService;
import com.itineric.knx.service.client.KnxService;

public class KnxServiceImpl implements KnxService
{
  private final Properties _properties;
  private final String _host;
  private final int _port;
  private KnxLink _knxLink;
  private KnxRawService _knxRawService;
  private KnxComponentService _knxComponentService;
  private boolean _running;

  public KnxServiceImpl(final Properties properties)
    throws Exception
  {
    _properties = properties;
    _host = properties.getProperty(KEY__HOST);
    final String portAsString = properties.getProperty(KEY__PORT);
    _port = Integer.parseInt(portAsString);
    start();
  }

  @Override
  public void start() throws Exception
  {
    _knxLink = new KnxLink(_host, _port);
    _knxLink.start();

    _knxRawService = new KnxRawServiceImpl(this, _knxLink, _properties);
    _knxComponentService = new KnxComponentServiceImpl(this, _knxLink, _properties);

    _running = true;
  }

  @Override
  public void stop()
  {
    _running = false;
    _knxLink.stop();
  }

  @Override
  public boolean isRunning()
  {
    return _running;
  }

  @Override
  public KnxRawService getKnxRawService() throws Exception
  {
    return _knxRawService;
  }

  @Override
  public KnxComponentService getKnxComponentService() throws Exception
  {
    return _knxComponentService;
  }

  void checkRunning() throws Exception
  {
    if (!_running)
    {
      throw new Exception("Knx service is not up");
    }
  }
}
