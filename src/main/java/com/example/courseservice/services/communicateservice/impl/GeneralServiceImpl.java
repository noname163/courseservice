package com.example.courseservice.services.communicateservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.courseservice.data.object.SendMail;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.communicateservice.GeneralService;
import com.example.courseservice.services.communicateservice.OpenConnect;
import com.example.courseservice.utils.EnvironmentVariable;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class GeneralServiceImpl implements GeneralService {

    @Autowired
    private EnvironmentVariable environmentVariable;
    @Autowired
    private OpenConnect openConnect;

    private final String generalBaseURL = "http://localhost:8080/api/service";

    @Override
    public void sendMail(SendMail sendMail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMail'");
    }

    @Override
    public UserInformation getUserInformation(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserInformation'");
    }

    @Override
    public boolean checkToken(String token) {
        WebClient webClient = openConnect.openConnectWithToken(generalBaseURL);

        // Use a Mono<Boolean> instead of Mono<Object> to represent the response
        Mono<Boolean> responseMono = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/check-token/{token}")
                    .build(token))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.error("Errors {}", clientResponse.createException().block().getMessage());
                    throw new BadRequestException("Errors");
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.error("Errors {}", clientResponse.createException().block().getMessage());
                    throw new BadRequestException("Errors");
                })
                .bodyToMono(Boolean.class);

        return responseMono.blockOptional().orElse(false);
    }

}
