package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse>getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(user);
    }
}