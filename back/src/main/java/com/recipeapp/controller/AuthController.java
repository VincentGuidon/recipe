package com.recipeapp.controller;

import com.recipeapp.dto.AuthRequest;
import com.recipeapp.dto.AuthResponse;
import com.recipeapp.dto.RegisterRequest;
import com.recipeapp.entity.User;
import com.recipeapp.security.JwtService;
import com.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.createUser(
            request.getEmail(),
            request.getPassword(),
            request.getUsername(),
            User.UserRole.USER
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.getUsername(), user.getRole().name()));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userService.getUserByEmail(request.getEmail());
        userService.updateLastLogin(user.getId());
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.getUsername(), user.getRole().name()));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody AuthRequest request) {
        userService.resetPassword(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}