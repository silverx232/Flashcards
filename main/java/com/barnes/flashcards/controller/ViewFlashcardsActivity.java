package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.barnes.flashcards.adapter.FlashcardRecyclerAdapter;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivityViewFlashcardsBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;
import com.barnes.flashcards.R;
import com.barnes.flashcards.util.DeleteDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for viewing all flashcards in a deck.
 *
 * <p> This class lets the user view information about all the flashcards in a user selected deck. </p>
 */
public class ViewFlashcardsActivity extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {
    /**
     * Tag for passing an ID for the user selected deck through an intent.
     */
    public static final String DECK_ID = "deck id";

    private ActivityViewFlashcardsBinding binding;
    private FlashcardsViewModel viewModel;
    private FlashcardRecyclerAdapter adapter;
    private boolean isDelete = false;
    private final List<Flashcard> deleteList = new ArrayList<>();
    private Deck deck;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It gets a list of all flashcards in a
     * deck and loads the Recycler Adapter with the list. Clicking on a flashcard will bring the
     * user to a detailed view of the flashcard. </p>
     * @param savedInstanceState contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewFlashcardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(
                ViewFlashcardsActivity.this.getApplication())
                .create(FlashcardsViewModel.class);

        int deckId = 1;
        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            deckId = intentData.getInt(DECK_ID);
        }

        // Set up the delete fab
        binding.deleteFab.setVisibility(View.GONE);
        binding.deleteFab.setOnClickListener(view -> {
            showDeleteDialog();
        });

        // Set up the recycler view
        binding.flashcardRecyclerview.setHasFixedSize(true);
        binding.flashcardRecyclerview.setLayoutManager(new LinearLayoutManager(ViewFlashcardsActivity.this));
        loadFlashcardsTask(deckId);
    }

    /**
     * Sets up the recycler view.
     *
     * <p> This method gets the list of all flashcards in a deck. It then sets up the recycler
     * view using that list. Clicking on a view will select it for deletion or take the user to
     * that flashcard's information. It runs on a background thread. </p>
     */
    private void loadFlashcardsTask(int deckId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Flashcard> flashcardList = viewModel.getFlashcardsInDeck(deckId);
            deck = viewModel.getDeckFromId(deckId);

            adapter = new FlashcardRecyclerAdapter(flashcardList,
                    (position, view) -> {  // OnContactClickListener
                Flashcard flashcard = adapter.getItems().get(position);

                if (isDelete) {
                    selectDeleteCards(flashcard, view);
                } else {
                    Intent intent = new Intent(ViewFlashcardsActivity.this, FlashcardInfoActivity.class);
                    intent.putExtra(FlashcardInfoActivity.FLASHCARD_ID, flashcard.getCardId());
                    startActivity(intent);
                }
            });

            runOnUiThread(() -> {
                binding.flashcardRecyclerview.setAdapter(adapter);
            });
        });
    }

    /**
     * Checks or unchecks a flashcard as being selected for deletion.
     *
     * <p> This method sets the view's delete checkbox to the opposite of what it currently is.
     * It then uses that boolean value to either add or remove the view's course instructor from a
     * list of instructors to be deleted. </p>
     * @param flashcard The flashcard whose information is located in the view
     * @param view The ViewHolder view from the recycler adapter
     */
    private void selectDeleteCards(Flashcard flashcard, View view) {
        CheckBox deleteCheckBox = view.findViewById(R.id.card_delete_checkbox);
        boolean isChecked = !deleteCheckBox.isChecked();

        deleteCheckBox.setChecked(isChecked);

        if (isChecked)
            deleteList.add(flashcard);
        else
            deleteList.remove(flashcard);
    }

    /**
     * Deletes the flashcards in the delete list from the database and updates the deck.
     */
    private void deleteFlashcards() {
        for (Flashcard flashcard : deleteList) {
            viewModel.delete(flashcard);
            deck.decrementSize();
            adapter.delete(flashcard);
        }

        viewModel.update(deck);

        deleteList.clear();
        isDelete = false;

        showDeleteButtons();
    }

    /**
     * Tells the recycler adapter to show or hide the delete checkboxes in the ViewHolders, and
     * hides the delete FAB.
     */
    private void showDeleteButtons() {
        if (isDelete) {
            binding.deleteFab.setVisibility(View.VISIBLE);
        } else {
            binding.deleteFab.setVisibility(View.GONE);
        }

        adapter.setDelete(isDelete);
        adapter.notifyDataSetChanged();
    }

    /**
     * Shows a message asking for confirmation to delete the selected flashcards.
     */
    private void showDeleteDialog() {
        int deleteSize = deleteList.size();

        if (deleteSize == 0) {
            isDelete = false;
            showDeleteButtons();
            return;
        }

        Bundle bundle = new Bundle();
        if (deleteSize == 1) {
            bundle.putString(DeleteDialog.MESSAGE, getString(R.string.delete_flashcard));
        } else {
            bundle.putString(DeleteDialog.MESSAGE, getString(R.string.delete_multiple_flashcards));
        }

        DeleteDialog dialog = new DeleteDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "DeleteDialog");
    }

    /**
     * Deletes the selected flashcards from the database when the user confirms deletion.
     *
     * @param dialog The dialog that was clicked on
     */
    @Override
    public void onDeleteDialogPositive(DialogFragment dialog) {
        deleteFlashcards();
    }

    /**
     * Tells the activity to clear the deletion list and buttons when the user cancels deletion.
     *
     * @param dialog The dialog that was clicked on
     */
    @Override
    public void onDeleteDialogNegative(DialogFragment dialog) {
        deleteList.clear();
        isDelete = false;
        showDeleteButtons();
    }

    /**
     * Inflates the menu using the specified layout.
     *
     * @param menu The menu to be created
     * @return Returns true to indicate that a menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_flashcards_menu, menu);
        return true;
    }

    /**
     * Tells the activity what to do when the user clicks a menu item.
     *
     * @param item The menu item that the user clicked on
     * @return Returns true if the menu item matches a listed item and the indicated action was performed
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.view_cards_add) {
            startActivity(new Intent(ViewFlashcardsActivity.this, AddFlashcardActivity.class));
            return true;
        } else if (id == R.id.view_cards_delete) {
            isDelete = true;
            showDeleteButtons();
            return true;
        } else if (id == R.id.view_cards_search) {
            startActivity(new Intent(ViewFlashcardsActivity.this, SearchFlashcardsActivity.class));
            return true;
        } else if (id == R.id.view_cards_decks) {
            startActivity(new Intent(ViewFlashcardsActivity.this, ViewDecksActivity.class));
            return true;
        } else if (id == R.id.view_cards_review) {
            Intent intent = new Intent(ViewFlashcardsActivity.this, ReviewActivity.class);
            intent.putExtra(ReviewActivity.DECK_ID, deck.getDeckId());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}