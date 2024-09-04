package com.example.tic_tac_toe;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<int[]> combinationsList = new ArrayList<>();
    private int[] boxPositions = new int[9];
    private int playerTurn = 1;
    private int totalSelectedBoxes = 0;
    private boolean gameActive = true;

    private LinearLayout playerOneLayout, playerTwoLayout;
    private TextView playerOneName, playerTwoName;
    private TextView playerOneScoreText, playerTwoScoreText;  // TextViews to display scores
    private ImageView[] imageViews = new ImageView[9];
    private Button resetScoreButton;  // Button to reset scores

    private int playerOneScore = 0;  // Score variable for Player One
    private int playerTwoScore = 0;  // Score variable for Player Two

    // MediaPlayer for the winning sound effect
    private MediaPlayer winSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize combinations
        combinationsList.add(new int[]{0, 1, 2});
        combinationsList.add(new int[]{3, 4, 5});
        combinationsList.add(new int[]{6, 7, 8});
        combinationsList.add(new int[]{0, 3, 6});
        combinationsList.add(new int[]{1, 4, 7});
        combinationsList.add(new int[]{2, 5, 8});
        combinationsList.add(new int[]{0, 4, 8});
        combinationsList.add(new int[]{2, 4, 6});

        // Initialize views
        initializeViews();

        // Retrieve player names from intent
        final String getPlayerOneName = getIntent().getStringExtra("playerOne");
        final String getPlayerTwoName = getIntent().getStringExtra("playerTwo");

        playerOneName.setText(getPlayerOneName);
        playerTwoName.setText(getPlayerTwoName);

        // Initialize MediaPlayer with the winning sound
        winSound = MediaPlayer.create(this, R.raw.win_sound);

        // Set click listeners for all image views
        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            imageViews[i].setOnClickListener(v -> {
                if (gameActive && isBoxSelectable(index)) {
                    performAction((ImageView) v, index);
                }
            });
        }

        // Set up the reset score button
        resetScoreButton.setOnClickListener(v -> resetScores());
    }

    // Initialize views and link with XML
    private void initializeViews() {
        playerOneName = findViewById(R.id.PlayerOneName);
        playerTwoName = findViewById(R.id.PlayerTwoName);

        playerOneLayout = findViewById(R.id.PlayerOneLayout);
        playerTwoLayout = findViewById(R.id.PlayerTwoLayout);

        playerOneScoreText = findViewById(R.id.PlayerOneScore);  // Add this to link to your XML
        playerTwoScoreText = findViewById(R.id.PlayerTwoScore);  // Add this to link to your XML

        resetScoreButton = findViewById(R.id.resetScoreButton);  // Initialize the reset button

        imageViews[0] = findViewById(R.id.image1);
        imageViews[1] = findViewById(R.id.image2);
        imageViews[2] = findViewById(R.id.image3);
        imageViews[3] = findViewById(R.id.image4);
        imageViews[4] = findViewById(R.id.image5);
        imageViews[5] = findViewById(R.id.image6);
        imageViews[6] = findViewById(R.id.image7);
        imageViews[7] = findViewById(R.id.image8);
        imageViews[8] = findViewById(R.id.image9);
    }

    // Perform an action when a box is selected
    private void performAction(ImageView imageView, int selectedBoxPosition) {
        boxPositions[selectedBoxPosition] = playerTurn;
        totalSelectedBoxes++;

        // Update UI based on player turn
        if (playerTurn == 1) {
            imageView.setImageResource(R.drawable.cross);
            if (checkWinner()) {
                playerOneScore++;
                updateScoreDisplay();  // Update the score display
                endGame(playerOneName.getText().toString() + " Has won!");
            } else {
                changePlayerTurn();
            }
        } else {
            imageView.setImageResource(R.drawable.zero);
            if (checkWinner()) {
                playerTwoScore++;
                updateScoreDisplay();  // Update the score display
                endGame(playerTwoName.getText().toString() + " Has won!");
            } else {
                changePlayerTurn();
            }
        }

        // Check for draw
        if (totalSelectedBoxes == 9 && gameActive) {
            endGame("It's a Draw!");
        }
    }

    // Update the player turn and UI
    private void changePlayerTurn() {
        playerTurn = (playerTurn == 1) ? 2 : 1;

        if (playerTurn == 1) {
            playerOneLayout.setBackgroundResource(R.drawable.round_back_blue_border);
            playerTwoLayout.setBackgroundResource(R.drawable.round_back_dark_blue);
        } else {
            playerTwoLayout.setBackgroundResource(R.drawable.round_back_blue_border);
            playerOneLayout.setBackgroundResource(R.drawable.round_back_dark_blue);
        }
    }

    // Check if there is a winner
    private boolean checkWinner() {
        for (int[] combination : combinationsList) {
            if (boxPositions[combination[0]] == playerTurn &&
                    boxPositions[combination[1]] == playerTurn &&
                    boxPositions[combination[2]] == playerTurn) {
                gameActive = false;
                return true;
            }
        }
        return false;
    }

    // End the game and show dialog
    private void endGame(String message) {
        // Play win sound
        if (!message.contains("Draw") && winSound != null) {
            winSound.start();
        }

        WinDialog winDialog = new WinDialog(MainActivity.this, message, MainActivity.this);
        winDialog.setCancelable(false);
        winDialog.show();
        gameActive = false;
    }

    // Check if the box is selectable
    private boolean isBoxSelectable(int boxPosition) {
        return boxPositions[boxPosition] == 0;
    }

    // Restart the match
    public void restartMatch() {
        boxPositions = new int[9];
        totalSelectedBoxes = 0;
        playerTurn = 1;
        gameActive = true;

        // Reset all the image views
        for (ImageView imageView : imageViews) {
            imageView.setImageResource(R.drawable.transparent_back);
        }

        // Reset the player layout colors
        playerOneLayout.setBackgroundResource(R.drawable.round_back_blue_border);
        playerTwoLayout.setBackgroundResource(R.drawable.round_back_dark_blue);
    }

    // Update the score display
    private void updateScoreDisplay() {
        playerOneScoreText.setText(String.valueOf(playerOneScore));
        playerTwoScoreText.setText(String.valueOf(playerTwoScore));
    }

    // Reset the scores
    private void resetScores() {
        playerOneScore = 0;
        playerTwoScore = 0;
        updateScoreDisplay();  // Update the UI with reset scores
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources
        if (winSound != null) {
            winSound.release();
            winSound = null;
        }
    }
}
