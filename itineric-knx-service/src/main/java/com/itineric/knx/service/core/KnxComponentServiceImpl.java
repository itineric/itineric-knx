package com.itineric.knx.service.core;

import static com.itineric.knx.service.core.ConfigurationConstants.KEY__COMPONENTS_CONFIGURATION_FILE;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.itineric.knx.service.client.ComponentNotFoundException;
import com.itineric.knx.service.client.ComponentType;
import com.itineric.knx.service.client.KnxComponentService;
import com.itineric.knx.service.client.KnxRawService;
import com.itineric.knx.service.client.type.Percent;
import com.itineric.knx.service.core.bean.Component;
import com.itineric.knx.service.core.bean.Configuration;
import com.itineric.knx.service.core.bean.CoverComponent;
import com.itineric.knx.service.core.bean.LightComponent;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class KnxComponentServiceImpl implements KnxComponentService
{
  private final Map<String, LightComponent> _lightComponents =
    new HashMap<String, LightComponent>();
  private final Map<String, CoverComponent> _coverComponents =
    new HashMap<String, CoverComponent>();
  private final KnxServiceImpl _knxService;
  private final KnxRawService _knxRawService;

  public KnxComponentServiceImpl(final KnxServiceImpl knxService,
                                 final KnxLink knxLink,
                                 final Properties properties)
    throws Exception
  {
    _knxService = knxService;
    _knxRawService = knxService.getKnxRawService();
    final String configurationFilePath =
      properties.getProperty(KEY__COMPONENTS_CONFIGURATION_FILE);

    final File configurationFile = new File(configurationFilePath);
    if (configurationFile.exists())
    {
      final Constructor constructor = new Constructor(Configuration.class);
      final TypeDescription configurationDescription = new TypeDescription(Configuration.class);
      configurationDescription.addPropertyParameters("lights", LightComponent.class);
      configurationDescription.addPropertyParameters("covers", CoverComponent.class);
      constructor.addTypeDescription(configurationDescription);
      final Yaml yaml = new Yaml(constructor);
      final Configuration configuration;
      final InputStream inputStream = new FileInputStream(configurationFile);
      try
      {
        configuration = (Configuration) yaml.load(inputStream);
      }
      finally
      {
        inputStream.close();
      }

      fillComponentsMap(_lightComponents, configuration.getLights());
      fillComponentsMap(_coverComponents, configuration.getCovers());
    }
  }

  @Override
  public <T> T read(final ComponentType componentType,
                    final String name)
    throws Exception
  {
    _knxService.checkRunning();
    Object result;
    switch (componentType)
    {
      case LIGHT:
        result = readLight(name);
        break;
      case COVER:
        result = readCover(name);
        break;
      default:
        throw new Exception("Unexpected component type [" + componentType + "]");
    }
    @SuppressWarnings("unchecked")
    final T tmp = (T)result;
    return tmp;
  }

  @Override
  public void write(final ComponentType componentType,
                    final String name,
                    final boolean value)
    throws Exception
  {
    _knxService.checkRunning();
    switch (componentType)
    {
      case LIGHT:
        writeLight(name,
                   value);
        break;
      case COVER:
        writeCover(name,
                   value);
        break;
      default:
        throw new Exception("Unexpected component type [" + componentType + "]");
    }
  }

  private <T extends Component> void fillComponentsMap(final Map<String, T> componentsMap,
                                                       final List<T> components)
  {
    for (final T component : components)
    {
      final String name = component.getName();
      final String normalizedName = normalize(name);
      componentsMap.put(normalizedName, component);
      final List<String> aliases = component.getAliases();
      if (aliases != null)
      {
        for (final String alias : aliases)
        {
          final String normalizedAlias = normalize(alias);
          componentsMap.put(normalizedAlias, component);
        }
      }
    }
  }

  private Object readLight(final String name)
    throws Exception
  {
    final LightComponent lightComponent = getLightComponent(name);
    final String stateAddress = lightComponent.getStateAddress();
    final String address = lightComponent.getAddress();
    final String actualAddress = stateAddress == null ? address : stateAddress;
    return _knxRawService.read(boolean.class, actualAddress);
  }

  private void writeLight(final String name, final boolean value)
    throws Exception
  {
    final LightComponent lightComponent = getLightComponent(name);
    final String address = lightComponent.getAddress();
    if (address == null)
    {
      final List<String> subElements = lightComponent.getSubElements();
      for (final String subElementName : subElements)
      {
        writeLight(subElementName, value);
      }
    }
    else
    {
      _knxRawService.write(address, value);
    }
  }

  private Object readCover(final String name)
    throws Exception
  {
    final CoverComponent coverComponent = getCoverComponent(name);
    final String stateAddress = coverComponent.getPositionStateAddress();
    return _knxRawService.read(Percent.class, stateAddress);
  }

  private void writeCover(final String name, final boolean value)
    throws Exception
  {
    final CoverComponent coverComponent = getCoverComponent(name);
    final String address = coverComponent.getMoveAddress();
    if (address == null)
    {
      final List<String> subElements = coverComponent.getSubElements();
      for (final String subElementName : subElements)
      {
        writeCover(subElementName, value);
      }
    }
    else
    {
      _knxRawService.write(address, value);
    }
  }

  private LightComponent getLightComponent(final String name)
    throws ComponentNotFoundException
  {
    return getComponent(_lightComponents, name);
  }

  private CoverComponent getCoverComponent(final String name)
    throws ComponentNotFoundException
  {
    return getComponent(_coverComponents, name);
  }

  private <T> T getComponent(final Map<String, T> components,
                             final String name)
    throws ComponentNotFoundException
  {
    final String normalizedName = normalize(name);
    final T component = components.get(normalizedName);
    if (component == null)
    {
      throw new ComponentNotFoundException("Component named [" + name + "] was not found (normalized name ["
                                             + normalizedName +"])");
    }
    return component;
  }

  private String normalize(final String value)
  {
    String normalizedValue = Normalizer.normalize(value, Normalizer.Form.NFD);
    normalizedValue = normalizedValue.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    normalizedValue = normalizedValue.replaceAll("'", " ");
    normalizedValue = normalizedValue.replaceAll(" ", "-");
    return normalizedValue;
  }
}
