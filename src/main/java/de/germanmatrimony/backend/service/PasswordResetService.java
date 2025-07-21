package de.germanmatrimony.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.germanmatrimony.backend.model.User;
import de.germanmatrimony.backend.repository.UserRepository;




@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final OTPService otpService;
    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository, OTPService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public void sendResetOTP(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            // For security, do not reveal if the email is registered; just return.
            return;
        }

            String otp = otpService.generateResetOtp(email);


        try {
        emailService.sendEmail(email, "Your OTP for Password Reset", "Your OTP is: " + otp);
        System.out.println("✅ OTP email sent successfully to " + email);
        } catch (Exception e) {
        System.out.println("❌ Failed to send email: " + e.getMessage());
        throw new RuntimeException("Failed to send OTP email.");
                }

    }

    public boolean verifyOTP(String email, String otp) {
        return otpService.validateResetOtp(email, otp);
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
    }
}
