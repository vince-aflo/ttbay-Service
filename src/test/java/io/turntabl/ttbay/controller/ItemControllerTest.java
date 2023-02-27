package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.service.ItemService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;


    @Test
    void returnAllUserAuctionItems_givenJwtAuthToken_shouldReturn200() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auction/items/on-auction")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));


        response.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test void returnAllUserAuctionItems_givenJwtToken_shouldReturn404() throws Exception {
        Mockito.when(itemService.returnAllAuctionItemsByUser(any())).thenThrow(new ResourceNotFoundException(""));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auction/items/on-auction")
                .with(jwt())
        );

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}