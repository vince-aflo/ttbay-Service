package io.turntabl.ttbay.utils.mappers;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.model.OfficeDay;
import io.turntabl.ttbay.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);
    default Profile profileDTOtoProfile(ProfileDTO profileDTO){
        Profile profile =  new Profile(
                profileDTO.profileId(),
                profileDTO.email(),
                profileDTO.fullName(),
                profileDTO.profileUrl(),
                profileDTO.office()
        );

        profile.setOfficeDays(
                profileDTO.officeDays()
                        .stream()
                        .map(weekday -> new OfficeDay(profile, weekday))
                        .toList()
        );

        return profile;
    }
}
