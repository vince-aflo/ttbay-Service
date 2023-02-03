package io.turntabl.ttbay.service.impl;

import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.exceptions.ProfileUpdateException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.OfficeDayRepository;
import io.turntabl.ttbay.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static io.turntabl.ttbay.enums.OfficeLocation.*;
import static io.turntabl.ttbay.enums.Weekday.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProfileServiceImplTest {

    @Autowired
    private ProfileServiceImpl profileService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OfficeDayRepository officeDayRepository;

    private ProfileDTO validProfileDTO;

    @BeforeEach
    void setup(){
        validProfileDTO = new ProfileDTO(
                "testingProfileID",
                "testing@testing.com",
                "Michael Jackson",
                "testingImage.com/image.png",
                SONNIDOM_HOUSE,
                List.of(MONDAY, TUESDAY)
        );
    }

    @Test
    void givenValidProfileDTO_whenUpdatingProfile_thenDeleteOfficeDays(){
        profileService.updateProfile(validProfileDTO);

        Mockito.verify(officeDayRepository, times(1))
                .deleteByUser(any(User.class));
    }


    @Test
    void givenValidProfileDTO_whenUpdatingProfile_thenSaveProfile(){
        profileService.updateProfile(validProfileDTO);

        Mockito.verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void whenErrorSavingProfile_thenThrowProfileCreationException(){
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        assertThrows(ProfileUpdateException.class, () -> profileService.updateProfile(validProfileDTO));
    }

    @Test
    void givenValidProfileDTO_whenUpdatingProfile_thenDeleteOfficeDaysBeforeSave(){
        profileService.updateProfile(validProfileDTO);

        InOrder inOrder = Mockito.inOrder(officeDayRepository, userRepository);

        inOrder.verify(officeDayRepository).deleteByUser(any(User.class));
        inOrder.verify(userRepository).save(any(User.class));
    }


}