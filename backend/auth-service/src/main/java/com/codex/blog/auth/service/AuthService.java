package com.codex.blog.auth.service;

import com.codex.blog.auth.domain.User;
import com.codex.blog.auth.repository.UserRepository;
import com.codex.blog.auth.web.dto.LoginRequest;
import com.codex.blog.auth.web.dto.RegisterRequest;
import com.codex.blog.auth.web.dto.TokenResponse;
import com.codex.blog.common.exception.BusinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final StringRedisTemplate redisTemplate;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService,
                       StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(AuthErrors.EMAIL_EXISTS);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new BusinessException(AuthErrors.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(AuthErrors.INVALID_CREDENTIALS);
        }
        user.setLastLoginAt(Instant.now());
        return issueTokens(user);
    }

    public TokenResponse refresh(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(refreshKey(refreshToken));
        if (userId == null) {
            throw new BusinessException(AuthErrors.INVALID_REFRESH_TOKEN);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrors.USER_NOT_FOUND));
        return issueTokens(user);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtTokenService.generateToken(user.getId(), user.getEmail());
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(refreshKey(refreshToken), user.getId(), Duration.ofDays(7));
        return new TokenResponse(accessToken, jwtTokenService.accessTokenTtlSeconds(), refreshToken, false);
    }

    private String refreshKey(String refreshToken) {
        return "auth:refresh:" + refreshToken;
    }
}
