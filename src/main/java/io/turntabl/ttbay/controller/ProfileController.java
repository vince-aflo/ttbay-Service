package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        profileService.updateProfile(profileDTO);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{email}")
    public ResponseEntity< Optional<User>> getUser(@PathVariable("email") String email){
        return ResponseEntity.ok(profileService.getUser(email));
    }


}