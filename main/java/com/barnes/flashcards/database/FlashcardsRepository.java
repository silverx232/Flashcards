package com.barnes.flashcards.database;

import android.app.Application;

import com.barnes.flashcards.dao.DeckDao;
import com.barnes.flashcards.dao.FlashcardDao;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;

import java.util.List;

/**
 * Class that acts as a repository between the database and the View Model.
 */
public class FlashcardsRepository {

    private DeckDao deckDao;
    private FlashcardDao flashcardDao;

    public FlashcardsRepository(Application application) {
        // Get an instance of the database
        FlashcardsRoomDatabase database = FlashcardsRoomDatabase.getDatabase(application);

        deckDao = database.deckDao();
        flashcardDao = database.flashcardDao();
    }

    public void insert(Deck deck) {
        FlashcardsRoomDatabase.databaseWriteExecutor.execute(() -> {
            deckDao.insert(deck);
        });
    }

    public void insert(Flashcard flashcard) {
        FlashcardsRoomDatabase.databaseWriteExecutor.execute(() -> {
            flashcardDao.insert(flashcard);
        });
    }

    public void update(Deck deck) {
        FlashcardsRoomDatabase.databaseWriteExecutor.execute(() -> {
            deckDao.update(deck);
        });
    }

    public void update(Flashcard flashcard) {
        FlashcardsRoomDatabase.databaseWriteExecutor.execute(() -> {
            flashcardDao.update(flashcard);
        });
    }

    public void delete(Deck deck) {
        FlashcardsRoomDatabase.databaseWriteExecutor.execute(() -> {
            deckDao.delete(deck);
        });
    }

    public void delete(Flashcard flashcard) {
        FlashcardsRoomDatabase.databaseWriteExecutor.execute(() -> {
            flashcardDao.delete(flashcard);
        });
    }

    public List<Deck> getAllDecks() {
        return deckDao.getAllDecks();
    }

    public Deck getDeckFromId(int deckId) {
        return deckDao.getDeckFromId(deckId);
    }

    public List<Flashcard> getAllFlashcards() {
        return flashcardDao.getAllFlashcards();
    }

    public Flashcard getFlashcardFromId(int cardId) {
        return flashcardDao.getFlashcardFromId(cardId);
    }

    public List<Flashcard> getFlashcardsInDeck(int deckId) {
        return flashcardDao.getFlashcardsInDeck(deckId);
    }

    public List<Flashcard> searchFlashcards(String search) {
        search = "%" + search.toLowerCase() + "%";
        return flashcardDao.searchFlashcards(search);
    }


    // Get a flashcard from the database located on row rowNumber
    public Flashcard getRowCard(int rowNumber, int deckId) {
        // OFFSET gets the rowNumber + 1 row, so rowNumber should be decremented
        rowNumber--;
        return flashcardDao.getRowCard(rowNumber, deckId);
    }
}
