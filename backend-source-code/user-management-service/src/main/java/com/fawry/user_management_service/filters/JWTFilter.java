package com.fawry.user_management_service.filters;

import com.fawry.user_management_service.enums.TokenTypeEnum;
import com.fawry.user_management_service.exceptions.UnAuthorizedException;
import com.fawry.user_management_service.utils.JWTUtil;
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
    private final ArrayList<String> whiteList;
    private final ArrayList<String> accessTokenList;
    private final ArrayList<String> refreshTokenList;
    private final ArrayList<String> OTPTokenList;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.whiteList = new ArrayList<>(List.of(
                "v1/auth/sign-up",
                "v1/auth/sign-in",
                "v1/position",
                "v1/governorate"
        ));
        this.accessTokenList = new ArrayList<>(List.of(
                "v1/auth/sign-in-with-token",
                "v1/auth/sign-in",
                "v1/auth/sign-out",
                "v1/user/suggestions",
                "v1/user/profile",
                "v1/request/pagination",
                "v1/request/action",
                "v1/request/reply"
        ));
        this.refreshTokenList = new ArrayList<>(List.of(
                "v1/auth/refresh-token"
        ));
        this.OTPTokenList = new ArrayList<>(List.of(
                "v1/auth/reset-otp",
                "v1/auth/verify"
        ));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws IOException, ServletException {

        String path = request.getRequestURI().substring(applicationContextPath.length());

        if(whiteList.contains(path) || path.startsWith("swagger-ui") || path.startsWith("v3/api-docs")) {
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

        if(accessTokenList.contains(path)){
            if(!claims.getBody().get("type").equals(TokenTypeEnum.ACCESS.toString())) {
                throw new UnAuthorizedException("Invalid token type");
            }
            request.setAttribute("id", claims.getBody().getSubject());
            request.setAttribute("username", claims.getBody().get("username"));
        } else if(refreshTokenList.contains(path)){
            if(!claims.getBody().get("type").equals(TokenTypeEnum.REFRESH.toString())) {
                throw new UnAuthorizedException("Invalid token type");
            }
            if(path.endsWith("refresh-token")) {
                request.setAttribute("token", token);
            }
            request.setAttribute("id", claims.getBody().getSubject());
        } else if(OTPTokenList.contains(path)){
            if(!claims.getBody().get("type").equals(TokenTypeEnum.OTP.toString())) {
                throw new UnAuthorizedException("Invalid token type");
            }
            request.setAttribute("email", claims.getBody().get("email"));
        }
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