package com.adventurebookapp.adventurebook.web;

import com.adventurebookapp.adventurebook.game.GameService;
import com.adventurebookapp.adventurebook.game.SavedGame;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/saves")
public class SaveController {

    private final GameService gameService;

    public SaveController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{title}")
    public SavedGame checkSave(@PathVariable String title) throws IOException {
        return gameService.checkSave(title)
                .orElseThrow(() -> new NoSuchElementException("No save found for: " + title));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public void handleNotFound() {
    }
}