package com.fawry.user_management_service.utils;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Component
public class OTPUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final long OTP_EXPIRATION_MINUTES = 5;
    private static final String OTP_TABLE = "otp";

    public static @NotNull String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        RedisUtils.setKeyJsonValue(OTP_TABLE, email, otp);
        RedisUtils.setExpire(OTP_TABLE, email, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public static boolean validateOtp(String otp, String email){
        String storedOtp = RedisUtils.getKeyJsonValue(OTP_TABLE, email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            RedisUtils.deleteKeyJsonValue(OTP_TABLE, email);
            return true;
        }
        return false;
    }
}