package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private static final Section BEGIN =
            new Section(1, "Start", SectionType.BEGIN, List.of(
                new Option("Go", 2, null),
                new Option("Keep going", 3, null)));
    private static final Section MIDDLE =
            new Section(2, "Middle", SectionType.NODE, List.of(
                    new Option("Keep going", 3, null)));
    private static final Section END =
            new Section(3, "End", SectionType.END, List.of());

    private final BookLibrary mockLibrary = mock(BookLibrary.class);
    private final SaveService mockSaveService = mock(SaveService.class);
    private final GameService gameService = new GameService(mockLibrary, mockSaveService);

    private Book book() {
        return new Book("Test Book", "Author", Difficulty.EASY, List.of(BEGIN, MIDDLE, END));
    }

    private void bookIsAvailable() {
        when(mockLibrary.getAllBooks()).thenReturn(List.of(book()));
    }

    @Test
    void startGame_withKnownTitle_returnsSessionIdAndBeginSection() {
        bookIsAvailable();
        GameStartResult result = gameService.startGame("Test Book");

        assertThat(result.sessionId()).isNotBlank();
        assertThat(result.section().text()).isEqualTo("Start");
        assertThat(result.section().isEnding()).isFalse();
    }

    @Test
    void startGame_withUnknownTitle_throwsException() {
        bookIsAvailable();

        assertThatThrownBy(() -> gameService.startGame("Nonexistent"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void makeChoice_withValidSession_advancesAndReturnsNewSection() {
        bookIsAvailable();
        GameStartResult started = gameService.startGame("Test Book");

        SectionView result = gameService.makeChoice(started.sessionId(), 3);

        assertThat(result.text()).isEqualTo("End");
        assertThat(result.isEnding()).isTrue();
    }

    @Test
    void makeChoice_withUnknownSessionId_throwsException() {
        assertThatThrownBy(() -> gameService.makeChoice("nonexistent-id", 2))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveGame_withValidSession_savesProgressAndReturnsNothingElse() throws IOException {
        bookIsAvailable();
        GameStartResult started = gameService.startGame("Test Book");
        gameService.makeChoice(started.sessionId(), 2); // move off BEGIN so save is valid

        gameService.saveGame(started.sessionId());

        verify(mockSaveService).save(new SavedGame("Test Book", 2, 10));
    }

    @Test
    void saveGame_whileStillAtBeginSection_throwsException() throws IOException {
        bookIsAvailable();
        GameStartResult started = gameService.startGame("Test Book");

        assertThatThrownBy(() -> gameService.saveGame(started.sessionId()))
                .isInstanceOf(IllegalStateException.class);

        verify(mockSaveService, never()).save(any());
    }

    @Test
    void resumeGame_withExistingSave_returnsSectionAtSavedState() throws Exception {
        bookIsAvailable();
        when(mockSaveService.load("Test Book")).thenReturn(Optional.of(new SavedGame("Test Book", 2, 6)));

        GameStartResult result = gameService.resumeGame("Test Book");

        assertThat(result.section().text()).isEqualTo("Middle");
        assertThat(result.section().health()).isEqualTo(6);
    }

    @Test
    void makeChoice_reachingEndSection_deletesSave() {
        bookIsAvailable();
        GameStartResult started = gameService.startGame("Test Book");

        gameService.makeChoice(started.sessionId(), 2);
        gameService.makeChoice(started.sessionId(), 3);

        verify(mockSaveService).delete("Test Book");
    }

    @Test
    void deleteSave_delegatesToSaveService() {
        gameService.deleteSave("Test Book");

        verify(mockSaveService).delete("Test Book");
    }
}