package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.user.JwtResponse;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.security.UserDetail;
import com.Travellers.DreamRoute.security.jwt.JwtService;
import com.Travellers.DreamRoute.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody UserRequest userRequest) {
        Authentication auth = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password())
        );
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
        String token = jwtService.generateToken(userDetail);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
