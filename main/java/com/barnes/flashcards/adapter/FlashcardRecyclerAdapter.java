package com.barnes.flashcards.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barnes.flashcards.R;
import com.barnes.flashcards.databinding.FlashcardRowBinding;
import com.barnes.flashcards.model.Flashcard;

import java.util.List;

/**
 * Recycler Adapter for the Flashcard class.
 *
 * <p> Sets up the cards for the RecyclerView list of flashcards. Contains the public class
 * ViewHolder and the interface OnContactClickListener. </p>
 */
public class FlashcardRecyclerAdapter extends RecyclerView.Adapter<FlashcardRecyclerAdapter.ViewHolder> {
    private List<Flashcard> flashcardList;
    private OnContactClickListener onContactClickListener;
    private boolean isDelete = false;

    /**
     * Constructor for FlashcardRecyclerAdapter.
     *
     * @param flashcardList The list of Flashcards that the RecyclerView will use
     * @param onContactClickListener A listener that specifies what will happen when the card is
     *                               clicked in the RecyclerView
     */
    public FlashcardRecyclerAdapter(List<Flashcard> flashcardList, OnContactClickListener onContactClickListener) {
        this.flashcardList = flashcardList;
        this.onContactClickListener = onContactClickListener;
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
                .inflate(R.layout.flashcard_row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Displays the data at the given position.
     *
     * <p> This method displays the data from the specified position. It populates the ViewHolder's
     * card with  the information for the Flashcard located at the given position in the
     * flashcardList. </p>
     * @param holder The ViewHolder that contains the views where the data will be displayed
     * @param position The position in the RecyclerView where the information will be displayed,
     *                 also the position in the flashcardList for which Flashcard to use.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isDelete) {
            holder.binding.cardDeleteCheckbox.setVisibility(View.VISIBLE);
        } else {
            holder.binding.cardDeleteCheckbox.setChecked(false);
            holder.binding.cardDeleteCheckbox.setVisibility(View.GONE);
        }

        Flashcard flashcard = flashcardList.get(position);
        holder.binding.vocabFront.setText(flashcard.getFront());
        holder.binding.vocabBack.setText(flashcard.getBack());
    }

    /**
     * Gets the size of the list of Decks being displayed.
     *
     * @return Returns the size of the list of Decks being displayed
     */
    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    /**
     * Sets the isDelete property.
     *
     * <p> This method tells the onBindViewHolder() method whether to display the delete Checkboxes.
     * This will allow the flashcards to be selected for deletion. </p>
     * @param isDelete True if the flashcards are being selected for deletion
     */
    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * Deletes a flashcard from the adapter's flashcard list.
     *
     * @param flashcard The flashcard to be deleted
     */
    public void delete(Flashcard flashcard) {
        flashcardList.remove(flashcard);
    }

    /**
     * Returns the list of items the adapter is currently using.
     *
     * @return Returns the list of items the adapter is using
     */
    public List<Flashcard> getItems() {
        return flashcardList;
    }

    /**
     * Class that sets up the View for the Recycler Adapter to use.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final FlashcardRowBinding binding;

        /**
         * Constructor for the ViewHolder class.
         *
         * @param itemView The view that will hold the row in the Recycler Adapter
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FlashcardRowBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Method that passes the onClick to the onContactClickListener.
         *
         * @param view The view (row) that was clicked on
         */
        @Override
        public void onClick(View view) {
            onContactClickListener.onContactClick(getAdapterPosition(), view);
        }
    }

    /**
     * Interface that specifies a listener that will tell the View in ViewHolder what to do when clicked.
     */
    public interface OnContactClickListener {
        void onContactClick(int position, View view);
    }
}
