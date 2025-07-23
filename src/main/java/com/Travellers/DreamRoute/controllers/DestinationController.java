package com.Travellers.DreamRoute.controllers;


import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.security.UserDetail;
import com.Travellers.DreamRoute.services.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/destinations")
public class DestinationController {
    private final DestinationService destinationService;

    @GetMapping
    public ResponseEntity<List<DestinationResponse>> getAllDestinations() {
        List<DestinationResponse> destinations = destinationService.getAllDestinations();
        return ResponseEntity.ok(destinations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinationResponse> getDestinationById(@PathVariable Long id) {
        DestinationResponse destination  = destinationService.getDestinationById(id);
        return ResponseEntity.ok(destination);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DestinationResponse>> getDestinationByUserId(@PathVariable Long userId){
        List<DestinationResponse> destinations = destinationService.getDestinationsByUserId(userId);
        return ResponseEntity.ok(destinations);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DestinationResponse> addDestination(@RequestBody @Valid DestinationRequest request, @AuthenticationPrincipal UserDetail userDetail){
        DestinationResponse response = destinationService.addDestination(request, userDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinationResponse> updateDestination(
            @PathVariable Long id, @RequestBody @Valid DestinationRequest request) {
        DestinationResponse updatedDestination = destinationService.updateDestination(id, request);
        return ResponseEntity.ok(updatedDestination);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDestination(@PathVariable Long id) {
        String message = destinationService.deleteDestination(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}