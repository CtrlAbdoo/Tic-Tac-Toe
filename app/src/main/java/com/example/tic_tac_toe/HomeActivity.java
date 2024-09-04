package com.example.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Get references to the buttons
        MaterialCardView singlePlayerButton = findViewById(R.id.single_player_button);
        MaterialCardView duoPlayerButton = findViewById(R.id.duo_player_button);

        // Set click listener for single player button
        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Ai activity
                Intent intent = new Intent(HomeActivity.this, Ai.class);
                startActivity(intent);
            }
        });

        // Set click listener for player vs player button
        duoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(HomeActivity.this, AddPlayers.class);
                startActivity(intent);
            }
        });
    }
}
