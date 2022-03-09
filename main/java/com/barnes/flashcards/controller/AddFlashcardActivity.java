package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.barnes.flashcards.R;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivityAddFlashcardBinding;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;
import com.barnes.flashcards.util.InformationDialog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for adding a flashcard.
 *
 * <p> This activity lets the user add a flashcard or edit an existing flashcard. </p>
 */
public class AddFlashcardActivity extends AppCompatActivity {
    /**
     * Tag for passing a flashcard ID through an intent. Used for editing a flashcard.
     */
    public static final String FLASHCARD_ID = "flashcard id";

    private ActivityAddFlashcardBinding binding;
    private FlashcardsViewModel viewModel;
    private ArrayAdapter<Flashcard.Status> statusAdapter;
    private ArrayAdapter<Deck> deckAdapter;
    private boolean isEdit = false;
    private Flashcard userFlashcard;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It checks whether information was
     * supplied through an Intent. If so, a flashcard is being edited instead of added. So it
     * loads that flashcard's information into the screen for editing. </p>
     * @param savedInstanceState contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddFlashcardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(FlashcardsViewModel.class);

        int cardId = 0;
        Bundle intentData = getIntent().getExtras();
        if (intentData != null) {
            isEdit = true;
            cardId = intentData.getInt(FLASHCARD_ID);
        }

        //Set up status spinner
        statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Flashcard.Status.statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusSpinner.setAdapter(statusAdapter);

        // Set up deck spinner and edit flashcard information
        loadEditTask(cardId);

        binding.saveButton.setOnClickListener(view -> {
            saveFlashcard();
        });
    }

    /**
     * Loads the deck spinner and the information for editing a flashcard.
     *
     * <p> This method adds a list of all decks to the deck spinner. If editing a flashcard, it finds
     * the flashcard with the supplied ID and loads the information into the views. It runs on a
     * background thread. </p>
     */
    private void loadEditTask(int cardId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Set up deck spinner
            List<Deck> deckList = viewModel.getAllDecks();
            deckAdapter = new ArrayAdapter<>(AddFlashcardActivity.this,
                    android.R.layout.simple_spinner_item, deckList);
            deckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.deckSpinner.setAdapter(deckAdapter);

            // Load edit information if editing a flashcard
            if (isEdit) {
                userFlashcard = viewModel.getFlashcardFromId(cardId);
                Deck deck = viewModel.getDeckFromId(userFlashcard.getDeckId());

                runOnUiThread(() -> {
                    binding.frontEditText.setText(userFlashcard.getFront());
                    binding.backEditText.setText(userFlashcard.getBack());
                    binding.statusSpinner.setSelection(statusAdapter.getPosition(userFlashcard.getStatus()));
                    binding.deckSpinner.setSelection(deckAdapter.getPosition(deck));
                });
            } else {
                userFlashcard = new Flashcard();
            }
        });
    }

    /**
     * Saves a flashcard in the database.
     *
     * <p> Creates a flashcard with the user supplied information and saves it in the database.
     * A front, back, and deck are required. After saving the flashcard, the user is returned
     * to viewing all flashcards from the saved flashcard's deck. </p>
     */
    private void saveFlashcard() {
        String front = binding.frontEditText.getText().toString().trim();
        if (front.isEmpty()) {
            showRequiredDialog(getString(R.string.front_required));
            return;
        }

        String back = binding.backEditText.getText().toString().trim();
        if (back.isEmpty()) {
            showRequiredDialog(getString(R.string.back_required));
            return;
        }

        Deck deck = (Deck) binding.deckSpinner.getSelectedItem();
        if (deck == null) {
            showRequiredDialog(getString(R.string.deck_required));
            return;
        }

        Flashcard.Status status = (Flashcard.Status) binding.statusSpinner.getSelectedItem();

        userFlashcard.setFront(front);
        userFlashcard.setBack(back);
        userFlashcard.setDeckId(deck.getDeckId());
        userFlashcard.setStatus(status);

        if (isEdit) {
            viewModel.update(userFlashcard);
        } else {
            viewModel.insert(userFlashcard);

            //update deck size and save
            deck.incrementSize();
            viewModel.update(deck);
        }

        // Go to view all flashcards in selected deck
        Intent intent = new Intent(AddFlashcardActivity.this, ViewFlashcardsActivity.class);
        intent.putExtra(ViewFlashcardsActivity.DECK_ID, deck.getDeckId());
        startActivity(intent);
    }

    /**
     * Shows a message informing the user that the information in message is required in order to
     * save the flashcard.
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
            startActivity(new Intent(AddFlashcardActivity.this, ViewDecksActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}