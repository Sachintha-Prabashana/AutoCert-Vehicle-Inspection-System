package lk.ijse.autocert.service;

import lk.ijse.autocert.dto.AuthDTO;
import lk.ijse.autocert.dto.AuthResponseDTO;
import lk.ijse.autocert.dto.RegisterDTO;
import lk.ijse.autocert.entity.User;
import lk.ijse.autocert.repository.UserRepository;
import lk.ijse.autocert.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Method to register a new user
    public String register(RegisterDTO registerDTO) {
        if(userRepository.findByEmail(registerDTO.getEmail()).isPresent()){
            throw new RuntimeException("User with this email already exists");

        }
        User user = User.builder()
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword())) // Encrypt the password
                .phone(registerDTO.getPhone())
                .build();
        userRepository.save(user);
        return "User registered successfully";
    }

    // Method to login a user and return a JWT token
    public String authenticate(AuthDTO authDTO) {
        User user = userRepository.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + authDTO.getEmail()));

        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return token;

    }


}
