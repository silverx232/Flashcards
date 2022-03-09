package com.barnes.flashcards.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barnes.flashcards.databinding.DeckRowBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.R;

import java.util.List;

/**
 * Recycler Adapter for the Deck class.
 *
 * <p> Sets up the cards for the RecyclerView list of decks. Contains the public class
 * ViewHolder and the interface OnContactClickListener. </p>
 */
public class DeckRecyclerAdapter extends RecyclerView.Adapter<DeckRecyclerAdapter.ViewHolder> {
    private List<Deck> deckList;
    private OnContactClickListener onContactClickListener;

    /**
     * Constructor for DeckRecyclerAdapter.
     *
     * @param deckList The list of Decks that the RecyclerView will use
     * @param onContactClickListener A listener that specifies what will happen when the card is
     *                               clicked in the RecyclerView
     */
    public DeckRecyclerAdapter(List<Deck> deckList, OnContactClickListener onContactClickListener) {
        this.deckList = deckList;
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
                .inflate(R.layout.deck_row, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Displays the data at the given position.
     *
     * <p> This method displays the data from the specified position. It populates the ViewHolder's
     * card with  the information for the Deck located at the given position in the deckList. </p>
     * @param holder The ViewHolder that contains the views where the data will be displayed
     * @param position The position in the RecyclerView where the information will be displayed,
     *                 also the position in the deckList for which Deck to use.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deck deck = deckList.get(position);

        holder.binding.titleTextview.setText(deck.getTitle());
        holder.binding.sizeTextview.setText(Integer.toString(deck.getSize()));
    }

    /**
     * Gets the size of the list of Decks being displayed.
     *
     * @return Returns the size of the list of Decks being displayed
     */
    @Override
    public int getItemCount() {
        return deckList.size();
    }

    /**
     * Class that sets up the View for the Recycler Adapter to use.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final DeckRowBinding binding;

        /**
         * Constructor for the ViewHolder class.
         *
         * @param itemView The view that will hold the row in the Recycler Adapter
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DeckRowBinding.bind(itemView);
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
