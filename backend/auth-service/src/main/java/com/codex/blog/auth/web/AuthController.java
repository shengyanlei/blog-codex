package com.codex.blog.auth.web;

import com.codex.blog.auth.service.AuthService;
import com.codex.blog.auth.web.dto.LoginRequest;
import com.codex.blog.auth.web.dto.RegisterRequest;
import com.codex.blog.auth.web.dto.TokenResponse;
import com.codex.blog.common.dto.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        TokenResponse tokenResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody Map<String, String> body) {
        TokenResponse tokenResponse = authService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}
