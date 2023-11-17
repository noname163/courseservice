package com.example.courseservice.services.authenticationservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.courseservice.configs.CustomUserDetails;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.ForbiddenException;
import com.example.courseservice.services.authenticationservice.SecurityContextService;

@Service
public class SecurityContextServiceImpl implements SecurityContextService {
    @Autowired
    private SecurityContext securityContext;

    private Boolean loginStatus;
    private String email;

    @Override
    public void setSecurityContext(UserInformation userInformation) {
        if (userInformation == null) {
            throw new ForbiddenException("Token not valid.");
        }
        UserDetails userDetails = new CustomUserDetails(userInformation);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        securityContext.setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Override
    public UserInformation getCurrentUser() {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            throw new BadRequestException("Current user is empty");
        }
        Object principal = authentication.getPrincipal();
        return ((CustomUserDetails) principal).getUser();
    }

    @Override
    public void validateCurrentUser(UserInformation user) {
        UserInformation currentUser = getCurrentUser();
        if (!currentUser.getEmail().equals(user.getEmail())) {
            throw new BadRequestException("Invalid User");
        }
    }

    @Override
    public UserInformation isLogin() {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        return ((CustomUserDetails) principal).getUser();
    }

    @Override
    public void setSecurityContextNull(UserInformation userInformation) {
        if (userInformation != null) {
            UserDetails userDetails = new CustomUserDetails(userInformation);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            securityContext.setAuthentication(usernamePasswordAuthenticationToken);
        }
        securityContext.setAuthentication(null);
    }

    @Override
    public boolean getIsAuthenticatedAndIsStudent() {
        Authentication authentication = securityContext.getAuthentication();
        var roles = authentication.getAuthorities();
        var role = roles.stream().findFirst();
        if(role.get().getAuthority().equals("ROLE_ANONYMOUS")&& !role.get().getAuthority().equals("STUDENT")){
            return false;
        }
        return true;
    }

    @Override
    public Boolean getLoginStatus() {
        return loginStatus;
    }

    @Override
    public void setLoginStatus(Boolean status) {
        this.loginStatus = status;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setStudentEmail(String email) {
        this.email = email;
    }
}
