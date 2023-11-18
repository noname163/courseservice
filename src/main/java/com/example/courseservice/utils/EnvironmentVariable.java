package com.example.courseservice.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class EnvironmentVariable {
    @Value("${allowed.content.types}")
    private String allowedContentTypes;

    @Value("${cloudinary.name}")
    private String cloudinaryName;

    @Value("${cloudinary.api.key}")
    private String cloudinaryApiKey;

    @Value("${cloudinary.api.secret}")
    private String cloudinaryApiSecret;

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Value("${jwt.secret-key-service}")
    private String jwtSecretService;

    @Value("${jwt.expires-time}")
    private long expireTime;

    @Value("${jwt.expires-time-system}")
    private int expireTimeSystem;

    @Value("${authentication.whitelistedUris}")
    private List<String> whiteListUrls;

    @Value("${general.base-url}")
    private String generalServiceBaseUrl;

    @Value("${system.request.method}")
    private String systemMethod;

    @Value("${system.email}")
    private String systemEmail;

    @Value("${video.max.segment}")
    private Integer videoMaxSegment;

    @Value("${vnpay.return-url}")
    private String vnPayReturnURL;
    
    @Value("${vnpay.redirect-url}")
    private String vnPayRedirectUrl;
    
    @Value("${vnpay.hash-secret}")
    private String vnPayHashSecret;

    @Value("${vnpay.tmn-code}")
    private String vnpayTmnCode;

    public Map<String,String> initializeAllowedContentTypes() {
        Map<String, String> result = new HashMap<>();
        String[] types = allowedContentTypes.split(",");
        for (String type : types) {
            result.put(type, type);
        }
        return result;
    }
}
