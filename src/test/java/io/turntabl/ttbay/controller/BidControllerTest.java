package io.turntabl.ttbay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.exceptions.BidLessThanMaxBidException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.service.BidService;
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

@WebMvcTest(BidController.class)
@AutoConfigureMockMvc
class BidControllerTest {
    private final BidDTO bidDTO = new BidDTO(5000.0, 1L);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BidService bidService;

    @Test
    void makeBid_givenBidDTOAndJwtToken_shouldReturn200() throws Exception{
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bids")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidDTO)));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void makeBid_givenBidDTOAndJwtToken_shouldReturn400() throws Exception {
        when(bidService.makeBid(any(), any())).thenThrow(ResourceNotFoundException.class);
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bids")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidDTO)));
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void makeBid_givenBidDTOAndJwtToken_shouldReturn403() throws Exception {
        when(bidService.makeBid(any(), any())).thenThrow(BidLessThanMaxBidException.class);
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bids")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidDTO)));
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}