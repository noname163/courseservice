package com.example.courseservice.services.communicateservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

import com.example.courseservice.data.constants.Common;
import com.example.courseservice.services.communicateservice.OpenConnect;
import com.example.courseservice.utils.JwtTokenUtil;

@Service
public class OpenConnectImpl implements OpenConnect {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    // @Autowired
    // private WebClient.Builder webClientBuilder;

    // @Override
    // public WebClient openConnect(String baseUrl) {
    //     return webClientBuilder
    //             .baseUrl(baseUrl)
    //             .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //             .build();
    // }

    // @Override
    // public WebClient openConnectWithToken(String baseUrl) {
    //     String authenToken = Common.SERVICE + jwtTokenUtil.generateJwtSytemToken();
    //     return webClientBuilder
    //             .baseUrl(baseUrl)
    //             .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //             .defaultHeader(HttpHeaders.AUTHORIZATION, authenToken)
    //             .build();
    // }

    @Override
    public HttpEntity<?>  authenticationHeader() {
        String authenToken = Common.SERVICE + jwtTokenUtil.generateJwtSytemToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, authenToken);
        return new HttpEntity<>(headers);
    }

}
