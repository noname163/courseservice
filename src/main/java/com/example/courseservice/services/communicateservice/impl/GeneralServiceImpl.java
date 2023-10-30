package com.example.courseservice.services.communicateservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
    @Autowired
    private RestTemplate restTemplate;

    private final String generalBaseURL = "https://general-service/api/service";

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
        HttpEntity<?> requestEntity = openConnect.authenticationHeader();
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                generalBaseURL + "/check-token/{token}",
                HttpMethod.GET,
                requestEntity,
                Boolean.class,
                token
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                throw new BadRequestException("Error 1: " + responseEntity.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            throw new BadRequestException("Error 2: " + ex.getMessage());
        }
    }

}
