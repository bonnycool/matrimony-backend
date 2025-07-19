package de.germanmatrimony.backend.controller;

import de.germanmatrimony.backend.dto.UserDTO;
import de.germanmatrimony.backend.model.User;
import de.germanmatrimony.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.germanmatrimony.backend.service.EmailService;
import de.germanmatrimony.backend.util.OtpUtil;
import java.util.Map;


import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;





   
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> result = authService.login(loginUser.getEmail(), loginUser.getPassword());

        if (result.isPresent()) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

  // ✅ NEW: Send OTP + Store user temporarily
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody UserDTO userDTO) {
        String message = authService.sendOtpToUser(userDTO);
        if (message.equals("Email already registered.")) {
            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.ok(message);
    }


     // ✅ NEW: Verify OTP + Finalize registration
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        String result = authService.verifyOtpAndRegister(email, otp);
        if (result.equals("User registered successfully.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

}
