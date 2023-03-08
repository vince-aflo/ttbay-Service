package io.turntabl.ttbay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ModelCreateException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.service.ItemService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    ItemRequest itemRequest = new ItemRequest("iPhone Xs", "slightly used 64g", ItemCondition.USED, Category.ELECTRONICS, List.of());
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @Test
    void returnAllUserAuctionItems_givenJwtAuthToken_shouldReturn200() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/on-auction")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));


        response.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void returnAllUserAuctionItems_givenJwtToken_shouldReturn404() throws Exception {
        when(itemService.returnAllAuctionItemsByUser(any())).thenThrow(new ResourceNotFoundException(""));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/on-auction")
                .with(jwt())
        );

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThat_givenJwtAuthToken_createItem_shouldReturn200() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/on-auction")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)));


        response.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void testThat_givenJwtAuthToken_createItem_shouldReturn404() throws Exception {
        when(itemService.addItem(any(), any())).thenThrow(new ResourceNotFoundException("User not found"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/items/add")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void testThat_givenJwtAuthToken_createItem_shouldReturn500() throws Exception {
        when(itemService.addItem(any(), any())).thenThrow(new ModelCreateException("Error creating item"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/items/add")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }

    @Test
    void testThat_givenJwtAuthToken_getOneIemOfUser_shouldReturn200() throws Exception {
        long itemId = 1L;
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/" + itemId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void testThat_givenJwtAuthToken_getOneIemOfUser_shouldReturn403() throws Exception {
        when(itemService.returnOneItemOfUser(any(), any())).thenThrow(new MismatchedEmailException("You don't have access to this resource"));
        long itemId = 1L;
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/" + itemId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    void testThat_givenJwtAuthToken_getOneIemOfUser_shouldReturn404() throws Exception {
        when(itemService.returnOneItemOfUser(any(), any())).thenThrow(new ResourceNotFoundException("Item not found"));
        long itemId = 1L;
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/" + itemId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void testThat_givenJwtAuthToken_returnAllItemsByUser_shouldReturn200() throws Exception {
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/all-by-user")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void testThat_givenJwtAuthToken_returnAllItemsByUser_shouldReturn404() throws Exception {
        when(itemService.returnAllItemsByUser(any())).thenThrow(new ResourceNotFoundException(""));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items/all-by-user")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());

    }


}

