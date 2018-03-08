package com.itineric.knx.rest;

import java.util.List;
import java.util.Map;

public abstract class ParameterManager
{
  private ParameterManager()
  {
  }

  public static <T> T getParameter(final Map<String, List<String>> parameters,
                                   final Class<T> clazz,
                                   final String name)
    throws ParameterException
  {
    final String valueAsString = getParameter(parameters, name);
    if (valueAsString == null)
    {
      return null;
    }
    final Object result;
    if (Class.class.equals(clazz))
    {
      if ("boolean".equals(valueAsString))
      {
        result = boolean.class;
      }
      else if ("short".equals(valueAsString))
      {
        result = short.class;
      }
      else if ("int".equals(valueAsString))
      {
        result = int.class;
      }
      else if ("long".equals(valueAsString))
      {
        result = long.class;
      }
      else if ("float".equals(valueAsString))
      {
        result = float.class;
      }
      else if ("double".equals(valueAsString))
      {
        result = double.class;
      }
      else
      {
        try
        {
          result = Class.forName(valueAsString);
        }
        catch (final ClassNotFoundException exception)
        {
          throw new ParameterException("Failed to convert parameter [" + name + "] with value [" + valueAsString
                                         + "] to class",
                                       exception);
        }
      }
    }
    else
    {
      throw new ParameterException("Unexpected type [" + clazz + "] for parameter [" + name + "]");
    }
    @SuppressWarnings("unchecked")
    final T tmp = (T)result;
    return tmp;
  }

  private static String getParameter(final Map<String, List<String>> parameters,
                                     final String name)
    throws ParameterException
  {
    final List<String> values = parameters.get(name);
    if (values == null)
    {
      return null;
    }
    if (values.size() == 0)
    {
      return null;
    }
    if (values.size() == 1)
    {
      return values.get(0);
    }
    throw new ParameterException("Parameter [" + name + "] seems to be a list but expecting single value");
  }

}
