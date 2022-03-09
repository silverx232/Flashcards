package com.barnes.flashcards.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * View Model for the database.
 *
 * <p> This class allows access between the GUI and the database. It manages data from the repository.
 * The AndroidViewModel survives screen rotation, and so is more efficient than just calling the
 * repository. </p>
 */
public class FlashcardsViewModel extends AndroidViewModel {

    private static FlashcardsRepository repository;

    //For passing information in ReviewActivity and fragments
    private List<Flashcard> reviewList = new ArrayList<>();
    private List<Flashcard> answerList = new ArrayList<>();
    private Map<Flashcard, String> reviewedMap = new LinkedHashMap<>();

    public FlashcardsViewModel(@NonNull Application application) {
        super(application);
        repository = new FlashcardsRepository(application);
    }

    public void insert(Deck deck) {
        repository.insert(deck);
    }

    public void insert(Flashcard flashcard) {
        repository.insert(flashcard);
    }

    public void update(Deck deck) {
        repository.update(deck);
    }

    public void update(Flashcard flashcard) {
        repository.update(flashcard);
    }

    public void delete(Deck deck) {
        repository.delete(deck);
    }

    public void delete(Flashcard flashcard) {
        repository.delete(flashcard);
    }

    public List<Deck> getAllDecks() {
        return repository.getAllDecks();
    }

    public Deck getDeckFromId(int deckId) {
        return repository.getDeckFromId(deckId);
    }

    public List<Flashcard> getAllFlashcards() {
        return repository.getAllFlashcards();
    }

    public Flashcard getFlashcardFromId(int cardId) {
        return repository.getFlashcardFromId(cardId);
    }

    public List<Flashcard> getFlashcardsInDeck(int deckId) {
        return repository.getFlashcardsInDeck(deckId);
    }

    public List<Flashcard> searchFlashcards(String search) {
        return repository.searchFlashcards(search);
    }

    public Flashcard getRowCard(int rowNumber, int deckId) {
        return repository.getRowCard(rowNumber, deckId);
    }

    public List<Flashcard> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Flashcard> reviewList) {
        this.reviewList = reviewList;
    }

    public List<Flashcard> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Flashcard> answerList) {
        this.answerList = answerList;
    }

    public Map<Flashcard, String> getReviewedMap() {
        return reviewedMap;
    }

    public void setReviewedMap(Map<Flashcard, String> reviewedMap) {
        this.reviewedMap = reviewedMap;
    }
}
