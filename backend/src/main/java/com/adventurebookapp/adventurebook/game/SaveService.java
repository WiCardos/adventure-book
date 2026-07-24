package com.adventurebookapp.adventurebook.game;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SaveService {

    private final Path saveDirectory;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public SaveService(Path saveDirectory) throws IOException {
        this.saveDirectory = saveDirectory;
        Files.createDirectories(saveDirectory);
    }

    public void save(SavedGame savedGame) throws IOException {
        Path file = pathFor(savedGame.title());
        Files.writeString(file, jsonMapper.writeValueAsString(savedGame));
    }

    public Optional<SavedGame> load(String title) throws IOException {
        Path file = pathFor(title);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        String content = Files.readString(file);
        return Optional.of(jsonMapper.readValue(content, SavedGame.class));
    }

    public void delete(String title) {
        Path file = pathFor(title);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // deletion is best-effort; a leftover save file is not a fatal problem
        }
    }

    private Path pathFor(String title) {
        String filename = title.toLowerCase().replaceAll("\\s+", "_") + ".json";
        return saveDirectory.resolve(filename);
    }
}