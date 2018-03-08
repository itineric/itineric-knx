package com.itineric.knx.service.core.bean;

import java.util.List;

public class Component
{
  private String _name;
  private List<String> _aliases;
  private List<String> _subElements;

  public String getName()
  {
    return _name;
  }

  public void setName(final String name)
  {
    _name = name;
  }

  public List<String> getAliases()
  {
    return _aliases;
  }

  public void setAliases(final List<String> aliases)
  {
    _aliases = aliases;
  }

  public List<String> getSubElements()
  {
    return _subElements;
  }

  public void setSubElements(final List<String> subElements)
  {
    _subElements = subElements;
  }
}
