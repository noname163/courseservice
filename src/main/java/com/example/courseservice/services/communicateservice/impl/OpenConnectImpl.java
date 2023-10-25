package com.example.courseservice.services.communicateservice.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.courseservice.data.constants.Common;
import com.example.courseservice.services.communicateservice.OpenConnect;

@Service
public class OpenConnectImpl implements OpenConnect {

    @Override
    public WebClient openConnect(String baseUrl) {
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public WebClient openConnectWithToken(String baseUrl, String token) {
        String authenToken = Common.BEARER + token;
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authenToken)
                .build();
    }
    
}
