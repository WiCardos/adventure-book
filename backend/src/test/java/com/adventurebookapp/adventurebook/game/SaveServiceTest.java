package com.adventurebookapp.adventurebook.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SaveServiceTest {


    private SaveService saveService;

    @BeforeEach
    void setUp(@TempDir java.nio.file.Path tempDir) throws IOException {
        saveService = new SaveService(tempDir);
    }

    @Test
    void save_thenLoad_returnsTheSameSavedGame() throws IOException {
        SavedGame savedGame = new SavedGame("The Whispering Lighthouse", 10, 7);

        saveService.save(savedGame);
        Optional<SavedGame> result = saveService.load("The Whispering Lighthouse");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedGame);
    }

    @Test
    void load_withNoExistingSave_returnsEmpty() throws IOException {
        Optional<SavedGame> result = saveService.load("Never Saved Book");

        assertThat(result).isEmpty();
    }

    @Test
    void save_sanitizesTitleForFilename(@TempDir Path tempDir) throws IOException {
        SaveService saveService = new SaveService(tempDir);
        SavedGame savedGame = new SavedGame("The Whispering Lighthouse", 10, 7);

        saveService.save(savedGame);

        assertThat(tempDir.resolve("the_whispering_lighthouse.json")).exists();
    }

    @Test
    void delete_removesTheSaveFile() throws IOException {
        SavedGame savedGame = new SavedGame("The Whispering Lighthouse", 10, 7);
        saveService.save(savedGame);

        saveService.delete("The Whispering Lighthouse");

        assertThat(saveService.load("The Whispering Lighthouse")).isEmpty();
    }

    @Test
    void delete_whenNoSaveExists_doesNotThrow() throws IOException {
        saveService.delete("Never Saved Book");
    }
}