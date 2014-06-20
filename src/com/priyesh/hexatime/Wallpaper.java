package com.priyesh.hexatime;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Wallpaper {

    private Paint backgroundPaint;

    public Wallpaper() {

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.CYAN);

    }

    public synchronized void updateSize(int width, int height) {

        update();

    }

    public synchronized void update() {

        
    }

    public synchronized void draw(Canvas canvas) {

        // clear the background
        canvas.drawPaint(backgroundPaint);

    }

}
