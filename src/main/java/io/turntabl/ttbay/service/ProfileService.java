package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.model.User;

import java.util.List;

public interface ProfileService {
    void updateProfile(ProfileDTO profileDTO);

    User getUser(String email);

    List<User> findAllUsers();
}
