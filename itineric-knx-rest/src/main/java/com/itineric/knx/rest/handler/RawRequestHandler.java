package com.itineric.knx.rest.handler;

import static com.itineric.knx.rest.JsonConstants.JSON_KEY__STATE;
import static com.itineric.knx.rest.JsonConstants.JSON_KEY__VALUE;
import static com.itineric.knx.rest.ParameterManager.getParameter;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.itineric.knx.rest.HandlerResult;
import com.itineric.knx.rest.RequestHandler;
import com.itineric.knx.service.client.KnxRawService;
import com.itineric.knx.service.client.KnxService;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RawRequestHandler implements RequestHandler
{
  private static final String BODY_FILE_NAME = "content";

  private static final Logger _logger = LogManager.getLogger(RawRequestHandler.class);

  private static final java.util.regex.Pattern _groupAddressPattern =
    Pattern.compile("/raw/groupAddress/(\\d+[/]\\d+[/]\\d+)");

  private KnxService _knxService;
  private KnxRawService _knxRawService;

  @Override
  public void init(final KnxService knxService) throws Exception
  {
    _knxService = knxService;
    _knxRawService = _knxService.getKnxRawService();
  }

  @Override
  public HandlerResult handle(final IHTTPSession httpSession) throws Exception
  {
    final Method method = httpSession.getMethod();
    final String uri = httpSession.getUri();
    final Map<String, List<String>> parameters = httpSession.getParameters();

    final java.util.regex.Matcher groupAddressMatcher = _groupAddressPattern.matcher(uri);
    if (groupAddressMatcher.matches())
    {
      final String groupAddress = groupAddressMatcher.group(1);

      if (Method.GET.equals(method))
      {
        final Class<?> type = getParameter(parameters, Class.class, "type");
        final Object result = _knxRawService.read(type, groupAddress);
        final JsonObject jsonObject = new JsonObject();
        if (boolean.class.equals(type)
          || Boolean.class.equals(type))
        {
          jsonObject.add(JSON_KEY__STATE, (Boolean)result);
        }
        else
        {
          jsonObject.add(JSON_KEY__STATE, result.toString());
        }
        return handledResponse(jsonObject);
      }
      else if (Method.PUT.equals(method))
      {
        final Map<String, String> files = new HashMap<String, String>();
        httpSession.parseBody(files);
        final String contentFilePath = files.get(BODY_FILE_NAME);
        final JsonObject input;
        final InputStreamReader isr = new InputStreamReader(new FileInputStream(contentFilePath));
        try
        {
          input = Json.parse(isr).asObject();
        }
        finally
        {
          isr.close();
        }

        final JsonValue value = input.get(JSON_KEY__VALUE);
        if (value.isBoolean())
        {
          _knxRawService.write(groupAddress, value.asBoolean());
        }
        else
        {
          final String message = "Unexpected result type, result was[" + value.toString() + "]";
          if (_logger.isErrorEnabled())
          {
            _logger.error(message);
          }
          throw new Exception(message);
        }

        final JsonObject jsonObject = new JsonObject().add(JSON_RESULT_KEY, JSON_ACK_VALUE);
        return handledResponse(jsonObject);
      }
    }
    return unhandledResponse();
  }
}
