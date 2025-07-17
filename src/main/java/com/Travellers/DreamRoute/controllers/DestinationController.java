package com.Travellers.DreamRoute.controllers;


import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.services.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DestinationResponse> addDestination(@RequestBody @Valid DestinationRequest request, @RequestParam String username){
        return ResponseEntity.ok(destinationService.addDestination(request, username));
    }

}

