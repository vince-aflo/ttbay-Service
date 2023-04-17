package io.turntabl.ttbay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.ttbay.dto.AuctionRequest;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.exceptions.ItemAlreadyOnAuctionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.service.AuctionService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(AuctionController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc()
class AuctionControllerTest{
    @Autowired
    ObjectMapper objectMapper;
    AuctionRequest auctionRequest = new AuctionRequest(1L, new Date(), new Date(), 52.8, AuctionStatus.LIVE);
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuctionService auctionService;

    @Test
    void testThat_givenAValidToken_createAuction_shouldReturnAStatus200() throws Exception{
        ResultActions response = mockMvc.perform(post("/api/v1/auctions/add")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auctionRequest)));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThat_givenAValidToken_createAuction_shouldReturnAStatus403() throws Exception{
        given(auctionService.createAuction(any(), any())).willAnswer(invocation -> {
            throw new ItemAlreadyOnAuctionException("This is item is already on auction");
        });
        ResultActions response = mockMvc.perform(post("/api/v1/auctions/add")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auctionRequest)));
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testThat_givenAValidToken_returnAllAuctionByUser_shouldReturnAStatus200() throws Exception{
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/all-by-user")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThat_givenNoToken_returnAllAuctionByUser_shouldReturnAStatus401() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/all-by-user")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testThat_givenAValidToken_returnAllAuctionByUser_shouldReturnAStatus404() throws Exception {
        when(auctionService.returnAllAuctionByUser(any())).thenThrow(new ResourceNotFoundException("empty auctions"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/all-by-user")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThat_givenAnIValidToken_returnAllAuctionByUser_shouldReturnAStatus404() throws Exception {
        when(auctionService.returnAllAuctionByUser(any())).thenThrow(new ResourceNotFoundException("User not found"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/all-by-user")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThat_givenAValidToken_returnOneAuctionOfUser_shouldReturnAStatus200() throws Exception{
        long auctionId = 1L;
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/" + auctionId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThat_accessingADifferentUsersAuction_returnOneAuctionOfUser_shouldReturnAStatus403() throws Exception{
        long auctionId = 1L;
        when(auctionService.returnOneAuctionOfUser(any())).thenThrow(new MismatchedEmailException("You don't have access to this resource"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/" + auctionId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testThat_accessingUserWithoutAnyAuctions_returnOneAuctionOfUser_shouldReturnAStatus404() throws Exception{
        long auctionId = 1L;
        when(auctionService.returnOneAuctionOfUser(any())).thenThrow(new ResourceNotFoundException("Item not found"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auctions/" + auctionId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}