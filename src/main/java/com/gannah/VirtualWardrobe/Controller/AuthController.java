package com.gannah.VirtualWardrobe.Controller;

import com.gannah.VirtualWardrobe.DTO.Request.LoginRequest;
import com.gannah.VirtualWardrobe.DTO.Request.RegisterRequest;
import com.gannah.VirtualWardrobe.DTO.Response.AuthResponse;
import com.gannah.VirtualWardrobe.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest Request) {
        return ResponseEntity.ok(authService.register(Request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest Request) {
        return ResponseEntity.ok(authService.login(Request));
    }
}
