package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.loading.BookLibrary;
import com.adventurebookapp.adventurebook.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceTest {

    private static final Section BEGIN =
            new Section(1, "Start", SectionType.BEGIN, List.of(new Option("Go", 2, null)));
    private static final Section END =
            new Section(2, "End", SectionType.END, List.of());

    private final BookLibrary mockLibrary = mock(BookLibrary.class);
    private final GameService gameService = new GameService(mockLibrary);

    private Book book() {
        return new Book("Test Book", "Author", Difficulty.EASY, List.of(BEGIN, END));
    }

    @Test
    void startGame_withKnownTitle_returnsSessionIdAndBeginSection() {
        when(mockLibrary.getAllBooks()).thenReturn(List.of(book()));

        GameStartResult result = gameService.startGame("Test Book");

        assertThat(result.sessionId()).isNotBlank();
        assertThat(result.section().text()).isEqualTo("Start");
        assertThat(result.section().isEnding()).isFalse();
    }

    @Test
    void startGame_withUnknownTitle_throwsException() {
        when(mockLibrary.getAllBooks()).thenReturn(List.of());

        assertThatThrownBy(() -> gameService.startGame("Nonexistent"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void makeChoice_withValidSession_advancesAndReturnsNewSection() {
        when(mockLibrary.getAllBooks()).thenReturn(List.of(book()));
        GameStartResult started = gameService.startGame("Test Book");

        SectionView result = gameService.makeChoice(started.sessionId(), 2);

        assertThat(result.text()).isEqualTo("End");
        assertThat(result.isEnding()).isTrue();
    }

    @Test
    void makeChoice_withUnknownSessionId_throwsException() {
        assertThatThrownBy(() -> gameService.makeChoice("nonexistent-id", 2))
                .isInstanceOf(NoSuchElementException.class);
    }
}