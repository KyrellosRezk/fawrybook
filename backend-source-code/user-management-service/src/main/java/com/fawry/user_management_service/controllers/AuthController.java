package com.fawry.user_management_service.controllers;

import com.fawry.user_management_service.payloads.requests.SignInRequest;
import com.fawry.user_management_service.payloads.requests.SignUpRequest;
import com.fawry.user_management_service.payloads.requests.VerifyUserRequest;
import com.fawry.user_management_service.payloads.responses.SignInResponse;
import com.fawry.user_management_service.payloads.responses.OTPResponse;
import com.fawry.user_management_service.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("v1/auth")
@Slf4j(topic = "AuthController")
public class AuthController {
    private final AuthService authService;

    @PostMapping("sign-up")
    public OTPResponse signUp(@RequestBody SignUpRequest signUpRequest) throws Exception {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("verify")
    public void verify(
            @RequestBody VerifyUserRequest verifyUserRequest,
            HttpServletRequest request
    ) throws Exception {
        String email = (String) request.getAttribute("email");
        this.authService.verify(verifyUserRequest.otp(), email);
    }

    @GetMapping("reset-otp")
    public void resetOTP(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        this.authService.resetOTP(email);
    }

    @PostMapping("sign-in")
    public SignInResponse signIn(@RequestBody SignInRequest signInRequest) throws Exception {
        return this.authService.signIn(signInRequest);
    }

    @PostMapping("sign-in-with-token")
    public SignInResponse signInWithToken(HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        return this.authService.signInWithToken(userId);
    }

    @PostMapping("refresh-token")
    public SignInResponse refreshToken(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        String userId = (String) request.getAttribute("id");
        return this.authService.refreshToken(token, userId);
    }

    @PostMapping("sign-out")
    public void signOut(HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        this.authService.signOut(userId);
    }
}
