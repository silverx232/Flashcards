package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.barnes.flashcards.R;
import com.barnes.flashcards.adapter.DeckRecyclerAdapter;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivityViewDecksBinding;
import com.barnes.flashcards.model.Deck;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for viewing all decks.
 *
 * <p> This class lets the user view information about the decks in the database. </p>
 */
public class ViewDecksActivity extends AppCompatActivity {
    private ActivityViewDecksBinding binding;
    private FlashcardsViewModel viewModel;

    // Tags for if it's the first time loading the app
    private static final String SHARED_PREFERENCES = "shared preferences";
    private static final String FIRST_TIME = "first time";

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. It gets a list of all decks in the
     * database and loads the Recycler Adapter with the list. Clicking on a deck will bring the
     * user to a more detailed view of the deck. </p>
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it's the first time the app was loaded
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME, true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_TIME, false)
                    .apply();
            startActivity(new Intent(ViewDecksActivity.this, WelcomePage.class));
        }

        binding = ActivityViewDecksBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(FlashcardsViewModel.class);

        // Set up the recycler view
        binding.deckRecyclerview.setHasFixedSize(true);
        binding.deckRecyclerview.setLayoutManager(new LinearLayoutManager(ViewDecksActivity.this));

        // Populate the recycler view
        loadDecksTask();
    }

    /**
     * Sets up the recycler view.
     *
     * <p> This method gets the list of all decks in the database. It then sets up the recycler view
     * using that list. It runs on a background thread. </p>
     */
    private void loadDecksTask() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Deck> deckList = viewModel.getAllDecks();

            DeckRecyclerAdapter adapter = new DeckRecyclerAdapter(deckList, (position, view) -> {  // OnContactClickListener
                Deck deck = deckList.get(position);

                Intent intent = new Intent(ViewDecksActivity.this, DeckInfoActivity.class);
                intent.putExtra(DeckInfoActivity.DECK_ID, deck.getDeckId());
                startActivity(intent);
            });

            runOnUiThread(() -> {
                binding.deckRecyclerview.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.view_decks_menu, menu);
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

        if (id == R.id.view_deck_add) {
            startActivity(new Intent(ViewDecksActivity.this, AddDeckActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}