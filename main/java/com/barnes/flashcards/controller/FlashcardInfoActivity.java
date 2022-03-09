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
import com.barnes.flashcards.databinding.ActivityFlashcardInfoBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;
import com.barnes.flashcards.util.DeleteDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for viewing a flashcard.
 *
 * <p> This class lets the user view information about a selected flashcard. </p>
 */
public class FlashcardInfoActivity extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {
    /**
     * Tag for passing an ID for the user selected flashcard through an intent.
     */
    public static final String FLASHCARD_ID = "flashcard id";

    private ActivityFlashcardInfoBinding binding;
    private FlashcardsViewModel viewModel;
    private Flashcard flashcard;
    private Deck deck;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It checks for the flashcard ID provided in
     * the Intent, finds that flashcard, and loads the information into the views. An Intent should
     * always be provided. </p>
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFlashcardInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(FlashcardsViewModel.class);

        int cardId = 1;
        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            cardId = intentData.getInt(FLASHCARD_ID);
        }

        // Load the information from the user selected flashcard
        loadFlashcardTask(cardId);

        binding.editButton.setOnClickListener(view -> {
            Intent intent = new Intent(FlashcardInfoActivity.this, AddFlashcardActivity.class);
            intent.putExtra(AddFlashcardActivity.FLASHCARD_ID, flashcard.getCardId());
            startActivity(intent);
        });
    }

    /**
     * Loads the information from a user selected flashcard.
     *
     * <p> This method uses the supplied flashcard ID to find the card in the database. It then
     * loads the flashcard information into the views. It runs on a background thread. </p>
     * @param cardId The ID of the user supplied flashcard
     */
    private void loadFlashcardTask(int cardId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            flashcard = viewModel.getFlashcardFromId(cardId);
            deck = viewModel.getDeckFromId(flashcard.getDeckId());

            runOnUiThread(() -> {
                binding.enterFront.setText(flashcard.getFront());
                binding.enterBack.setText(flashcard.getBack());

                if (flashcard.getStatus() != null)
                    binding.enterStatus.setText(flashcard.getStatus().toString());

                binding.enterDeckTitle.setText(deck.getTitle());
            });
        });
    }

    /**
     * Deletes the flashcard from the database and updates the deck.
     */
    private void deleteFlashcard() {
        viewModel.delete(flashcard);

        deck.decrementSize();
        viewModel.update(deck);
    }

    /**
     * Inflates the menu using the specified layout.
     *
     * @param menu The menu to be created
     * @return Returns true to indicate that a menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.flashcard_info_menu, menu);
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

        if (id == R.id.card_info_add) {
            startActivity(new Intent(FlashcardInfoActivity.this, AddFlashcardActivity.class));
            return true;
        } else if (id == R.id.card_info_delete) {
            showDeleteDialog();
            return true;
        } else if (id == R.id.card_info_search) {
            startActivity(new Intent(FlashcardInfoActivity.this, SearchFlashcardsActivity.class));
            return true;
        } else if (id == R.id.card_info_view_decks) {
            startActivity(new Intent(FlashcardInfoActivity.this, ViewDecksActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a message asking for confirmation to delete the selected flashcard.
     */
    private void showDeleteDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(DeleteDialog.MESSAGE, getString(R.string.delete_flashcard));

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
        deleteFlashcard();

        // Go to view flashcards from the deleted flashcard's deck
        Intent intent = new Intent(FlashcardInfoActivity.this, ViewFlashcardsActivity.class);
        intent.putExtra(ViewFlashcardsActivity.DECK_ID, deck.getDeckId());
        startActivity(intent);
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