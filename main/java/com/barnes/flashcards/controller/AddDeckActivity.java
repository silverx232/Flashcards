package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.barnes.flashcards.R;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivityAddDeckBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.util.InformationDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for adding a deck.
 *
 * <p> This activity lets the user add a deck or edit an existing deck. </p>
 */
public class AddDeckActivity extends AppCompatActivity {
    /**
     * Tag for passing a deck ID through an intent. Used for editing a deck.
     */
    public static final String DECK_ID = "deck id";

    private ActivityAddDeckBinding binding;
    private FlashcardsViewModel viewModel;
    private boolean isEdit = false;
    private Deck userDeck;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It checks whether information was
     * supplied through an Intent. If so, a deck is being edited instead of added. So it
     * loads that deck's information into the screen for editing. </p>
     * @param savedInstanceState contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddDeckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(FlashcardsViewModel.class);

        int deckId = 0;
        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            isEdit = true;
            deckId = intentData.getInt(DECK_ID);
        }

        //Load flashcard information if provided
        if (isEdit) {
            loadEditTask(deckId);
        } else {
            userDeck = new Deck();
        }

        binding.saveButton2.setOnClickListener(view -> {
            saveDeck();
        });
    }

    /**
     * Loads the information for editing a flashcard.
     *
     * <p> This method finds the flashcard with the supplied ID and loads the information into the
     * views. It runs on a background thread. </p>
     */
    private void loadEditTask(int deckId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            userDeck = viewModel.getDeckFromId(deckId);

            runOnUiThread(() -> {
                binding.enterTitle.setText(userDeck.getTitle());
            });
        });
    }

    /**
     * Saves a deck in the database.
     *
     * <p> Creates a deck with the user supplied information and saves it in the database.
     * A title is required. After saving the deck, the user is returned to viewing all decks. </p>
     */
    private void saveDeck() {
        String title = binding.enterTitle.getText().toString().trim();
        if (title.isEmpty()) {
            showRequiredDialog(getString(R.string.title_required));
            return;
        }

        userDeck.setTitle(title);

        if (isEdit) {
            viewModel.update(userDeck);
        } else {
            viewModel.insert(userDeck);
        }

        // Go to view all decks
        startActivity(new Intent(AddDeckActivity.this, ViewDecksActivity.class));
    }

    /**
     * Shows a message informing the user that the information in message is required in order to
     * save the deck.
     *
     * @param message The message about the required information
     */
    private void showRequiredDialog(String message) {
        Bundle bundle = new Bundle();
        bundle.putString(InformationDialog.MESSAGE, message);

        InformationDialog dialog = new InformationDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "InformationDialog");
    }

    /**
     * Inflates the menu using the specified layout.
     *
     * @param menu The menu to be created
     * @return Returns true to indicate that a menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_menu, menu);
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

        if (id == R.id.add_edit_view_decks) {
            startActivity(new Intent(AddDeckActivity.this, ViewDecksActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}