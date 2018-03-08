package com.itineric.knx.service.core;

import java.net.InetSocketAddress;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class KnxLink
{
  private final String _host;
  private final int _port;
  private KNXNetworkLink _knxLink;
  private ProcessCommunicatorImpl _processCommunicator;

  public KnxLink(final String host, final int port)
  {
    _host = host;
    _port = port;
  }

  public void start() throws KNXException, InterruptedException
  {
    final TPSettings settings = new TPSettings();
    final InetSocketAddress address = new InetSocketAddress(_host, _port);
    _knxLink = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING,
                                    null,
                                    address,
                                    true,
                                    settings);

    _processCommunicator = new ProcessCommunicatorImpl(_knxLink);
  }

  public void stop()
  {
    if (_processCommunicator != null)
    {
      _processCommunicator.detach();
    }

    if (_knxLink != null)
    {
      _knxLink.close();
    }
  }

  public boolean readBool(final String address)
    throws KNXException, InterruptedException
  {
    final GroupAddress groupAddress = new GroupAddress(address);
    return _processCommunicator.readBool(groupAddress);
  }

  public int readPercent(final String address)
    throws KNXException, InterruptedException
  {
    final GroupAddress groupAddress = new GroupAddress(address);
    return _processCommunicator.readUnsigned(groupAddress, ProcessCommunicator.SCALING);
  }

  public void write(final String address, final boolean value)
    throws KNXException, InterruptedException
  {
    final GroupAddress groupAddress = new GroupAddress(address);
    _processCommunicator.write(groupAddress, value);
  }
}
