package com.barnes.flashcards.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.barnes.flashcards.model.Flashcard;

import java.util.List;

/**
 * Sets up CRUD operations for the flashcard table in the database.
 */
@Dao
public interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Flashcard flashcard);

    @Update
    void update(Flashcard flashcard);

    @Delete
    void delete(Flashcard flashcard);

    @Query("SELECT * FROM flashcard_table")
    List<Flashcard> getAllFlashcards();

    @Query("SELECT * FROM flashcard_table WHERE cardId = :cardId")
    Flashcard getFlashcardFromId(int cardId);

    @Query("SELECT * FROM flashcard_table WHERE deck_id = :deckId")
    List<Flashcard> getFlashcardsInDeck(int deckId);

    @Query("SELECT * FROM flashcard_table WHERE lower(front) LIKE :search OR lower(back) LIKE :search")
    List<Flashcard> searchFlashcards(String search);

    // Get a flashcard row from the table using OFFSET. Calling activity must make sure row is in the table using Deck size
    // Selects the (rowNumber + 1) row
    @Query("SELECT * FROM flashcard_table WHERE deck_id = :deckId LIMIT 1 OFFSET :rowNumber")
    Flashcard getRowCard(int rowNumber, int deckId);
}
