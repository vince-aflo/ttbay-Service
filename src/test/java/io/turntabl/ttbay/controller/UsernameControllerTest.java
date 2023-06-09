package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.exceptions.UsernameAlreadyExistException;
import io.turntabl.ttbay.service.UsernameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(UsernameController.class)
@AutoConfigureMockMvc()
class UsernameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UsernameService usernameService;

    @Test
    void updateUsername_givenAvailableName_shouldReturnAStatus200() throws Exception{
        String available_username = "available";
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/profile/username/" + available_username)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateUsername_givenUnavailableName_shouldReturnAStatus409() throws Exception{
        String unavailable_username = "unavailable";
        when(usernameService.updateUsername(any(), any())).thenThrow(new UsernameAlreadyExistException("unavailable"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/profile/username/" + unavailable_username)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isConflict());
    }
}


