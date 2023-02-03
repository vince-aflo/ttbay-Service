package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping
    public void updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        profileService.updateProfile(profileDTO);
    }
}
