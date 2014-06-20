package com.priyesh.hexatime;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class AnimationThread extends Thread {

    private static final String TAG = "AnimationThread";

    private Object pauseLock = new Object();

    private boolean running = true;
    private boolean paused = true;

    private int fps = 30;
    private int timeFrame = 1000 / fps; // drawing time frame in miliseconds 1000 ms / fps

    private SurfaceHolder surfaceHolder;
    private Wallpaper scene;

    AnimationThread(SurfaceHolder surfaceHolder, Wallpaper scene) {
        this.surfaceHolder = surfaceHolder;
        this.scene = scene;
    }

    @Override
    public void run() {

        while (running) {

            waitOnPause();

            if (!running) {
                return;
            }

            long beforeDrawTime = System.currentTimeMillis();

            Canvas canvas = null;
            try {

                canvas = surfaceHolder.lockCanvas();

                /** Workaround for: SurfaceTextureClient: dequeueBuffer failed (No such device) */
                if (canvas == null) {
                    continue;
                }

                scene.update();
                scene.draw(canvas);

            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error during surfaceHolder.lockCanvas()", e);
                stopThread();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "Error during unlockCanvasAndPost()", e);
                        stopThread();
                    }
                }
            }

            long afterDrawTime = System.currentTimeMillis() - beforeDrawTime;
            try {
                if (timeFrame > afterDrawTime) {
                    Thread.sleep(timeFrame - afterDrawTime);
                }
            } catch (InterruptedException ex) {
                Log.e(TAG, "Exception during Thread.sleep().", ex);
            }

        }

    }

    public void stopThread() {
        synchronized (pauseLock) {
            paused = false;
            running = false;
            pauseLock.notifyAll();
        }
        Log.d(TAG, "Stopped thread (" + this.getId() + ")");
    }

    public void pauseThread() {
        synchronized (pauseLock) {
            paused = true;
        }
        Log.d(TAG, "Paused thread (" + this.getId() + ")");
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
        Log.d(TAG, "Resumed thread (" + this.getId() + ")");
    }

    private void waitOnPause() {
        synchronized (pauseLock) {
            while (paused) {
                try {
                    pauseLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
