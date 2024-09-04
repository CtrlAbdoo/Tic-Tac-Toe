package com.example.tic_tac_toe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class WinDialogAi extends Dialog {

    private final String message;
    private final Ai aiActivity;

    public WinDialogAi(@NonNull Context context, String message, Ai aiActivity) {
        super(context);
        this.message = message;
        this.aiActivity = aiActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.win_dialog_layout);

        final TextView messagTxt = findViewById(R.id.messageTxt);
        final Button startAgainBtn = findViewById(R.id.startAgainBtn);

        messagTxt.setText(message);

        startAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiActivity.restartMatch(); // Use aiActivity to call restartMatch
                dismiss();
            }
        });
    }
}
