package com.gannah.VirtualWardrobe.Controller;

import com.gannah.VirtualWardrobe.DTO.Request.*;
import com.gannah.VirtualWardrobe.DTO.Response.AuthResponse;
import com.gannah.VirtualWardrobe.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest Request) {
        return ResponseEntity.ok(authService.register(Request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest Request) {
        return ResponseEntity.ok(authService.login(Request));
    }

    @PostMapping("/forgot-Password")
    public ResponseEntity<Void>  forgotPassword(@Valid @RequestBody ForgotPasswordRequest Request) {
        authService.forgotPassword(Request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("verify-OTP")
    public ResponseEntity<Void> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        authService.verifyOtp(request);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok().build();
    }
}
