package com.adventurebookapp.adventurebook.loading;

import com.adventurebookapp.adventurebook.model.Book;
import com.adventurebookapp.adventurebook.model.Consequence;
import com.adventurebookapp.adventurebook.model.ConsequenceType;
import com.adventurebookapp.adventurebook.model.Option;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class BookLoaderTest {

    private final BookLoader loader = new BookLoader();

    private Book loadTestBook() {
        InputStream jsonStream = getClass().getResourceAsStream("/test-book.json");
        return loader.load(jsonStream);
    }

    @Test
    void loadsBookFromJson_mapsTitleCorrectly() throws Exception {
        Book book = loadTestBook();

        assertThat(book.title()).isEqualTo("Test Book");
    }


    @Test
    void loadsBookFromJson_mapsIdCorrectly() throws Exception {
        Book book = loadTestBook();

        assertThat(book.sections().get(0).id()).isEqualTo(500);
    }


    @Test
    void loadsBookFromJson_mapsConsequenceCorrectly() throws Exception {
        Book book = loadTestBook();

        Consequence consequence = book.sections().get(0).options().get(0).consequence();

        assertThat(consequence).isNotNull();
        assertThat(consequence.type()).isEqualTo(ConsequenceType.LOSE_HEALTH);
        assertThat(consequence.value()).isEqualTo(3);
        assertThat(consequence.text()).isEqualTo("The pirate lunges at you with a hidden dagger before the guards subdue him.");
    }


    @Test
    void loadsBookFromJson_mapsMissingConsequenceAsNull() {
        Book book = loadTestBook();

        Option optionWithoutConsequence = book.sections().get(0).options().get(1);

        assertThat(optionWithoutConsequence.consequence()).isNull();
    }
}