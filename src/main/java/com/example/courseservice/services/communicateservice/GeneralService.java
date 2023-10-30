package com.example.courseservice.services.communicateservice;

import com.example.courseservice.data.object.SendMail;
import com.example.courseservice.data.object.UserInformation;

public interface GeneralService {
    public void sendMail(SendMail sendMail);
    public UserInformation getUserInformation(String email);
    public boolean checkToken(String token);
}
