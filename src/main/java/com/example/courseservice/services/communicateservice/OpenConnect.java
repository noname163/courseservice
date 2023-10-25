package com.example.courseservice.services.communicateservice;

import org.springframework.web.reactive.function.client.WebClient;

public interface OpenConnect {
    public WebClient openConnect(String baseUrl);
    public WebClient openConnectWithToken(String baseUrl);
}
