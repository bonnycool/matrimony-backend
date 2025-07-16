package de.germanmatrimony.backend.service;

import de.germanmatrimony.backend.model.User;
import de.germanmatrimony.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Optional<User> register(User user) {
    try {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Optional.empty(); // User already exists
        }

        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    } catch (Exception e) {
        e.printStackTrace();  // Important: see what is failing
        return Optional.empty();  // Optional: return null response
    }
}

    // ✅ Login
    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Validate password
            if (encoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty(); // Invalid login
    }
}
