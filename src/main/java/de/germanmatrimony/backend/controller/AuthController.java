package de.germanmatrimony.backend.controller;

import de.germanmatrimony.backend.model.User;
import de.germanmatrimony.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginUser) {
        Optional<User> result = authService.login(loginUser.getEmail(), loginUser.getPassword());
        return result.isPresent() ? "Login successful" : "Invalid credentials";
    }
}
