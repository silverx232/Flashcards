package com.barnes.flashcards.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.barnes.flashcards.databinding.ActivityWelcomePageBinding;

/**
 * Controller for the welcome page.
 *
 * <p> This class shows a welcome page for the user's first time loading the app. This gives
 * the database time to finish loading if test data is supplied in the code. </p>
 */
public class WelcomePage extends AppCompatActivity {
    private ActivityWelcomePageBinding binding;

    /**
     * Method that runs when the activity is created.
     *
     * <p> This method initializes the views in the layout. </p>
     * @param savedInstanceState Contains data supplied to onSaveInstanceState() or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWelcomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.continueButton3.setOnClickListener(view -> {
            startActivity(new Intent(WelcomePage.this, ViewDecksActivity.class));
        });
    }
}