package com.adventurebookapp.adventurebook.game;

import com.adventurebookapp.adventurebook.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameSessionTest {

    private static final Section BEGIN =
            new Section(1, "Start", SectionType.BEGIN, List.of(new Option("Go", 2, null)));
    private static final Section MIDDLE =
            new Section(2, "Middle", SectionType.NODE, List.of(new Option("Finish", 3, null)));
    private static final Section END =
            new Section(3, "End", SectionType.END, List.of());

    private Book book() {
        return new Book("Test Book", "Author", Difficulty.EASY, List.of(MIDDLE, END, BEGIN));
    }

    @Test
    void newSession_startsAtBeginSection() {
        GameSession session = new GameSession(book());

        assertThat(session.getCurrentSection()).isEqualTo(BEGIN);
        assertThat(session.isOver()).isFalse();
    }

    @Test
    void choose_withValidGotoId_advancesToNextSection() {
        GameSession session = new GameSession(book());

        session.choose(2);

        assertThat(session.getCurrentSection()).isEqualTo(MIDDLE);
        assertThat(session.isOver()).isFalse();
    }

    @Test
    void choose_reachingEndSection_setsIsOverTrue() {
        GameSession session = new GameSession(book());

        session.choose(2);
        session.choose(3);

        assertThat(session.getCurrentSection()).isEqualTo(END);
        assertThat(session.isOver()).isTrue();
    }

    @Test
    void choose_withGotoIdNotAmongCurrentOptions_throwsException() {
        GameSession session = new GameSession(book());

        assertThatThrownBy(() -> session.choose(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("3");
    }
}