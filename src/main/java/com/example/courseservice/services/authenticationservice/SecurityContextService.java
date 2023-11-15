package com.example.courseservice.services.authenticationservice;

import com.example.courseservice.data.object.UserInformation;

public interface SecurityContextService {
    public void setSecurityContext(UserInformation userInformation);

    public void validateCurrentUser(UserInformation user);

    public UserInformation getCurrentUser();

}
