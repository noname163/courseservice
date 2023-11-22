package com.example.courseservice.filters;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.WhitelistedUri;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.ForbiddenException;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.utils.EnvironmentVariable;
import com.example.courseservice.utils.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String SERVICE = "Service ";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private EnvironmentVariable environmentVariables;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isUriWhitelisted(request)) {
            if (request.getRequestURI().contentEquals("/api/courses/user")
                    || request.getRequestURI().contains("/api/videos/user")
                    || (request.getRequestURI().contentEquals("/api/videos") && request.getMethod().equals("GET"))) {
                processAuthenticationOfCourse(request, response, filterChain);
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            processAuthentication(request, response, filterChain);
        }
    }

    private boolean isUriWhitelisted(HttpServletRequest request) {
        List<String> whitelistedUris = environmentVariables.getWhiteListUrls();
        for (String whitelistedUri : whitelistedUris) {
            WhitelistedUri whitelistedUriObject = new WhitelistedUri();
            whitelistedUriObject = whitelistedUriObject.parseWhitelistedUri(whitelistedUri);
            if (whitelistedUriObject.matches(request)) {
                return true;
            }
        }
        return false;
    }

    private void processAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) {
        final Optional<String> requestTokenHeaderOpt = getJwtFromRequest(request);
        if (requestTokenHeaderOpt.isPresent()) {
            try {
                String accessToken = requestTokenHeaderOpt.get();
                Jws<Claims> jwtClaims = jwtTokenUtil.getJwsClaims(accessToken, getJwtPrefix(request));
                Claims claims = jwtClaims.getBody();
                UserInformation userInformation = UserInformation
                        .builder()
                        .id(Long.parseLong(claims.get("id").toString()))
                        .email(claims.get("email").toString())
                        .role(claims.get("role").toString())
                        .fullname(claims.get("fullName").toString())
                        .build();
                securityContextService.setSecurityContext(userInformation);
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                throw new BadRequestException(ex.getMessage(), ex);
            }
        } else {
            throw new ForbiddenException("JWT Access Token does not start with 'Bearer '.");
        }
    }

    private void processAuthenticationOfCourse(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        final Optional<String> requestTokenHeaderOpt = getJwtFromRequest(request);
        if (requestTokenHeaderOpt.isPresent() && !requestTokenHeaderOpt.isEmpty()) {
            try {
                String accessToken = requestTokenHeaderOpt.get();
                Jws<Claims> jwtClaims = jwtTokenUtil.getJwsClaims(accessToken, getJwtPrefix(request));
                Claims claims = jwtClaims.getBody();
                if (claims != null && !claims.equals("")) {
                    securityContextService.setLoginStatus(true);
                    UserInformation userInformation = UserInformation
                            .builder()
                            .id(Long.parseLong(claims.get("id").toString()))
                            .email(claims.get("email").toString())
                            .role(claims.get("role").toString())
                            .fullname(claims.get("fullName").toString())
                            .build();
                    securityContextService.setSecurityContext(userInformation);
                } else {
                    securityContextService.setLoginStatus(false);
                }
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                throw new BadRequestException(ex.getMessage(), ex);
            }
        } else {
            securityContextService.setLoginStatus(false);
            securityContextService.setSecurityContextNull(null);
            filterChain.doFilter(request, response);
        }
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && (bearerToken.startsWith(BEARER) || bearerToken.startsWith(SERVICE))) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    private String getJwtPrefix(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.startsWith(BEARER)) {
                return BEARER;
            }
            if (bearerToken.startsWith(SERVICE)) {
                return SERVICE;
            }
        }
        return null;
    }
}
