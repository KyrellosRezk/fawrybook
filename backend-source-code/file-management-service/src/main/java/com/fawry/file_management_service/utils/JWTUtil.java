package com.fawry.file_management_service.utils;

import com.fawry.file_management_service.exceptions.UnAuthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j(topic = "JWTUtil")
public class JWTUtil {

    @Value("${jwt.rsa.public-key-path}")
    private Resource publicKeyPath;

    private RSAPublicKey publicKey;

    @PostConstruct
    private void loadKeys() throws Exception {
        String publicPem = Files.readString(publicKeyPath.getFile().toPath())
                .replaceAll("-----BEGIN (RSA )?PUBLIC KEY-----", "")
                .replaceAll("-----END (RSA )?PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] publicBytes = Base64.getDecoder().decode(publicPem);
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(publicSpec);

        log.info("RSA keys loaded successfully from");
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
}