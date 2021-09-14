package com.dunkworm21game.dunkworm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageButton;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton imageButtonSound = findViewById(R.id.ib_sound);
        imageButtonSound.setOnClickListener(v -> {

            if (imageButtonSound.getTag().equals("0")){
                //imageButtonSound.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_no_sounds ));
                imageButtonSound.setBackgroundResource(R.drawable.ic_no_sounds );
                imageButtonSound.setTag("1");
            }
            if (imageButtonSound.getTag().equals("1")){
                imageButtonSound.setBackgroundResource(R.drawable.ic_sound );

                //imageButtonSound.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_sound ));
                imageButtonSound.setTag("0");
            }


        });

        ImageButton imageButtonMusic = findViewById(R.id.ib_music);
        imageButtonMusic.setOnClickListener(v -> {
            imageButtonMusic.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_no_music));
        });
        ImageButton imageButtonBack = findViewById(R.id.ib_back);
        imageButtonBack.setOnClickListener(v -> {
            Intent intent  = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

}