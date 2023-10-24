package com.example.courseservice.data.object;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistedUri {
    private String uri;
    private String httpMethod;


    public boolean matches(HttpServletRequest request) {
        boolean uriMatches = request.getRequestURI().startsWith(uri);
        boolean methodMatches = httpMethod == null || request.getMethod().equals(httpMethod);
        return uriMatches && methodMatches;
    }

    public WhitelistedUri parseWhitelistedUri(String uriString) {
        String[] parts = uriString.split(":");
        if (parts.length == 1) {
            return WhitelistedUri.builder().uri(parts[0]).build(); // URL without an HTTP method
        } else if (parts.length == 2) {
            return new WhitelistedUri(parts[0], parts[1]); // URL and HTTP method
        } else {
            throw new IllegalArgumentException("Invalid whitelisted URI format: " + uriString);
        }
    }
}

