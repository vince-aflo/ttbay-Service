package io.turntabl.ttbay.service.impl;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.exceptions.ProfileCreationException;
import io.turntabl.ttbay.model.Profile;
import io.turntabl.ttbay.repository.OfficeDayRepository;
import io.turntabl.ttbay.repository.ProfileRepository;
import io.turntabl.ttbay.service.ProfileService;
import io.turntabl.ttbay.utils.mappers.ProfileMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    private final OfficeDayRepository officeDayRepository;

    @Transactional
    @Override
    public void updateProfile(ProfileDTO profileDTO) {
        try{
            Profile profile = ProfileMapper.INSTANCE.profileDTOtoProfile(profileDTO);
            officeDayRepository.deleteByProfileId(profile);
            profileRepository.save(profile);
        }catch (Exception exception) {
            System.err.println(exception.getMessage());
            throw new ProfileCreationException("Unable to create/update the profile");
        }
    }
}
