package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.game.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

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

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
    }
}