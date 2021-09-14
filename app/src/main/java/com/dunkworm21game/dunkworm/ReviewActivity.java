package com.dunkworm21game.dunkworm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ImageButton imageButtonBack = findViewById(R.id.ib_back);
        imageButtonBack.setOnClickListener(v -> {
            Intent intent  = new Intent(ReviewActivity.this, MainActivity.class);
            startActivity(intent);
        });
        EditText editTextFeedBack = findViewById(R.id.edt_feedback);
        ImageButton imageButtonSend = findViewById(R.id.ib_send);
        imageButtonSend.setOnClickListener(v -> {
            String feedback = editTextFeedBack.getText().toString();
            Toast.makeText(this, "Your Feedback:  "+feedback, Toast.LENGTH_LONG).show();
        });
    }
}