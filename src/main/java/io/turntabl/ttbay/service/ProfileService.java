package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.model.User;

import java.util.Optional;

public interface ProfileService {
    void updateProfile(ProfileDTO profileDTO);

     Optional<User> getUser(String email);

}
