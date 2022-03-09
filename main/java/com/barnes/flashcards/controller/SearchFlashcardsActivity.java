package com.barnes.flashcards.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.barnes.flashcards.R;
import com.barnes.flashcards.adapter.FlashcardRecyclerAdapter;
import com.barnes.flashcards.database.FlashcardsViewModel;
import com.barnes.flashcards.databinding.ActivitySearchFlashcardsBinding;
import com.barnes.flashcards.model.Flashcard;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for searching for existing flashcards.
 *
 * <p> This class lets the search the database for existing flashcards using a given String. </p>
 */
public class SearchFlashcardsActivity extends AppCompatActivity {
    private ActivitySearchFlashcardsBinding binding;
    private FlashcardsViewModel viewModel;
    private FlashcardRecyclerAdapter recyclerAdapter;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. </p>
     * @param savedInstanceState contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchFlashcardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(FlashcardsViewModel.class);

        binding.flashcardRecyclerView.setHasFixedSize(true);
        binding.flashcardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Clicking on search will load the recycler view with the results
        binding.searchButton.setOnClickListener(view -> {
            searchFlashcards();

            // Hide the keyboard
            InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

    }

    /**
     * Sets up the recycler view in response to a search.
     *
     * <p> This method gets the list of all flashcards whose front or back are LIKE the search. It
     * then sets up the recycler view using that list. Clicking on a view will take the user to
     * that flashcard's information. It runs on a background thread. </p>
     */
    private void searchFlashcards() {
        String search = binding.enterSearch.getText().toString().trim();
        if (search.isEmpty())
            return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Flashcard> searchList = viewModel.searchFlashcards(search);

            recyclerAdapter = new FlashcardRecyclerAdapter(searchList, (position, view) -> {  // OnContactClickListener
                Flashcard flashcard = recyclerAdapter.getItems().get(position);

                Intent intent = new Intent(SearchFlashcardsActivity.this, FlashcardInfoActivity.class);
                intent.putExtra(FlashcardInfoActivity.FLASHCARD_ID, flashcard.getCardId());
                startActivity(intent);
            });

            runOnUiThread(() -> {
                binding.flashcardRecyclerView.setAdapter(recyclerAdapter);
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
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
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
            startActivity(new Intent(SearchFlashcardsActivity.this, ViewDecksActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}