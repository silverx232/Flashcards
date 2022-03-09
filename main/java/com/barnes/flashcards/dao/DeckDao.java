package com.barnes.flashcards.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.barnes.flashcards.model.Deck;

import java.util.List;

/**
 * Sets up CRUD operations for the deck table in the database.
 */
@Dao
public interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Deck deck);

    @Update
    void update(Deck deck);

    @Delete
    void delete(Deck deck);

    @Query("SELECT * FROM deck_table")
    List<Deck> getAllDecks();

    @Query("SELECT * FROM deck_table WHERE deck_id = :deckId")
    Deck getDeckFromId(int deckId);
}
