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

import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.FragmentWrongAnswerBinding;
import com.barnes.flashcards.model.Flashcard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragment that shows the correct answer to a flashcard quiz.
 *
 * <p> This fragment takes a given flashcard and displays the back (question) and front (answer) of
 * the flashcard. </p>
 */
public class WrongAnswerFragment extends Fragment {

    private FragmentWrongAnswerBinding binding;
    private FlashcardsViewModel viewModel;
    private WrongAnswerListener listener;

    // the fragment initialization parameter
    private static final String FLASHCARD_ID = "flashcard id";

    private Flashcard flashcard;

    /**
     * Default constructor.
     */
    public WrongAnswerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cardId Id of the flashcard being used
     * @return A new instance of fragment WrongAnswerFragment.
     */
    public static WrongAnswerFragment newInstance(int cardId) {
        WrongAnswerFragment fragment = new WrongAnswerFragment();

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
        binding = FragmentWrongAnswerBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    /**
     * Method that runs after the fragment view is created.
     *
     * <p> The views in the layout are initialized here. It checks the Intent for a flashcard ID and
     * loads that flashcard. </p>
     * @param view The view returned by onCreateView()
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getActivity().getApplication())
                .create(FlashcardsViewModel.class);

        int cardId = 1;
        if (getArguments() != null) {
            cardId = getArguments().getInt(FLASHCARD_ID);
        }

        loadCardTask(cardId);

        binding.continueButton.setOnClickListener(v -> {
            listener.wrongAnswerClick();
        });
    }

    /**
     * Loads the information from a given flashcard.
     *
     * <p> This method uses the supplied flashcard ID to find the card in the database and binds its
     * information to the layout's views. </p>
     * @param cardId The ID of the flashcard
     */
    private void loadCardTask(int cardId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            flashcard = viewModel.getFlashcardFromId(cardId);

            if (isAdded()) {
                getActivity().runOnUiThread(() -> {
                    binding.enterVocab2.setText(flashcard.getBack());
                    binding.enterAnswer.setText(flashcard.getFront());
                });
            }
        });
    }

    /**
     * Called when a fragment is first attached to its context.
     *
     * <p> This method is called before onCreate(). It is used to check that the context has
     * implemented WrongAnswerListener and assigns it. </p>
     * @param context The context of the activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WrongAnswerListener) {
            listener = (WrongAnswerListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement WrongAnswerListener");
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     *
     * <p> This method is called after onDestroy(). It gets rid of the reference to
     * WrongAnswerListener. </p>
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
     * Interface that specifies a listener that will inform the main activity the continue button
     * was clicked.
     */
    public interface WrongAnswerListener {
        void wrongAnswerClick();
    }
}