package com.fawry.user_management_service.services;

import com.fawry.user_management_service.enums.UserStatusEnum;
import com.fawry.user_management_service.exceptions.BadRequestException;
import com.fawry.user_management_service.exceptions.UnAuthorizedException;
import com.fawry.user_management_service.models.UserCredentialEntity;
import com.fawry.user_management_service.models.UserEntity;
import com.fawry.user_management_service.payloads.responses.UserBasicDataResponse;
import com.fawry.user_management_service.payloads.requests.CreateUserRequest;
import com.fawry.user_management_service.payloads.requests.SignInRequest;
import com.fawry.user_management_service.payloads.requests.SignUpRequest;
import com.fawry.user_management_service.payloads.responses.OTPResponse;
import com.fawry.user_management_service.payloads.responses.SignInResponse;
import com.fawry.user_management_service.repositories.UserCredentialRepository;
import com.fawry.user_management_service.utils.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j(topic = "AuthService")
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordHashingUtil passwordHashingUtil;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Transactional(rollbackOn =  Exception.class)
    public OTPResponse signUp(@NotNull SignUpRequest signUpRequest) throws Exception {
        UserEntity userEntity = this.userService.create(CreateUserRequest.mapToRequest(signUpRequest));
        try {
            String hashedPassword = this.passwordHashingUtil.hashPassword(signUpRequest.password());
            UserCredentialEntity userCredentialEntity = this.userCredentialRepository.save(
                    new UserCredentialEntity(userEntity, hashedPassword)
            );
            this.userCredentialRepository.save(userCredentialEntity);
            return sendOtp(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getEmail()
            );
        } catch (Exception e) {
            RedisUtils.deleteJsonValue("user", userEntity.getId());
            throw new BadRequestException(e.getMessage());
        }
    }

    public void verify(@NotNull String OTP, String email) throws Exception {
        if(!OTPUtil.validateOtp(OTP, email)) {
            throw new UnAuthorizedException("Invalid otp");
        }
        userService.verify(email);
    }

    public void resetOTP(String email) {
        UserEntity userEntity = this.userService.getByEmail(email);
        if (!userEntity.getStatus().equals(UserStatusEnum.UNVERIFIED)) {
            throw new BadRequestException("User with email: " + email + " already verified");
        }
        sendOtp(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail());
    }

    public SignInResponse signIn(@NotNull SignInRequest signInRequest) throws Exception {
        UserEntity userEntity = this.userService.getByIdentifier(signInRequest.identifier());
        if (!passwordHashingUtil.verifyPassword(
                signInRequest.password(),
                userEntity.getUserCredential().getPassword()
        )) {
            throw new UnAuthorizedException("Invalid credentials");
        }
        return generateTokens(userEntity);
    }

    public SignInResponse signInWithToken(@NotNull String userId) {
        UserEntity userEntity = this.userService.getById(userId);
        String newAccessToken = jwtUtil.generateAccessToken(userId, userEntity.getUsername(), userEntity.getEmail());
        UserBasicDataResponse user = new UserBasicDataResponse(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getFirstName(),
            userEntity.getMiddleName(),
            userEntity.getLastName(),
            ""
        );
        return new SignInResponse(
                newAccessToken,
                null,
                null,
                user
        );
    }

    public SignInResponse refreshToken(@NotNull String refreshToken, String userId) {
        UserEntity userEntity = this.userService.getById(userId);
        if(userEntity.getUserCredential().getRefreshToken() == null
            || !userEntity.getUserCredential().getRefreshToken().equals(refreshToken)) {
            throw new UnAuthorizedException("Invalid refresh token");
        }
        String newAccessToken = jwtUtil.generateAccessToken(userId, userEntity.getUsername(), userEntity.getEmail());
        return new SignInResponse(
                newAccessToken,
                null,
                null,
                null
        );
    }

    public void signOut(String userId) {
        UserCredentialEntity userCredentialEntity = this.getByUser_Id(userId);
        userCredentialEntity.setRefreshToken(null);
        userCredentialEntity.setUpdatedAt(Instant.now());
        this.userCredentialRepository.save(userCredentialEntity);
    }

    private @NotNull UserCredentialEntity getByUser_Id(String userId) {
        Optional<UserCredentialEntity> userCredentialEntity = this.userCredentialRepository.findByUser_Id(userId);
        if(userCredentialEntity.isEmpty()) {
            throw new BadRequestException("User with id: " + userId + " not found");
        }
        return userCredentialEntity.get();
    }

    private SignInResponse generateTokens(@NotNull UserEntity userEntity) {
        switch(userEntity.getStatus()) {
            case ACTIVE -> {
                return this.generateActiveUserTokens(userEntity);
            }
            case UNVERIFIED -> {
                return this.generateUnvrifiedUserTokens(
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getEmail()
                );
            }
            default -> throw new BadRequestException("User is deleted");
        }
    }

    private SignInResponse generateUnvrifiedUserTokens(String userId, String username, String email) {
        return new SignInResponse(
                null,
                null,
                jwtUtil.generateOTPToken(userId, username, email),
                null
        );
    }

    private SignInResponse generateActiveUserTokens(@NotNull UserEntity userEntity) {
        String accessToken = jwtUtil.generateAccessToken(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail()
        );
        String refreshToken = jwtUtil.generateRefreshToken(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail()
        );
        UserCredentialEntity userCredentialEntity = userEntity.getUserCredential();
        userCredentialEntity.setRefreshToken(refreshToken);
        userCredentialEntity.setUpdatedAt(Instant.now());
        this.userCredentialRepository.save(userCredentialEntity);
        UserBasicDataResponse user = new UserBasicDataResponse(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getFirstName(),
            userEntity.getMiddleName(),
            userEntity.getLastName(),
            ""
        );
        return new SignInResponse(
                accessToken,
                refreshToken,
                null,
                user
        );
    }

    private OTPResponse sendOtp(String userId, String username, String email){
        String otp = OTPUtil.generateOtp(email);
        String OTPToken = jwtUtil.generateOTPToken(userId, username, email);
        EmailUtil.sendOtpEmail(email, otp);
        return new OTPResponse(OTPToken);
    }
}
