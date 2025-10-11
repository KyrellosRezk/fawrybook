package com.fawry.user_management_service.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PasswordHashingUtil {

    @Value("${secret.key}")
    private String secretKey;

    private SecretKeySpec keySpec;

    @PostConstruct
    private void init() {
        this.keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String hashPassword(String plainPassword) throws Exception {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] hashBytes = mac.doFinal(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) throws Exception {
        String computedHash = hashPassword(plainPassword);
        return computedHash.equals(hashedPassword);
    }
}