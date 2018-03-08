package com.itineric.knx.service.core.bean;

public class LightComponent extends Component
{
  private String _address;
  private String _stateAddress;

  public String getAddress()
  {
    return _address;
  }

  public void setAddress(final String address)
  {
    _address = address;
  }

  public String getStateAddress()
  {
    return _stateAddress;
  }

  public void setStateAddress(final String stateAddress)
  {
    _stateAddress = stateAddress;
  }
}
