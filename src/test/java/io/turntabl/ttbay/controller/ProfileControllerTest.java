package io.turntabl.ttbay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.ttbay.dto.ProfileDTO;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static io.turntabl.ttbay.enums.OfficeLocation.SONNIDOM_HOUSE;
import static io.turntabl.ttbay.enums.Weekday.MONDAY;
import static io.turntabl.ttbay.enums.Weekday.TUESDAY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProfileService profileService;
    private ProfileDTO validProfileDTO;
    private ProfileDTO invalidProfileDTO;
    private User validUser;

    @BeforeEach
    void setup(){
        validProfileDTO = new ProfileDTO(
                "Mick",
                "testing@testing.com",
                "Michael Jackson",
                "testingImage.com/image.png",
                SONNIDOM_HOUSE,
                List.of(MONDAY, TUESDAY)
        );
        invalidProfileDTO = new ProfileDTO(
                "Mick",
                "testing.com",
                "Michael Jackson",
                "testingImage.com/image.png",
                SONNIDOM_HOUSE,
                List.of(MONDAY, TUESDAY)
        );
        validUser = User.builder().email("testing@testing.com").username("Mick").fullName("Michael Jackson").profileUrl("testingImage.com/image.png").role(Role.USER).officeLocation(SONNIDOM_HOUSE).build();
    }

    @Test
    void whenGettingUserWithEmail_givenValidExistingEmail_shouldReturnStatus200() throws Exception{
        String validEmail = "testing@testing.com";
        ResultActions response = mockMvc.perform(get("/api/v1/profile/"+validEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenUpdatingProfile_givenValidProfileDTO_shouldReturnStatus200() throws Exception{
        ResultActions response = mockMvc.perform(put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProfileDTO)));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenUpdatingProfile_givenInvalidProfileDTO_shouldReturnStatus400() throws Exception{
        ResultActions response = mockMvc.perform(put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProfileDTO)));
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}