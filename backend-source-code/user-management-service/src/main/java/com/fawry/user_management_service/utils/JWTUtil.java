package com.fawry.user_management_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawry.user_management_service.enums.TokenTypeEnum;
import com.fawry.user_management_service.exceptions.UnAuthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j(topic = "JWTUtil")
public class JWTUtil {

    @Value("${jwt.rsa.private-key-path}")
    private Resource privateKeyPath;

    @Value("${jwt.rsa.public-key-path}")
    private Resource publicKeyPath;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void loadKeys() throws Exception {
        String privatePem = Files.readString(privateKeyPath.getFile().toPath())
                .replaceAll("-----BEGIN (RSA )?PRIVATE KEY-----", "")
                .replaceAll("-----END (RSA )?PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] privateBytes = Base64.getDecoder().decode(privatePem);
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);

        String publicPem = Files.readString(publicKeyPath.getFile().toPath())
                .replaceAll("-----BEGIN (RSA )?PUBLIC KEY-----", "")
                .replaceAll("-----END (RSA )?PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] publicBytes = Base64.getDecoder().decode(publicPem);
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateSpec);
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(publicSpec);

        log.info("RSA keys loaded successfully from");
    }

    public String generateAccessToken(String userId, String username, String email) {
        return generateToken(userId, getAccessTokenValidity(), TokenTypeEnum.ACCESS, username, email);
    }

    public String generateRefreshToken(String userId, String username, String email) {
        return generateToken(userId, getRefreshTokenValidity(), TokenTypeEnum.REFRESH, username, email);
    }

    public String generateOTPToken(String userId, String username, String email) {
        return generateToken(userId, getOTPTokenValidity(), TokenTypeEnum.OTP, username, email);
    }

    private String generateToken(String subject, Duration validity, TokenTypeEnum type, String username, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plus(validity);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("type", type.name())
                .claim("username", username)
                .claim("email", email)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Jws<Claims> verifyToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException("Token is expired");
        } catch (SignatureException e) {
            throw new UnAuthorizedException("Invalid Token");
        } catch (JwtException e) {
            throw new UnAuthorizedException(e.getMessage());
        }
    }

    public Duration getAccessTokenValidity() { return Duration.ofMinutes(15); }

    public Duration getRefreshTokenValidity() { return Duration.ofDays(30); }

    public Duration getOTPTokenValidity() { return Duration.ofMinutes(5); }
}