package de.germanmatrimony.backend.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private static final int EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;
    private static final long RESEND_INTERVAL = 60 * 1000;

    // Separate maps for Registration and Reset Password OTPs
    private final Map<String, OtpEntry> registrationOtpMap = new ConcurrentHashMap<>();
    private final Map<String, OtpEntry> resetOtpMap = new ConcurrentHashMap<>();

    // Rate limiting logs (shared)
    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();

    // 🔹 Generate OTP for Registration
    public String generateRegistrationOtp(String email) {
        return generateOtp(email, registrationOtpMap, "REGISTRATION");
    }

    // 🔹 Generate OTP for Forgot Password
    public String generateResetOtp(String email) {
        return generateOtp(email, resetOtpMap, "RESET PASSWORD");
    }

    // 🔹 Validate Registration OTP
    public boolean validateRegistrationOtp(String email, String otp) {
        return validateOtp(email, otp, registrationOtpMap);
    }

    // 🔹 Validate Reset OTP
    public boolean validateResetOtp(String email, String otp) {
        return validateOtp(email, otp, resetOtpMap);
    }

    // 🔹 Clear OTP (optional usage)
    public void clearRegistrationOtp(String email) {
        registrationOtpMap.remove(email);
    }

    public void clearResetOtp(String email) {
        resetOtpMap.remove(email);
    }

    // 🔹 Internal OTP generator logic with limits
    private String generateOtp(String email, Map<String, OtpEntry> otpMap, String purpose) {
        long now = System.currentTimeMillis();

        System.out.println("==> GENERATE OTP called for: " + purpose + " | " + email);
        System.out.println("==> otpMap contains: " + otpMap.containsKey(email));
        System.out.println("==> requestLog contains: " + requestLog.containsKey(email));

        OtpEntry lastEntry = otpMap.get(email);
        if (lastEntry != null && now - lastEntry.timestamp < RESEND_INTERVAL) {
            long waitTime = (RESEND_INTERVAL - (now - lastEntry.timestamp)) / 1000;
            throw new RuntimeException("Please wait " + waitTime + "s before requesting again.");
        }

        List<Long> attempts = requestLog.getOrDefault(email, new ArrayList<>());
        attempts.removeIf(t -> now - t > 10 * 60 * 1000); // clear old attempts

        if (attempts.size() >= MAX_ATTEMPTS) {
            throw new RuntimeException("Too many OTP requests. Try again later.");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpMap.put(email, new OtpEntry(otp, now));
        attempts.add(now);
        requestLog.put(email, attempts);

        System.out.println("==> OTP GENERATED [" + purpose + "]: " + otp);
        return otp;
    }

    // 🔹 Internal OTP validator
    private boolean validateOtp(String email, String otp, Map<String, OtpEntry> otpMap) {
        OtpEntry entry = otpMap.get(email);
        if (entry == null || !entry.otp.equals(otp)) return false;
        if ((System.currentTimeMillis() - entry.timestamp) > EXPIRY_MINUTES * 60 * 1000) return false;
        otpMap.remove(email); // one-time use
        return true;
    }

    // 🔹 Inner class to store OTP and timestamp
    private static class OtpEntry {
        String otp;
        long timestamp;
        OtpEntry(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
