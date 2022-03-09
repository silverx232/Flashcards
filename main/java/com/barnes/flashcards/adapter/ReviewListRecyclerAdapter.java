package com.barnes.flashcards.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barnes.flashcards.R;
import com.barnes.flashcards.databinding.ReviewListRowBinding;
import com.barnes.flashcards.model.Flashcard;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Recycler Adapter for the ReviewDoneFragment class.
 *
 * <p> Sets up the cards for the RecyclerView list of flashcards reviewed. Contains the public class
 * ViewHolder. </p>
 */
public class ReviewListRecyclerAdapter extends RecyclerView.Adapter<ReviewListRecyclerAdapter.ViewHolder> {
    private List<Flashcard> flashcardList;
    private List<String> guessList;

    /**
     * Constructor for ReviewListRecyclerAdapter.
     *
     * @param reviewedMap A map of flashcards and whether they were guessed correctly or incorrectly
     */
    public ReviewListRecyclerAdapter(Map<Flashcard, String> reviewedMap) {
        flashcardList = Arrays.asList(reviewedMap.keySet().toArray(new Flashcard[0]));
        guessList = Arrays.asList(reviewedMap.values().toArray(new String[0]));
    }

    /**
     * Class that creates a ViewHolder.
     *
     * <p> This is called when the Adapter needs a new ViewHolder to represent an item. </p>
     * @param parent The group that the ViewHolder's view will be added to.
     * @param viewType An int specifying the type of ViewHolder to create, if there are multiple
     *                 ViewHolders. (override getItemViewType())
     * @return Returns a ViewHolder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_row, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Displays the data at the given position.
     *
     * <p> This method displays the data from the specified position. It populates the ViewHolder's
     * card with  the information for list of the reviewed flashcards and their guesses. </p>
     * @param holder The ViewHolder that contains the views where the data will be displayed
     * @param position The position in the RecyclerView where the information will be displayed,
     *                 also the position in the flashcardList.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flashcard flashcard = flashcardList.get(position);
        String guess = guessList.get(position);

        holder.binding.vocabFront2.setText(flashcard.getFront());
        holder.binding.vocabBack2.setText(flashcard.getBack());
        holder.binding.enterGuess.setText(guess);
    }

    /**
     * Gets the size of the list of flashcards being displayed.
     *
     * @return Returns the size of the list of flashcards being displayed
     */
    @Override
    public int getItemCount() {
        return flashcardList.size();
    }


    /**
     * Class that sets up the View for the Recycler Adapter to use.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final ReviewListRowBinding binding;

        /**
         * Constructor for the ViewHolder class.
         *
         * @param itemView The view that will hold the row in the Recycler Adapter
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ReviewListRowBinding.bind(itemView);
        }
    }
}
