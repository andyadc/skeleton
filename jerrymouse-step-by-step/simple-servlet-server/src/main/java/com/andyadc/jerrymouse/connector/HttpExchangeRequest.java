package com.andyadc.jerrymouse.connector;

import java.net.URI;

public interface HttpExchangeRequest {

    String getRequestMethod();

    URI getRequestURI();

}
