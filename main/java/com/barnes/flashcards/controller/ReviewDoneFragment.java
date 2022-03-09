package com.barnes.flashcards.controller;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barnes.flashcards.adapter.ReviewListRecyclerAdapter;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.FragmentReviewDoneBinding;
import com.barnes.flashcards.model.Flashcard;

import java.util.Map;

/**
 * Fragment that displays a list of reviewed flashcards.
 *
 * <p> This fragment displays a list of the flashcards that were just reviewed in ReviewActivity.
 * It also displays whether the user's guess was correct or incorrect. </p>
 */
public class ReviewDoneFragment extends Fragment {

    private FragmentReviewDoneBinding binding;
    private FlashcardsViewModel viewModel;
    private ReviewDoneListener listener;

    /**
     * Default constructor.
     */
    public ReviewDoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewDoneFragment.
     */
    public static ReviewDoneFragment newInstance() {
        ReviewDoneFragment fragment = new ReviewDoneFragment();
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
        binding = FragmentReviewDoneBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Method that runs after the fragment view is created.
     *
     * <p> The views in the layout are initialized here. It gets a list of the reviewed flashcards
     * from the view model and binds it to the recycler adapter. </p>
     * @param view The view returned by onCreateView()
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FlashcardsViewModel.class);

        binding.continueButton2.setOnClickListener(v -> {
            listener.reviewDoneClick();
        });

        Map<Flashcard, String> reviewedMap = viewModel.getReviewedMap();
        ReviewListRecyclerAdapter adapter = new ReviewListRecyclerAdapter(reviewedMap);

        binding.reviewListRecyclerView.setHasFixedSize(true);
        binding.reviewListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.reviewListRecyclerView.setAdapter(adapter);
    }

    /**
     * Called when a fragment is first attached to its context.
     *
     * <p> This method is called before onCreate(). It is used to check that the context has
     * implemented ReviewDoneListener and assigns it. </p>
     * @param context The context of the activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ReviewDoneListener) {
            listener = (ReviewDoneListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ReviewDoneListener.");
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     *
     * <p> This method is called after onDestroy(). It gets rid of the reference to
     * ReviewDoneListener. </p>
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
        viewModel = null;
        binding = null;
    }

    /**
     * Interface that specifies a listener that will inform the main activity the continue button
     * was clicked.
     */
    public interface ReviewDoneListener {
        void reviewDoneClick();
    }
}