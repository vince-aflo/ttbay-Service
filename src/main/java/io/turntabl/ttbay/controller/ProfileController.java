package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping
    public void updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        profileService.updateProfile(profileDTO);
    }

    @GetMapping("/{email}")
    public User getUser(@PathVariable("email") String email){
       return profileService.getUser(email);
    }

    @GetMapping("/all")
    public List<User> getAllUsers(){
        return profileService.findAllUsers();
    }
}
