package com.example.courseservice.services.communicateservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.courseservice.data.object.SendMail;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.communicateservice.GeneralService;
import com.example.courseservice.services.communicateservice.OpenConnect;
import com.example.courseservice.utils.EnvironmentVariable;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class GeneralServiceImpl implements GeneralService {

    @Autowired
    private EnvironmentVariable environmentVariable;
    @Autowired
    private OpenConnect openConnect;
    @Autowired
    private RestTemplate restTemplate;

    private final String generalBaseURL = "http://localhost:8084/api";

    @Override
    public void sendMail(SendMail sendMail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMail'");
    }

    @Override
    public UserInformation getUserInformation(String email) {
        HttpEntity<?> requestEntity = openConnect.authenticationHeader();
        log.info("Start Calling user-information API ");
        try {
            ResponseEntity<UserInformation> responseEntity = restTemplate.exchange(
                    generalBaseURL + "/user-information?email=" + email,
                    HttpMethod.GET,
                    requestEntity,
                    UserInformation.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                throw new BadRequestException("Error 1: " + responseEntity.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            throw new BadRequestException("Error 2: " + ex.getMessage());
        }
    }

    @Override
    public boolean checkToken(String token) {
        HttpEntity<?> requestEntity = openConnect.authenticationHeader();
        log.info("Start Call Check Token Api");

        ResponseEntity<Boolean> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    generalBaseURL + "/check-token/{token}",
                    HttpMethod.GET,
                    requestEntity,
                    Boolean.class,
                    token);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Check Token Success");
                return responseEntity.getBody();
            }
        } catch (HttpStatusCodeException ex) {
            log.error("Check Token Error: Status Code [{}], Response Body [{}]", ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            throw new BadRequestException("Error: " + ex.getRawStatusCode());
        }

        log.error("Check Token Error: Unknown Error");
        throw new BadRequestException("Unknown Error");
    }

}
