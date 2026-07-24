package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.game.*;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void startGame_returnsSessionIdAndSection() throws Exception {
        SectionView section = new SectionView("Start", List.of(new OptionView("Go", 2)), false, 10, false);
        when(gameService.startGame("Test Book")).thenReturn(new GameStartResult("abc-123", section));

        mockMvc.perform(post("/games")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new StartGameRequest("Test Book"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("abc-123"))
                .andExpect(jsonPath("$.section.text").value("Start"))
                .andExpect(jsonPath("$.section.isEnding").value(false));
    }

    @Test
    void startGame_withUnknownTitle_returns404() throws Exception {
        when(gameService.startGame("Nonexistent")).thenThrow(new NoSuchElementException("not found"));

        mockMvc.perform(post("/games")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new StartGameRequest("Nonexistent"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void makeChoice_returnsNewSection() throws Exception {
        SectionView section = new SectionView("End", List.of(), true, 10, false);
        when(gameService.makeChoice("abc-123", 2)).thenReturn(section);

        mockMvc.perform(post("/games/abc-123/choices")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new ChoiceRequest(2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("End"))
                .andExpect(jsonPath("$.isEnding").value(true));
    }

    @Test
    void makeChoice_withUnknownSession_returns404() throws Exception {
        when(gameService.makeChoice("bad-id", 2)).thenThrow(new NoSuchElementException("not found"));

        mockMvc.perform(post("/games/bad-id/choices")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new ChoiceRequest(2))))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveGame_withValidSession_returnsOk() throws Exception {
        doNothing().when(gameService).saveGame("abc-123");

        mockMvc.perform(post("/games/abc-123/save"))
                .andExpect(status().isOk());

        verify(gameService).saveGame("abc-123");
    }

    @Test
    void saveGame_atInvalidState_returns409() throws Exception {
        doThrow(new IllegalStateException("Cannot save here")).when(gameService).saveGame("abc-123");

        mockMvc.perform(post("/games/abc-123/save"))
                .andExpect(status().isConflict());
    }

    @Test
    void resumeGame_withExistingSave_returnsSessionAndSection() throws Exception {
        SectionView section = new SectionView("Middle", List.of(new OptionView("Go", 3)), false, 6, false);
        when(gameService.resumeGame("Test Book")).thenReturn(new GameStartResult("abc-123", section));

        mockMvc.perform(post("/games/resume")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new StartGameRequest("Test Book"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("abc-123"))
                .andExpect(jsonPath("$.section.health").value(6));
    }

    @Test
    void resumeGame_withNoExistingSave_returns404() throws Exception {
        when(gameService.resumeGame("Test Book")).thenThrow(new NoSuchElementException("No save"));

        mockMvc.perform(post("/games/resume")
                        .contentType("application/json")
                        .content(jsonMapper.writeValueAsString(new StartGameRequest("Test Book"))))
                .andExpect(status().isNotFound());
    }
}