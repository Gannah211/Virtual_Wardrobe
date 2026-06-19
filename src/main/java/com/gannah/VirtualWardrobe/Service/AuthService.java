package com.gannah.VirtualWardrobe.Service;

import com.gannah.VirtualWardrobe.DTO.Request.*;
import com.gannah.VirtualWardrobe.DTO.Response.AuthResponse;
import com.gannah.VirtualWardrobe.Model.PasswordResetOtp;
import com.gannah.VirtualWardrobe.Repository.PasswordResetOtpRepository;
import com.gannah.VirtualWardrobe.Repository.UserRepository;
import com.gannah.VirtualWardrobe.Security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.gannah.VirtualWardrobe.Model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse  register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), 24L * 60 * 60 * 1000);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
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

    @Transactional
    public void forgotPassword (ForgotPasswordRequest request) {
        User user =  userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found"));

        otpRepository.deleteByEmail(user.getEmail());

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        otpRepository.save(resetOtp);
        emailService.sendEmail(user.getEmail(), otp);
    }

    public void verifyOtp(VerifyOtpRequest request) {
        PasswordResetOtp resetOtp = otpRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Otp not found"));

        if(resetOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if(!resetOtp.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        resetOtp.setVerified(true);
        otpRepository.save(resetOtp);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetOtp resetOtp = otpRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Otp not found"));

        if(!resetOtp.isVerified()){
            throw new RuntimeException("OTP not verified");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpRepository.deleteByEmail(request.getEmail());
    }

}
