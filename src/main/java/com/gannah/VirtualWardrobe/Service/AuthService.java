package com.gannah.VirtualWardrobe.Service;

import com.gannah.VirtualWardrobe.DTO.Request.LoginRequest;
import com.gannah.VirtualWardrobe.DTO.Request.RegisterRequest;
import com.gannah.VirtualWardrobe.DTO.Response.AuthResponse;
import com.gannah.VirtualWardrobe.Repository.UserRepository;
import com.gannah.VirtualWardrobe.Security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.gannah.VirtualWardrobe.Model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return "Welcome to community Dear";
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        Long expiration = request.isRememberMe()
                ? 30L * 24 * 60 * 60 * 1000
                : 24L * 60 * 60 * 1000;

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user.getEmail(), expiration);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

}
