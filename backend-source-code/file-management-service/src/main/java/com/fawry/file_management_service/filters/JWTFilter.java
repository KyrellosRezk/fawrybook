package com.fawry.file_management_service.filters;

import com.fawry.file_management_service.exceptions.UnAuthorizedException;
import com.fawry.file_management_service.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Value("${server.servlet.context-path}")
    private String applicationContextPath;

    private final JWTUtil jwtUtil;

    private final ArrayList<String> OTPTokenList;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

        this.OTPTokenList = new ArrayList<>(List.of(
                "v1/file/profile"
        ));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws IOException, ServletException {

        String path = request.getRequestURI().substring(applicationContextPath.length());

        if(path.startsWith("swagger-ui") || path.startsWith("v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (token == null) {
            throw new UnAuthorizedException("Token is not provided");
        }

        Jws<Claims> claims = jwtUtil.verifyToken(token);

        if(OTPTokenList.contains(path)){
            if(!claims.getBody().get("type").equals("OTP")) {
                throw new UnAuthorizedException("Invalid token type");
            }
        } else {
            if(!claims.getBody().get("type").equals("ACCESS")) {
                throw new UnAuthorizedException("Invalid token type");
            }
        }

        request.setAttribute("id", claims.getBody().getSubject());

        filterChain.doFilter(request, response);
    }

    protected String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        } else if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}