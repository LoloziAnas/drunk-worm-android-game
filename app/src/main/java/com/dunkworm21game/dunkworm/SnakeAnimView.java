package com.dunkworm21game.dunkworm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class SnakeAnimView extends SurfaceView implements Runnable {
    Canvas canvas;
    SnakeAnimView snakeAnimView;
    //the snake head sprite sheet
    Bitmap headAnimBitmap;

    //the portion of the bitmap to be drawn in the current frame
    Rect rectToBeDrawn;
    //The dimensions of  a single frame
    int frameHeight = 64;
    int frameWidth = 64;
    int numFrames = 6;
    int frameNumber;

    static int screenWidth;
    static int screenHeight;

    //stats
    long lastFrameTime;
    int fps;
    int hi;
    Thread thread = null;
    SurfaceHolder holder;
    volatile boolean playingSnake;
    Paint paint;

    public SnakeAnimView(Context context) {
        super(context);
        holder = getHolder();
        frameHeight = headAnimBitmap.getHeight();
        frameWidth = headAnimBitmap.getWidth();

    }

    @Override
    public void run() {
        while (playingSnake) {
            update();
            draw();
            controlFPS();
        }
    }

    private void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
        long timeToSleep = 500 - timeThisFrame;

        if (timeThisFrame > 0) {
            fps = (int) (1000 / timeThisFrame);
        }
        if (timeToSleep > 0) {
            try {
                thread.sleep(timeToSleep);
            } catch (InterruptedException e) {

            }
        }
        lastFrameTime = System.currentTimeMillis();
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            // locking the canvas
            canvas = holder.lockCanvas();
            //convert background image drawable to Bitmap
            Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.bg_image);
            //background color
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(150);
            canvas.drawText("Snake", 10, 150, paint);
            paint.setTextSize(25);

            canvas.drawText(" Hi Score: " + hi, 10, screenHeight - 50, paint);

            //Draw the snake head
            //make this Rect whatever size and location as i like
            Rect destRect = new Rect(screenWidth / 2 - 100, screenHeight / 2 - 100, screenWidth / 2 + 100, screenHeight / 2 + 100);
            canvas.drawBitmap(headAnimBitmap, rectToBeDrawn, destRect, paint);
            //unlock the canvas
            holder.unlockCanvasAndPost(canvas);

        }
    }

    private void update() {
        //which frame should be drawn
        rectToBeDrawn = new Rect((frameNumber * frameWidth) - 1, 0,
                (frameNumber * frameWidth + frameWidth) - 1, frameHeight);
        //now the next frame
        frameNumber++;
        //don't try and draw frames that don't exist
        if (frameNumber == numFrames) {
            frameNumber = 0;//back to the first frame
        }
    }
    public void pause(){
        playingSnake = false;
        try {
            thread.join();
        }catch (InterruptedException e){

        }
    }

    public void resume(){
        playingSnake = true;
        thread = new Thread(this);
        thread.start();
    }
}

