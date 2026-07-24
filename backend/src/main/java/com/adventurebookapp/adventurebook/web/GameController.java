package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.game.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public GameStartResult startGame(@RequestBody StartGameRequest request) {
        return gameService.startGame(request.title());
    }

    @PostMapping("/{sessionId}/choices")
    public SectionView makeChoice(@PathVariable String sessionId, @RequestBody ChoiceRequest request) {
        return gameService.makeChoice(sessionId, request.gotoId());
    }

    @PostMapping("/{sessionId}/save")
    public void saveGame(@PathVariable String sessionId) {
        gameService.saveGame(sessionId);
    }

    @PostMapping("/resume")
    public GameStartResult resumeGame(@RequestBody StartGameRequest request) throws IOException {
        return gameService.resumeGame(request.title());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleConflict() {
    }
}