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

    @Override
    public HttpEntity<?> authenticationHeader() {
        String authenToken = Common.SERVICE + jwtTokenUtil.generateJwtSytemToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, authenToken);
        return new HttpEntity<>(headers);
    }

}
