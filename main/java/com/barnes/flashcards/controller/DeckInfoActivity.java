package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.barnes.flashcards.R;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivityDeckInfoBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.util.DeleteDialog;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for viewing a deck.
 *
 * <p> This class lets the user view information about a selected deck. </p>
 */
public class DeckInfoActivity extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {
    /**
     * Tag for passing an ID for the user selected deck through an intent.
     */
    public static final String DECK_ID = "deck id";

    private ActivityDeckInfoBinding binding;
    private FlashcardsViewModel viewModel;
    private Deck deck;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It checks for the deck ID provided in
     * the Intent, finds that deck, and loads the information into the views. An Intent should
     * always be provided. </p>
     * @param savedInstanceState contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeckInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(DeckInfoActivity.this.getApplication())
                .create(FlashcardsViewModel.class);

        int deckId = 1;
        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            deckId = intentData.getInt(DECK_ID);
        }

        // Load the information from the user selected deck
        loadDeckTask(deckId);

        binding.flashcardsButton.setOnClickListener(view -> {
            Intent intent = new Intent(DeckInfoActivity.this, ViewFlashcardsActivity.class);
            intent.putExtra(ViewFlashcardsActivity.DECK_ID, deck.getDeckId());
            startActivity(intent);
        });

        binding.reviewButton.setOnClickListener(view -> {
            Intent intent = new Intent(DeckInfoActivity.this, ReviewActivity.class);
            intent.putExtra(ReviewActivity.DECK_ID, deck.getDeckId());
            startActivity(intent);
        });
    }

    /**
     * Loads the information from a user selected deck.
     *
     * <p> This method uses the supplied deck ID to find the deck in the database. It then
     * loads the deck information into the views. It runs on a background thread. </p>
     */
    private void loadDeckTask(int deckId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            deck = viewModel.getDeckFromId(deckId);

            runOnUiThread(() -> {
                // Populate the deck information
                binding.titleText.setText(deck.getTitle());
                binding.enterSize.setText(Integer.toString(deck.getSize()));
                binding.enterPercentRight.setText(String.format("%.2f%%", deck.getPercentRight()));
                binding.enterDatetime.setText(deck.getTimeReviewed()
                        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
            });
        });
    }

    /**
     * Inflates the menu using the specified layout.
     *
     * @param menu The menu to be created
     * @return Returns true to indicate that a menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deck_info_menu, menu);
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

        if (id == R.id.deck_info_add) {
            startActivity(new Intent(DeckInfoActivity.this, AddDeckActivity.class));
            return true;
        } else if (id == R.id.deck_info_delete) {
            showDeleteDialog();
            return true;
        } else if (id == R.id.deck_info_edit) {
            Intent intent = new Intent(DeckInfoActivity.this, AddDeckActivity.class);
            intent.putExtra(AddDeckActivity.DECK_ID, deck.getDeckId());
            startActivity(intent);
            return true;
        } else if (id == R.id.deck_info_search) {
            startActivity(new Intent(DeckInfoActivity.this, SearchFlashcardsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a message asking for confirmation to delete the selected deck.
     */
    private void showDeleteDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(DeleteDialog.MESSAGE, getString(R.string.delete_deck));

        DeleteDialog dialog = new DeleteDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "DeleteDialog");
    }

    /**
     * Deletes the selected flashcard when the user confirms deletion.
     *
     * @param dialog The dialog that was clicked on
     */
    @Override
    public void onDeleteDialogPositive(DialogFragment dialog) {
        viewModel.delete(deck);

        // Go to view all decks
        startActivity(new Intent(DeckInfoActivity.this, ViewDecksActivity.class));
    }

    /**
     * Tells the activity to do nothing when the user cancels deletion.
     *
     * @param dialog The dialog that was clicked on
     */
    @Override
    public void onDeleteDialogNegative(DialogFragment dialog) {
        // do nothing
    }
}