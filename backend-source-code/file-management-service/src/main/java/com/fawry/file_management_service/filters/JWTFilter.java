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
import java.util.Arrays;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Value("${server.servlet.context-path}")
    private String applicationContextPath;

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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

        if(!claims.getBody().get("type").equals("ACCESS")) {
            throw new UnAuthorizedException("Invalid token type");
        }

        request.setAttribute("id", claims.getBody().getSubject());
        request.setAttribute("username", claims.getBody().get("username"));

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