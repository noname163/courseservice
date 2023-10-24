package com.example.courseservice.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.ForbiddenException;
import com.example.courseservice.utils.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String SERVICE = "Service ";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/swagger-ui")
                || request.getRequestURI().startsWith("/v3/api-docs")
                || request.getRequestURI().startsWith("/api/authentication")
                ||request.getMethod().equals("GET") 
                    &&request.getRequestURI().startsWith("/api/combinations")
                ||request.getMethod().equals("GET") 
                    &&request.getRequestURI().startsWith("/api/subjects")) {
            filterChain.doFilter(request, response);
        } else {
            final Optional<String> requestTokenHeaderOpt = getJwtFromRequest(request);
            if (requestTokenHeaderOpt.isPresent()) {
                try {
                    String accessToken = requestTokenHeaderOpt.get();
                    Jws<Claims> listClaims = jwtTokenUtil.getJwsClaims(accessToken, getJwtPrefix(request));
                    String email = jwtTokenUtil.getEmailFromClaims(listClaims.getBody());
                    filterChain.doFilter(request, response);
                } catch (Exception ex) {
                    throw new BadRequestException(ex.getMessage(), ex);
                }
            } else {
                throw new ForbiddenException("JWT Access Token does not start with 'Bearer '.");
            }
        }
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && (bearerToken.startsWith(BEARER) || bearerToken.startsWith(SERVICE))) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }
    private String getJwtPrefix(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken)) {
            if(bearerToken.startsWith(BEARER)){
                return BEARER;
            }
            if (bearerToken.startsWith(SERVICE)) {
                return SERVICE;
            }
        }
        return null;
    }
}
