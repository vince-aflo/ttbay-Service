package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.service.AccountSettingsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(AccountSettingsController.class)
@AutoConfigureMockMvc()
class AccountSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountSettingsService accountSettingsService;

    //check for valid email
    @Test
    void deleteUser_givenjwtAuthTokenAndValidUserEnteredUserEmail_shouldReturnStatus200() throws Exception {
        String validEmail = "aikins.dwamena@turntabl.io";

        ResultActions response = mockMvc.perform(delete("/api/v1/account/delete-user/" + validEmail)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

    }

    //check for invalid email  throwing the right http forbidden
    @Test
    void deleteUser_givenjwtAuthTokenAndInValidUserEnteredUserEmail_shouldReturnStatus403() throws Exception {
        String validEmail = "aikinsakenten@gmail.com";
        Mockito.when(accountSettingsService.deleteAccount(any(), any())).thenThrow(new MismatchedEmailException("You're unauthorized"));
        ResultActions response = mockMvc.perform(delete("/api/v1/account/delete-user/" + validEmail)
                .with(jwt())
        );

        response.andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    //check for resource not found throwing 404

    @Test
    void deleteUser_givenjwtAuthTokenAndValidUserEnteredUserEmail_shouldReturnStatus404() throws Exception {
        String validEmail = "aikins.dwamena@turntabl.io";
        Mockito.when(accountSettingsService.deleteAccount(any(), any())).thenThrow(new ResourceNotFoundException("User couldn't be found"));
        ResultActions response = mockMvc.perform(delete("/api/v1/account/delete-user/" + validEmail)
                .with(jwt())
        );

        response.andExpect(MockMvcResultMatchers.status().isNotFound());

    }

}



