package io.turntabl.ttbay.utils.mappers;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.OfficeDay;
import io.turntabl.ttbay.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);
    default User profileDTOtoProfile(ProfileDTO profileDTO){
        User user =  new User(
                profileDTO.username(),
                profileDTO.email(),
                profileDTO.fullName(),
                profileDTO.profileUrl(),
                profileDTO.officeLocation()
        );

        user.setRole(Role.valueOf("USER")); //Todo set role dynamically

        user.setOfficeDays(
                profileDTO.officeDays()
                        .stream()
                        .map(weekday -> new OfficeDay(user, weekday))
                        .toList()
        );

        return user;
    }
}
