package de.germanmatrimony.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.germanmatrimony.backend.service.OTPService;
import de.germanmatrimony.backend.service.PasswordResetService;
import java.util.Map;
import de.germanmatrimony.backend.service.EmailService;
import de.germanmatrimony.backend.repository.UserRepository;



@RestController
@RequestMapping("api/auth/forgot")
public class ForgotPasswordController {

    @Autowired
    private OTPService otpService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    private final PasswordResetService passwordResetService;

    public ForgotPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

@PostMapping("/forgot-password")
public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
    String email = body.get("email");

    if (email == null || email.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Email is required.");
    }

    try {
        passwordResetService.sendResetOTP(email);
        System.out.println("✅ Forgot password OTP template email sent to: " + email);
        return ResponseEntity.ok("OTP sent successfully to your email.");
    } catch (RuntimeException e) {
        System.out.println("❌ Failed to send forgot password OTP to " + email + ": " + e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
    }
}




    @PostMapping("/verify-otp")
public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String otp = body.get("otp");

    if (passwordResetService.verifyOTP(email, otp)) {
        return ResponseEntity.ok("OTP verified successfully");
    } else {
        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }
}


@PostMapping("/resend-otp")
public ResponseEntity<String> resendForgotOtp(@RequestBody Map<String, String> body) {
    String email = body.get("email");

    if (email == null || email.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Email is required.");
    }

    if (!userRepository.findByEmail(email).isPresent()) {
        System.out.println("❌ Email not found in DB: " + email);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found.");
    }

    // ✅ Check if OTP was already generated
    if (!otpService.hasResetOtp(email)) {
        System.out.println("❌ Cannot resend OTP — initial forgot-password not triggered for: " + email);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No OTP request found. Please trigger Forgot Password first.");
    }

    try {
        String otp = otpService.generateResetOtp(email);
        System.out.println("✅ Resending RESET OTP for " + email + ": " + otp);

        // ✅ Template message setup
        emailService.sendTemplateEmail(
            email,
            "Your Reset OTP (Resent)",
            "forgot_password_otp_resend.html",
            Map.of(
                "otp", otp,
                "introMessage", "Here is your OTP again to reset your password:"
            )
        );

        return ResponseEntity.ok("Reset OTP resent successfully.");
    } catch (RuntimeException e) {
        System.out.println("❌ Failed to resend OTP to " + email + ": " + e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
    }
}



    @PostMapping("/reset-password")
public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String newPassword = body.get("newPassword");

    passwordResetService.resetPassword(email, newPassword);
    return ResponseEntity.ok("Password reset successful");
}





}
