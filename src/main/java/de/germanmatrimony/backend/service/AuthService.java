package de.germanmatrimony.backend.service;

import de.germanmatrimony.backend.dto.UserDTO;
import de.germanmatrimony.backend.model.User;
import de.germanmatrimony.backend.repository.UserRepository;
import de.germanmatrimony.backend.util.OtpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import de.germanmatrimony.backend.service.OTPService;



@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

      // Store OTPs and temp user data
    private final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserDTO> pendingUserStore = new ConcurrentHashMap<>();

    @Autowired
    private OTPService otpService;


      // ✅ Step 1: Send OTP and temporarily store user details
    public String sendOtpToUser(UserDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return "Email already registered.";
        }

        
        String otp = otpService.generateRegistrationOtp(dto.getEmail());



        // 👉 Add this log for testing
    System.out.println("Generated OTP for " + dto.getEmail() + ": " + otp);

    
        otpStore.put(dto.getEmail(), otp);
        pendingUserStore.put(dto.getEmail(), dto);

        emailService.sendEmail(dto.getEmail(), "Your OTP Code", "Your OTP is: " + otp);

        return "OTP sent successfully.";
    }


   // ✅ Step 2: Verify OTP and save user permanently
    public String verifyOtpAndRegister(String email, String otp) {
        String storedOtp = otpStore.get(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            return "Invalid or expired OTP.";
        }

        UserDTO dto = pendingUserStore.get(email);
        if (dto == null) {
            return "User data not found.";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already registered.";
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());

        userRepository.save(user);

        otpStore.remove(email);
        pendingUserStore.remove(email);

        return "User registered successfully.";
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
