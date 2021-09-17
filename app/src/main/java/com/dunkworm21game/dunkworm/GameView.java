package com.dunkworm21game.dunkworm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GameView extends View {

    Bitmap bitmapGrass;
    public static int sizeOfMap = 75*Constants.SCREEN_WITH*1000;
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmapGrass = BitmapFactory.decodeResource(getResources(), R.drawable.bg_image);
        bitmapGrass = Bitmap.createScaledBitmap(bitmapGrass, sizeOfMap, sizeOfMap, false);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(bitmapGrass, Constants.SCREEN_HEIGHT*1.0f,Constants.SCREEN_WITH*1.0f, null);
    }
}
