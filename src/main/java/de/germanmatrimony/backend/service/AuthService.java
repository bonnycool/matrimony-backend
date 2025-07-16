package de.germanmatrimony.backend.service;

import de.germanmatrimony.backend.dto.UserDTO;
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

    public Optional<User> register(UserDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return Optional.empty();
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());

        return Optional.of(userRepository.save(user));
    }

    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (encoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
