package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.game.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaveController.class)
public class SaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    void checkSaveExists_whenSaveExists_returnsOkWithSavedGame() throws Exception {
        when(gameService.checkSave("Test Book")).thenReturn(Optional.of(new SavedGame("Test Book", 2, 6)));

        mockMvc.perform(get("/saves/Test Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sectionId").value(2))
                .andExpect(jsonPath("$.health").value(6));
    }

    @Test
    void checkSaveExists_whenNoSaveExists_returns404() throws Exception {
        when(gameService.checkSave("Test Book")).thenReturn(Optional.empty());

        mockMvc.perform(get("/saves/Test Book"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSave_returnsOk() throws Exception {
        doNothing().when(gameService).deleteSave("Test Book");

        mockMvc.perform(delete("/saves/Test Book"))
                .andExpect(status().isOk());

        verify(gameService).deleteSave("Test Book");
    }
}
