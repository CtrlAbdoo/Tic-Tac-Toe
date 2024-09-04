package com.example.tic_tac_toe;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ai extends AppCompatActivity {

    private final List<int[]> combinationsList = new ArrayList<>();
    private int[] boxPositions = new int[9];
    private int playerTurn = 1; // 1 for player, 2 for bot
    private int totalSelectedBoxes = 0;
    private boolean gameActive = true;

    private LinearLayout playerOneLayout, playerTwoLayout;
    private TextView playerOneName, playerTwoName;
    private TextView playerOneScoreText, playerTwoScoreText; // TextViews to display scores
    private ImageView[] imageViews = new ImageView[9];
    private Button resetScoreButton; // Button to reset scores

    private int playerOneScore = 0; // Score variable for Player One
    private int playerTwoScore = 0; // Score variable for Bot

    // MediaPlayer for the winning sound effect
    private MediaPlayer winSound;

    private final Random random = new Random(); // Random object for bot moves
    private final Handler handler = new Handler(); // Handler for delay in bot moves

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
        playerOneName.setText("Me");
        playerTwoName.setText("Bot");

        // Initialize MediaPlayer with the winning sound
        winSound = MediaPlayer.create(this, R.raw.win_sound);

        // Set click listeners for all image views
        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            imageViews[i].setOnClickListener(v -> {
                if (gameActive && isBoxSelectable(index)) {
                    performAction((ImageView) v, index, 1);
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

        playerOneScoreText = findViewById(R.id.PlayerOneScore);
        playerTwoScoreText = findViewById(R.id.PlayerTwoScore);

        resetScoreButton = findViewById(R.id.resetScoreButton);

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
    private void performAction(ImageView imageView, int selectedBoxPosition, int player) {
        boxPositions[selectedBoxPosition] = player;
        totalSelectedBoxes++;

        // Update UI based on player turn
        if (player == 1) {
            imageView.setImageResource(R.drawable.cross);
            if (checkWinner()) {
                playerOneScore++;
                updateScoreDisplay();
                endGame(playerOneName.getText().toString() + " won!");
            } else {
                changePlayerTurn();
                if (gameActive) {
                    botPlay();
                }
            }
        } else {
            imageView.setImageResource(R.drawable.zero);
            if (checkWinner()) {
                playerTwoScore++;
                updateScoreDisplay();
                endGame(playerTwoName.getText().toString() + " won!");
            } else {
                changePlayerTurn();
            }
        }

        // Check for draw
        if (totalSelectedBoxes == 9 && gameActive) {
            endGame("It's a Draw!");
        }
    }

    // Bot's turn to play
    private void botPlay() {
        handler.postDelayed(() -> {
            int botMove = getBotMove();
            if (botMove != -1) {
                performAction(imageViews[botMove], botMove, 2);
            }
        }, 500); // Delay to make the bot's move more natural
    }

    // Get a strategic move for the bot
    private int getBotMove() {
        // 1. Check if the bot can win
        int winMove = getWinningMove(2);
        if (winMove != -1) {
            return winMove;
        }

        // 2. Check if the bot needs to block the player from winning
        int blockMove = getWinningMove(1);
        if (blockMove != -1) {
            return blockMove;
        }

        // 3. Take the center if available
        if (isBoxSelectable(4)) {
            return 4;
        }

        // 4. Take a corner if available
        int[] corners = {0, 2, 6, 8};
        for (int corner : corners) {
            if (isBoxSelectable(corner)) {
                return corner;
            }
        }

        // 5. Fall back to a random move
        return getRandomMove();
    }

    // Check for a winning move for the given player (1 for player, 2 for bot)
    private int getWinningMove(int player) {
        for (int[] combination : combinationsList) {
            int first = combination[0], second = combination[1], third = combination[2];
            if (boxPositions[first] == player && boxPositions[second] == player && boxPositions[third] == 0) {
                return third;
            }
            if (boxPositions[first] == player && boxPositions[third] == player && boxPositions[second] == 0) {
                return second;
            }
            if (boxPositions[second] == player && boxPositions[third] == player && boxPositions[first] == 0) {
                return first;
            }
        }
        return -1; // No winning move found
    }

    // Get a random valid move for the bot
    private int getRandomMove() {
        List<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < boxPositions.length; i++) {
            if (boxPositions[i] == 0) {
                availablePositions.add(i);
            }
        }
        if (availablePositions.isEmpty()) {
            return -1;
        }
        return availablePositions.get(random.nextInt(availablePositions.size()));
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

        WinDialogAi winDialog = new WinDialogAi(Ai.this, message, Ai.this);
        winDialog.setCancelable(false);
        winDialog.show();
        gameActive = false;
    }

    // Check if the box is selectable
    private boolean isBoxSelectable(int boxPosition) {
        return boxPositions[boxPosition] == 0;
    }
    // Inside Ai.java

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


    // Reset the scores
    private void resetScores() {
        playerOneScore = 0;
        playerTwoScore = 0;
        updateScoreDisplay();
    }

    // Update score display
    private void updateScoreDisplay() {
        playerOneScoreText.setText(String.valueOf(playerOneScore));
        playerTwoScoreText.setText(String.valueOf(playerTwoScore));
    }
}
