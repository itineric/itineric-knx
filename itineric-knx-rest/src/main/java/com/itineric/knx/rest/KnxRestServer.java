package com.itineric.knx.rest;

import static com.itineric.knx.rest.ConfigurationConstants.KEY__HANDLERS;
import static com.itineric.knx.rest.ConfigurationConstants.KEY__PORT;
import static com.itineric.knx.rest.JsonConstants.JSON_KEY__MESSAGE;
import static com.itineric.knx.rest.JsonConstants.JSON_KEY__STACKTRACE;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.eclipsesource.json.JsonObject;
import com.itineric.knx.service.client.KnxService;
import com.itineric.knx.service.client.KnxServiceFactory;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class KnxRestServer extends NanoHTTPD
{
  private static final String JSON_MIME_TYPE = "application/json";

  private final List<RequestHandler> _handlers;

  public KnxRestServer(final int port,
                       final List<RequestHandler> handlers)
    throws IOException
  {
    super(port);
    _handlers = handlers;
  }

  @Override
  public Response serve(final IHTTPSession httpSession)
  {
    Throwable throwable = null;
    HandlerResult handlerResult = null;
    for (final RequestHandler handler : _handlers)
    {
      try
      {
        handlerResult = handler.handle(httpSession);
      }
      catch (final Throwable t)
      {
        throwable = t;
        break;
      }
      if (handlerResult.isHandled())
      {
        break;
      }
    }

    final Status status;
    final JsonObject jsonObject;
    if (throwable != null)
    {
      status = Status.INTERNAL_ERROR;
      jsonObject = new JsonObject().add(JSON_KEY__MESSAGE, throwable.getMessage());
      final StringWriter sw = new StringWriter();
      final PrintWriter pw = new PrintWriter(sw);
      throwable.printStackTrace(pw);
      jsonObject.add(JSON_KEY__STACKTRACE, sw.toString());
    }
    else if (handlerResult == null || !handlerResult.isHandled())
    {
      status = Status.BAD_REQUEST;
      jsonObject = new JsonObject().add(JSON_KEY__MESSAGE, "Unknown API call");
    }
    else
    {
      status = Status.OK;
      jsonObject = handlerResult.getResult();
    }

    return newFixedLengthResponse(status, JSON_MIME_TYPE, jsonObject.toString());
  }

  public static void main(final String[] args) throws Exception
  {
    if (args.length != 1)
    {
      System.err.println("Expecting one argument: properties config file");
      System.exit(-1);;
    }

    final String configurationFilePath = args[0];

    final Properties properties = new Properties();
    properties.load(new FileReader(configurationFilePath));

    final KnxService knxService = KnxServiceFactory.getKnxService(properties);

    final String portAsString = properties.getProperty(KEY__PORT);
    final int port = Integer.parseInt(portAsString);
    final String requestHandlersAsString = properties.getProperty(KEY__HANDLERS);
    final String[] requestHandlerClassNames = requestHandlersAsString.split(",");
    final List<RequestHandler> requestHandlers = new ArrayList<RequestHandler>();
    for (final String requestHandlerClassName : requestHandlerClassNames)
    {
      @SuppressWarnings("unchecked")
      final Class<RequestHandler> requestHandlerClass =
        (Class<RequestHandler>) Class.forName(requestHandlerClassName);
      final RequestHandler requestHandler = requestHandlerClass.newInstance();
      requestHandler.init(knxService);
      requestHandlers.add(requestHandler);
    }

    final KnxRestServer knxRestServer = new KnxRestServer(port, requestHandlers);
    knxRestServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
  }
}
