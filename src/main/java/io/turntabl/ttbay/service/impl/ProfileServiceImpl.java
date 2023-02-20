package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.exceptions.ProfileUpdateException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.OfficeDayRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.ProfileService;
import io.turntabl.ttbay.utils.mappers.ProfileMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;

    private final OfficeDayRepository officeDayRepository;

    @Override
    @Transactional
    public void updateProfile(ProfileDTO profileDTO) {
        try{
            User user = ProfileMapper.INSTANCE.profileDTOtoProfile(profileDTO);
            officeDayRepository.deleteByUser(user);
            userRepository.save(user);
        }catch (Exception exception) {
            throw new ProfileUpdateException("Unable to create/update the profile");
        }
    }

    @Override
    public Optional<User> getUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }

}
