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

    private GameSession newSession() {
        return new GameSession(book());
    }

    private GameSession sessionWithConsequence(Consequence consequence) {
        Section begin = new Section(1, "Start", SectionType.BEGIN,
                List.of(new Option("Path", 2, consequence)));
        Section end = new Section(2, "End", SectionType.END, List.of());
        Book book = new Book("Test Book", "Author", Difficulty.EASY, List.of(begin, end));
        return new GameSession(book);
    }

    @Test
    void newSession_startsAtBeginSection() {
        GameSession session = newSession();

        assertThat(session.getCurrentSection()).isEqualTo(BEGIN);
        assertThat(session.isOver()).isFalse();
    }

    @Test
    void choose_withValidGotoId_advancesToNextSection() {
        GameSession session = newSession();

        session.choose(2);

        assertThat(session.getCurrentSection()).isEqualTo(MIDDLE);
        assertThat(session.isOver()).isFalse();
    }

    @Test
    void choose_reachingEndSection_setsIsOverTrue() {
        GameSession session = newSession();

        session.choose(2);
        session.choose(3);

        assertThat(session.getCurrentSection()).isEqualTo(END);
        assertThat(session.isOver()).isTrue();
    }

    @Test
    void choose_withGotoIdNotAmongCurrentOptions_throwsException() {
        GameSession session = newSession();

        assertThatThrownBy(() -> session.choose(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("3");
    }

    @Test
    void newSession_startsWithInitialHealth() {
        GameSession session = newSession();

        assertThat(session.getHealth()).isEqualTo(10);
    }

    @Test
    void choose_withHealthLossConsequence_reducesHealth() {
        Consequence loseHealth = new Consequence(ConsequenceType.LOSE_HEALTH, 3, "Ouch.");
        GameSession session = sessionWithConsequence(loseHealth);

        session.choose(2);

        assertThat(session.getHealth()).isEqualTo(7);
    }

    @Test
    void choose_whenHealthReachesZero_marksSessionAsDeadAndOver() {
        Consequence fatalBlow = new Consequence(ConsequenceType.LOSE_HEALTH, 10, "You did not survive.");
        GameSession session = sessionWithConsequence(fatalBlow);

        session.choose(2);

        assertThat(session.getHealth()).isEqualTo(0);
        assertThat(session.isDead()).isTrue();
        assertThat(session.isOver()).isTrue();
    }

    @Test
    void choose_withHealthGainConsequence_capsAtMaxHealth() {
        Consequence bigHeal = new Consequence(ConsequenceType.GAIN_HEALTH, 15, "Fully restored.");
        GameSession session = sessionWithConsequence(bigHeal);

        session.choose(2);

        assertThat(session.getHealth()).isEqualTo(20);
    }

    @Test
    void resumeAt_startsAtGivenSectionWithGivenHealth() {
        GameSession session = GameSession.resumeAt(book(), 2, 6);

        assertThat(session.getCurrentSection()).isEqualTo(MIDDLE);
        assertThat(session.getHealth()).isEqualTo(6);
        assertThat(session.isOver()).isFalse();
    }
}