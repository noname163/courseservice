package com.example.courseservice.services.authenticationservice;

import com.example.courseservice.data.object.UserInformation;

public interface SecurityContextService {
    public void setSecurityContext(UserInformation userInformation);

    public boolean getIsAuthenticatedAndIsStudent();

    public void setSecurityContextNull(UserInformation userInformation);

    public void validateCurrentUser(UserInformation user);

    public UserInformation getCurrentUser();

    public UserInformation isLogin();

    public Boolean getLoginStatus();

    public void setLoginStatus(Boolean status);
}
