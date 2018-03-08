package com.itineric.knx.rest;

import com.eclipsesource.json.JsonObject;

public interface HandlerResult
{
  boolean isHandled();

  JsonObject getResult();
}
