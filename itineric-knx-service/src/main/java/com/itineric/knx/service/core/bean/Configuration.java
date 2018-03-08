package com.itineric.knx.service.core.bean;

import java.util.List;

public class Configuration
{
  private List<LightComponent> _lights;
  private List<CoverComponent> _covers;

  public List<LightComponent> getLights()
  {
    return _lights;
  }

  public void setLight(final List<LightComponent> lights)
  {
    _lights = lights;
  }

  public List<CoverComponent> getCovers()
  {
    return _covers;
  }

  public void setCover(final List<CoverComponent> covers)
  {
    _covers = covers;
  }
}
