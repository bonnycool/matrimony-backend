package de.germanmatrimony.backend.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10)); // generates digit between 0-9
        }

        return otp.toString();
    }
}
