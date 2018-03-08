package com.itineric.knx.service.core.bean;

public class CoverComponent extends Component
{
  private String _moveAddress;
  private String _stopAddress;
  private String _positionStateAddress;

  public String getMoveAddress()
  {
    return _moveAddress;
  }

  public void setMoveAddress(final String moveAddress)
  {
    _moveAddress = moveAddress;
  }

  public String getStopAddress()
  {
    return _stopAddress;
  }

  public void setStopAddress(final String stopAddress)
  {
    _stopAddress = stopAddress;
  }

  public String getPositionStateAddress()
  {
    return _positionStateAddress;
  }

  public void setPositionStateAddress(final String positionStateAddress)
  {
    _positionStateAddress = positionStateAddress;
  }
}
