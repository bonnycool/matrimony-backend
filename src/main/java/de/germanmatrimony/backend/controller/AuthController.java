package de.germanmatrimony.backend.controller;

import de.germanmatrimony.backend.dto.UserDTO;
import de.germanmatrimony.backend.model.User;
import de.germanmatrimony.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            Optional<User> registeredUser = authService.register(userDTO);

            if (registeredUser.isEmpty()) {
                return ResponseEntity.badRequest().body("User already exists with this email");
            }

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> result = authService.login(loginUser.getEmail(), loginUser.getPassword());

        if (result.isPresent()) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
