package com.dunkworm21game.dunkworm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Random;

public class GameActivity extends Activity {

    Canvas canvas;
    SnakeView snakeView;

    Bitmap headBitmap;
    Bitmap bodyBitmap;
    Bitmap tailBitmap;
    Bitmap appleBitmap;
    Bitmap background;

    //Sound
    //initialize sound variables
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;
    int sample5 = -1;

    //for snake movement
    int directionOfTravel = 0;
    //0 = up, 1 = right, 2 = down, 3= left

    int screenWidth;
    int screenHeight;
    int topGap;

    //stats
    long lastFrameTime;
    int fps;
    int score;
    int hi;

    //Game objects
    int[] snakeX;
    int[] snakeY;
    int snakeLength;
    int ballX;
    int ballY;

    //The size in pixels of a place on the game board
    int blockSize;
    int numBlocksWide;
    int numBlocksHigh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSound();
        configureDisplay();
        snakeView = new SnakeView(this);
        setContentView(snakeView);
    }


    class SnakeView extends SurfaceView implements Runnable {

        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSnake;
        Paint paint;

        public SnakeView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();

            snakeX = new int[200];
            snakeY = new int[200];
            //our starting snake
            getSnake();
            // get ball to dunk
            getBall();

        }

        private void getSnake() {
            snakeLength = 3;
            //start snake head in the middle of screen
            snakeX[0] = numBlocksWide / 2;
            snakeY[0] = numBlocksHigh / 2;

            //Then the body
            snakeX[1] = snakeX[0] - 1;
            snakeY[1] = snakeY[0];

            //And the tail
            snakeX[1] = snakeX[1] - 1;
            snakeY[1] = snakeY[0];
        }

        private void getBall() {
            Random random = new Random();
            ballX = random.nextInt(numBlocksWide - 1) + 1;
            ballY = random.nextInt(numBlocksHigh - 1) + 1;
        }

        @Override
        public void run() {
            while (playingSnake) {
                updateGame();
                drawGame();
                controlFPS();
            }
        }

        private void updateGame() {
            if (snakeX[0] == ballX && snakeY[0] == ballY) {
                //grow the snake
                snakeLength++;
                //replace the apple
                getBall();
                //add to the score
                score = score + snakeLength;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }
            //move the body - starting at the back
            for (int i = snakeLength; i > 0; i--) {
                snakeX[i] = snakeX[i - 1];
                snakeY[i] = snakeY[i - 1];
            }
            //Move the head in the appropriate direction
            switch (directionOfTravel) {
                case 0://up
                    snakeY[0]--;
                    break;

                case 1://right
                    snakeX[0]++;
                    break;

                case 2://down
                    snakeY[0]++;
                    break;

                case 3://left
                    snakeX[0]--;
                    break;
            }
            // have we have accident
            boolean dead = false;
            //with a wall
            if (snakeX[0] == -1) dead = true;
            if (snakeX[0] == numBlocksWide) dead = true;
            if (snakeY[0] == -1) dead = true;
            if (snakeY[0] == numBlocksHigh) dead = true;
            // or eaten itself
            //or eaten ourselves?
            for (int i = snakeLength - 1; i > 0; i--) {
                if ((i > 4) && (snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])) {
                    dead = true;
                }
            }
            if (dead) {
                //start again
                soundPool.play(sample4, 1, 1, 0, 0, 1);
                score = 0;
                getSnake();

            }

        }

        private void drawGame() {
            Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_image);

            canvas = new Canvas(backgroundBitmap);
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //background
                //canvas.drawBitmap(backgroundBitmap, 0,0, null);
                canvas.drawColor(Color.BLACK);
                //Draw the snake
                canvas.drawBitmap(headBitmap, snakeX[0] * blockSize, (snakeY[0] * blockSize) + topGap, paint);

                //Draw the body
                for (int i = 1; i < snakeLength - 1; i++) {
                    canvas.drawBitmap(bodyBitmap, snakeX[i] * blockSize, (snakeY[i] * blockSize) + topGap, paint);
                }
                //draw the tail
                canvas.drawBitmap(tailBitmap, snakeX[snakeLength - 1] * blockSize, (snakeY[snakeLength - 1] * blockSize) + topGap, paint);

                //draw the apple
                canvas.drawBitmap(appleBitmap, ballX * blockSize, (ballY * blockSize) + topGap, paint);

                ourHolder.unlockCanvasAndPost(canvas);


            }
        }

        private void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 100 - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }
            if (timeToSleep > 0) {

                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    //Print an error message to the console
                    Log.e("error", "failed to load sound files");
                }

            }

            lastFrameTime = System.currentTimeMillis();
        }

        public void pause() {
            playingSnake = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }

        }

        public void resume() {
            playingSnake = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (motionEvent.getX() >= screenWidth / 2) {
                        //turn right
                        directionOfTravel++;
                        if (directionOfTravel == 4) {//no such direction
                            //loop back to 0(up)
                            directionOfTravel = 0;
                        }
                    } else {
                        //turn left
                        directionOfTravel--;
                        if (directionOfTravel == -1) {//no such direction
                            //loop back to 0(up)
                            directionOfTravel = 3;
                        }
                    }
            }
            return true;
        }

    }

    private void loadSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            //Create objects of the 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("Background_gameplay_music.mp3");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("review_sent.mp3");
            sample2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("worm_broke_the_shield.mp3");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("worm_crashed.mp3");
            sample4 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("worm_crashed.mp3");
            sample5 = soundPool.load(descriptor, 0);


        } catch (IOException e) {
            //Print an error message to the console
            Log.e("error", "failed to load sound files");

        }
    }


    private void configureDisplay() {
        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        topGap = screenHeight / 14;

        //Determine the size of each block/place on the game board
        blockSize = screenWidth / 40;

        //Determine how many game blocks will fit into the height and width
        //Leave one block for the score at the top
        numBlocksWide = 40;
        numBlocksHigh = ((screenHeight - topGap)) / blockSize;

        //Load and scale bitmaps
        headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_sprite);
        bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        tailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        appleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_image);

        //scale the bitmaps to match the block size
        headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
        bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap, blockSize, blockSize, false);
        tailBitmap = Bitmap.createScaledBitmap(tailBitmap, blockSize, blockSize, false);
        appleBitmap = Bitmap.createScaledBitmap(appleBitmap, blockSize, blockSize, false);
        background = Bitmap.createScaledBitmap(background, blockSize, blockSize, false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            snakeView.pause();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            snakeView.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeView.pause();
    }
}
