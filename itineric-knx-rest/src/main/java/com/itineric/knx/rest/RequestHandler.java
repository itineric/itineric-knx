package com.itineric.knx.rest;

import com.eclipsesource.json.JsonObject;
import com.itineric.knx.service.client.KnxService;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public interface RequestHandler
{
  String JSON_RESULT_KEY = "result";
  String JSON_ACK_VALUE = "ok";

  void init(KnxService knxService) throws Exception;

  HandlerResult handle(IHTTPSession httpSession) throws Exception;

  default HandlerResult unhandledResponse()
  {
    return new HandlerResult()
    {
      @Override
      public boolean isHandled()
      {
        return false;
      }

      @Override
      public JsonObject getResult()
      {
        return null;
      }
    };
  }

  default HandlerResult handledResponse(final JsonObject jsonObject)
  {
    return new HandlerResult()
    {
      @Override
      public boolean isHandled()
      {
        return true;
      }

      @Override
      public JsonObject getResult()
      {
        return jsonObject;
      }
    };
  }
}
