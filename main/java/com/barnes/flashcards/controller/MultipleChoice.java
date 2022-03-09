package com.barnes.flashcards.controller;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barnes.flashcards.R;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.FragmentMultipleChoiceBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Fragment for a multiple choice quiz for a flashcard.
 *
 * <p> This fragment presents a multiple choice quiz for the user. The back of the flashcard is the
 * question. The front of the flashcard is the answer. The fragment chooses random flashcards from
 * same deck to act as the other options for an answer. </p>
 */
public class MultipleChoice extends Fragment {

    /**
     * Tags to inform the user and ReviewActivity that flashcard was guessed correctly, incorrectly,
     * or there were insufficient cards in the deck to make a multiple choice quiz
     */
    private static final String CORRECT = "Correct";
    public static final String INCORRECT = "Incorrect";
    public static final String INSUFFICIENT_CARDS = "insufficient cards";

    // the fragment initialization parameter
    private static final String FLASHCARD_ID = "flashcard id";

    // The number of answers the user can choose from
    private static final int NUM_ANSWERS = 4;

    private FragmentMultipleChoiceBinding binding;
    private FlashcardsViewModel viewModel;
    private MultipleChoiceListener listener;
    private Flashcard currentCard;
    private Deck deck;
    private List<Flashcard> answerList = new ArrayList<>();

    /**
     * Default constructor.
     */
    public MultipleChoice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cardId ID of flashcard being used
     * @return A new instance of fragment MultipleChoice.
     */
    public static MultipleChoice newInstance(int cardId) {
        MultipleChoice fragment = new MultipleChoice();
        Bundle args = new Bundle();
        args.putInt(FLASHCARD_ID, cardId);

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Method that runs when the fragment is created.
     *
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Method that runs when the fragment view is created.
     *
     * @param inflater A LayoutInflater for the view
     * @param container The parent view the fragment's UI should be attached to
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     * @return The view for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMultipleChoiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Method that runs after the fragment view is created.
     *
     * <p> The views in the layout are initialized here. It checks the Intent for a flashcard ID and
     * loads that flashcard. It then calls methods to choose answers. </p>
     * @param view The view returned by onCreateView()
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FlashcardsViewModel.class);

        int cardId = requireArguments().getInt(FLASHCARD_ID);

        loadCardTask(cardId, savedInstanceState);

        binding.card1.setOnClickListener(v -> {
            checkAnswer(v);
        });

        binding.card2.setOnClickListener(v -> {
            checkAnswer(v);
        });

        binding.card3.setOnClickListener(v -> {
            checkAnswer(v);
        });

        binding.card4.setOnClickListener(v -> {
            checkAnswer(v);
        });

    }

    /**
     * Loads the information from a given flashcard.
     *
     * <p> This method uses the supplied flashcard ID to find the card in the database and load it
     * its deck. It also sets up an answer list of possible answers and binds the information to
     * the views. </p>
     * @param cardId The ID of the flashcard
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    private void loadCardTask(int cardId, Bundle savedInstanceState) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            currentCard = viewModel.getFlashcardFromId(cardId);
            deck = viewModel.getDeckFromId(currentCard.getDeckId());

            if (savedInstanceState != null) {
                answerList = viewModel.getAnswerList();
            } else {
                // Choose 3 random flashcards to provide answers
                chooseAnswers();
            }

            if (answerList.size() < (NUM_ANSWERS)) {
                listener.multipleChoiceInput(INSUFFICIENT_CARDS);
                return;
            }


            // Load information for the review flashcard
            if (isAdded()) {
                getActivity().runOnUiThread(() -> {
                    binding.enterVocab.setText(currentCard.getBack());

                    // Randomize the answers selection
                    Random random = new Random();
                    List<Flashcard> copyList = new ArrayList<>(answerList);

                    int randomNum = random.nextInt(copyList.size());
                    binding.enterAnswer1.setText(copyList.get(randomNum).getFront());
                    copyList.remove(randomNum);

                    randomNum = random.nextInt(copyList.size());
                    binding.enterAnswer2.setText(copyList.get(randomNum).getFront());
                    copyList.remove(randomNum);

                    randomNum = random.nextInt(copyList.size());
                    binding.enterAnswer3.setText(copyList.get(randomNum).getFront());
                    copyList.remove(randomNum);

                    binding.enterAnswer4.setText(copyList.get(0).getFront());
                });
            }
        });
    }

    /**
     * Sets up an answer list of flashcards for possible answers.
     *
     * <p> This method adds the selected flashcard plus 3 random flashcards for its deck to a list of
     * possible answers. It should be run in a background thread. </p>
     */
    private void chooseAnswers() {
        int deckSize = deck.getSize();
        Random random = new Random();

        // Make sure answerList is clear
        answerList.clear();
        answerList.add(currentCard);

        // Get (NUM_ANSWERS - 1) more possible answers
        for (int i = 0; i < (NUM_ANSWERS - 1); i++) {
            // Get a random number from 1 to deckSize, inclusive
            int randomNum = random.nextInt(deckSize) + 1;

            Flashcard flashcard = viewModel.getRowCard(randomNum, deck.getDeckId());

            int j = 0;
            while (flashcard.equals(currentCard) || answerList.contains(flashcard)) {
                randomNum++;
                if (randomNum > deckSize)
                    randomNum = randomNum % deckSize;

                flashcard = viewModel.getRowCard(randomNum, deck.getDeckId());

                // If j reaches deckSize, there are no more flashcards to add to reviewList
                j++;
                if (j >= deckSize)
                    return;
            }

            answerList.add(flashcard);
        }
    }

    /**
     * Checks the user selected answer against the correct answer.
     *
     * <p> This method checks if the answer the user clicked on matches the front of the flashcard
     * in currentCard. It sends a string back to the main activity indicating whether the guess was
     * correct or incorrect. </p>
     * @param view The view that was clicked on
     */
    private void checkAnswer(View view) {
        String answer = "";
        int viewId = view.getId();

        // Locate the textview located in the card.
        if (viewId == R.id.card1) {
            answer = binding.enterAnswer1.getText().toString();
        } else if (viewId == R.id.card2) {
            answer = binding.enterAnswer2.getText().toString();
        } else if (viewId == R.id.card3) {
            answer = binding.enterAnswer3.getText().toString();
        } else if (viewId == R.id.card4) {
            answer = binding.enterAnswer4.getText().toString();
        }

        // Compare answer to current flashcard's front
        if (answer.equals(currentCard.getFront())) {
            deck.guessedRight();
            deck.setTimeReviewed(LocalDateTime.now());
            viewModel.update(deck);

            Toast.makeText(getContext(), CORRECT, Toast.LENGTH_SHORT)
                    .show();

            listener.multipleChoiceInput(CORRECT);

        } else {
            deck.guessedWrong();
            deck.setTimeReviewed(LocalDateTime.now());
            viewModel.update(deck);

            listener.multipleChoiceInput(INCORRECT);
        }
    }

    /**
     * Called when a fragment is first attached to its context.
     *
     * <p> This method is called before onCreate(). It is used to check that the context has
     * implemented MultipleChoiceListener and assigns it. </p>
     * @param context The context of the activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MultipleChoiceListener) {
            listener = (MultipleChoiceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MultipleChoiceListener");
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     *
     * <p> This method is called after onDestroy(). It gets rid of the reference to
     * MultipleChoiceListener. </p>
     */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Called when the view has been detached from the fragment.
     *
     * <p> Called after onStop(). The next time the fragment needs to be displayed, a new view will
     * be created. It is used to get rid of the references to the binding and view model. </p>
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        viewModel = null;
    }

    /**
     * Saves the fragment's current dynamic state for later reconstruction.
     *
     * <p> This method is used to save the answer list to the view model for retrieval when the
     * fragment view is reconstructed. </p>
     * @param outState Bundle where the saved state is placed
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        viewModel.setAnswerList(answerList);
    }

    /**
     * Interface that specifies a listener that will pass the result of the user guess to the main
     * activity.
     */
    public interface MultipleChoiceListener {
        void multipleChoiceInput(String input);
    }
}