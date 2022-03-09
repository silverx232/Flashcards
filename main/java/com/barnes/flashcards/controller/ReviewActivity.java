package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.barnes.flashcards.R;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivityReviewBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;
import com.barnes.flashcards.util.InformationDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for reviewing a list of random flashcards in a deck.
 *
 * <p> This class lets the user review 10 random flashcards in the specified deck. The back of the
 * flashcard is the question and the front is the answer. It keeps track of correct or incorrect
 * guesses and shows the user the correct answer to incorrect guesses. It displays a list of the
 * reviewed flashcards and the results at the end. </p>
 */
public class ReviewActivity extends AppCompatActivity implements WrongAnswerFragment.WrongAnswerListener,
        MultipleChoice.MultipleChoiceListener, ReviewDoneFragment.ReviewDoneListener {
    /**
     * Tag for passing an ID for the user selected deck through an intent.
     */
    public static final String DECK_ID = "deck id";

    private ActivityReviewBinding binding;
    private FlashcardsViewModel viewModel;

    private List<Flashcard> reviewList = new ArrayList<>();
    private Map<Flashcard, String> finishedMap = new LinkedHashMap<>();
    private Deck deck;
    private Flashcard currentCard;

    // The number of cards to study in a review session
    private static final int NUM_CARDS = 10;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It gets a deck ID from the Intent and
     * runs a task to load that deck. </p>
     * @param savedInstanceState contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FlashcardsViewModel.class);

        int deckId = 1;
        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            deckId = intentData.getInt(DECK_ID);
        }

        // Load deck and choose cards to review
        loadDeckTask(deckId, savedInstanceState);
    }

    /**
     * Loads the selected deck from the database.
     *
     * <p> This method gets the select deck from the database. It choose a list of random flashcards
     * to review if there is not a saved instance. It then starts the multiple choice fragment. It
     * runs on a background thread. </p>
     * @param deckId The ID of the deck to get flashcards from
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    private void loadDeckTask(int deckId, Bundle savedInstanceState) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            deck = viewModel.getDeckFromId(deckId);

            if (savedInstanceState != null) {
                reviewList = viewModel.getReviewList();
                finishedMap = viewModel.getReviewedMap();
            } else {
                // Choose 10 random flashcards to review
                chooseCards();
            }

            if (reviewList.size() == 0) {
                reviewDone();
                return;
            }

            currentCard = reviewList.get(0);

            runOnUiThread(() -> {
                // Set up the fragment container
                // If savedInstanceState is not null, then the fragment is already added and doesn't need to be added again
                if (savedInstanceState == null) {
                    MultipleChoice fragment = MultipleChoice.newInstance(currentCard.getCardId());

                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.review_fragment_container, fragment, null)
                            .commit();
                }
            });
        });
    }

    /**
     * Sets up a review list of NUM_CARDS number of random flashcards.
     *
     * <p> This method select NUM_CARDS random flashcards from the specified deck for the user to
     * review. It should be run in a background thread. </p>
     */
    private void chooseCards() {
        int deckSize = deck.getSize();
        Random random = new Random();

        for (int i = 0; i < NUM_CARDS; i++) {
            // Get a random number from 1 to deckSize, inclusive
            int randomNum = random.nextInt(deckSize) + 1;

            // Use random number to get a flashcard from the database
            Flashcard flashcard = viewModel.getRowCard(randomNum, deck.getDeckId());

            // If flashcard is archived (status) or already in reviewList,
            // then increment random number (or return to zero if at deck size)
            int j = 0;
            while (flashcard.getStatus() == Flashcard.Status.ARCHIVED ||
                    reviewList.contains(flashcard)) {
                randomNum++;
                if (randomNum > deckSize)
                    randomNum = randomNum % deckSize;

                flashcard = viewModel.getRowCard(randomNum, deck.getDeckId());

                // If j reaches deckSize, there are no more non-archived flashcards to add to reviewList
                j++;
                if (j >= deckSize)
                    return;
            }

            reviewList.add(flashcard);
        }
    }

    /**
     * Takes the user to a fragment indicating that the review is done.
     *
     * <p> This method passes the map of reviewed flashcards to the view model. It then calls the
     * fragment that will show the user a list of the reviewed flashcards. </p>
     */
    private void reviewDone() {
        viewModel.setReviewedMap(finishedMap);

        ReviewDoneFragment fragment = ReviewDoneFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.review_fragment_container, fragment, null)
                .commit();
    }

    /**
     * Listener from WrongAnswerFragment.WrongAnswerListener.
     *
     * <p> This method implements the interface from WrongAnswerFragment. It tells the activity
     * what to do when the continue button is clicked in the fragment. </p>
     */
    @Override
    public void wrongAnswerClick() {
        nextCard();
    }

    /**
     * Listener from MultipleChoice.MultipleChoiceListener.
     *
     * <p> This method implements the interface from MultipleChoice. It receives the outcome of the
     * multiple choice quiz and decides what to do based on that outcome. It stores the current
     * flashcard and its outcome in finishedMap if the map does not already contain the flashcard. </p>
     * @param input The result of MultipleChoice. Will contain MultipleChoice.CORRECT, INCORRECT,
     *              or INSUFFICIENT_CARDS.
     */
    @Override
    public void multipleChoiceInput(String input) {
        //Inform user insufficient flashcards for review, move to viewing all flashcards in deck
        if (input.equals(MultipleChoice.INSUFFICIENT_CARDS)) {
            Bundle bundle = new Bundle();
            bundle.putString(InformationDialog.MESSAGE, getString(R.string.four_flashcards_required));

            InformationDialog dialog = new InformationDialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "InformationDialog");

            Intent intent = new Intent(ReviewActivity.this, ViewFlashcardsActivity.class);
            intent.putExtra(ViewFlashcardsActivity.DECK_ID, deck.getDeckId());
            startActivity(intent);
            return;
        }

        // Only record first time reviewing the card (in one session)
        if (!finishedMap.containsKey(currentCard)) {
            finishedMap.put(currentCard, input);
        }

        if (input.equals(MultipleChoice.INCORRECT)) {
            // The user should continue to review the flashcard until they can answer correctly.
            reviewList.add(currentCard);

            // show what the correct answer is
            WrongAnswerFragment fragment = WrongAnswerFragment.newInstance(currentCard.getCardId());

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.review_fragment_container, fragment, null)
                    .commit();
        } else {
            nextCard();
        }
    }

    /**
     * Reviews the next flashcard in the review list, if available.
     */
    private void nextCard() {
        // Remove only the first occurrence
        reviewList.remove(currentCard);

        if (reviewList.size() == 0) {
            reviewDone();
            return;
        }

        currentCard = reviewList.get(0);

        MultipleChoice fragment = MultipleChoice.newInstance(currentCard.getCardId());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.review_fragment_container, fragment, null)
                .commit();
    }

    /**
     * Listener from ReviewDoneFragment.ReviewDoneListener.
     *
     * <p> This method implements the interface from ReviewDoneFragment. It tells the activity
     * what to do when the continue button is clicked in the fragment. It takes the user to
     * viewing the deck information. </p>
     */
    @Override
    public void reviewDoneClick() {
        Intent intent = new Intent(ReviewActivity.this, DeckInfoActivity.class);
        intent.putExtra(DeckInfoActivity.DECK_ID, deck.getDeckId());
        startActivity(intent);
    }

    /**
     * Saves the activity's current dynamic state for later reconstruction.
     *
     * <p> This method is used to save the review list and finished map to the view model for
     * retrieval when the activity is reconstructed. </p>
     * @param outState Bundle where the saved state is placed
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        viewModel.setReviewList(reviewList);
        viewModel.setReviewedMap(finishedMap);
    }
}