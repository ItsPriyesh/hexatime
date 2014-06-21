package com.priyesh.hexatime;

import java.util.Calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class HexatimeService extends WallpaperService {

	private static final String TAG = "Wallpaper";

	public int oneSecond = 1000;

	public int hour, min, sec;
	public Calendar cal;

	@Override
	public Engine onCreateEngine() {
		return new DemoWallpaperEngine();
	}

	private class DemoWallpaperEngine extends Engine {

		private boolean mVisible = false;
		private final Handler mHandler = new Handler();
		private GestureDetector mGestureDetector;

		private final Runnable mUpdateDisplay = new Runnable() {

			@Override
			public void run() {
				draw();
			}};

			private void draw() {
				cal = Calendar.getInstance();
				hour = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
				sec = cal.get(Calendar.SECOND);
				SurfaceHolder holder = getSurfaceHolder();
				Canvas c = null;
				try {
					c = holder.lockCanvas();
					if (c != null) {
						Paint p = new Paint();
						p.setTextSize(90);
						p.setTypeface(Typeface.createFromAsset(getAssets(), "LatoLight.ttf"));
						p.setAntiAlias(true);

						String text = String.format("#%02d%02d%02d", hour, min, sec );

						float d = p.measureText(text, 0, text.length());
						int offset = (int) d / 2;
						int w = c.getWidth();
						int h = c.getHeight();
						p.setColor(Color.argb(255, hour, min, sec));
						c.drawRect(0, 0, w, h, p);
						p.setColor(Color.WHITE);
						c.drawText(text, w/2- offset, h/2, p);

					}
				} finally {
					if (c != null)
						holder.unlockCanvasAndPost(c);
				}
				mHandler.removeCallbacks(mUpdateDisplay);
				if (mVisible) {
					mHandler.postDelayed(mUpdateDisplay, oneSecond);
				}
			}

			@Override
			public void onVisibilityChanged(boolean visible) {
				mVisible = visible;
				if (visible) {
					draw();
				} else {
					mHandler.removeCallbacks(mUpdateDisplay);
				}
			}

			@Override
			public void onCreate(SurfaceHolder holder) {
				super.onCreate(holder);
				mGestureDetector = new GestureDetector(HexatimeService.this, mGestureListener);
				setTouchEventsEnabled(true);
			}

			@Override
			public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				draw();
			}

			@Override
			public void onSurfaceDestroyed(SurfaceHolder holder) {
				super.onSurfaceDestroyed(holder);
				mVisible = false;
				mHandler.removeCallbacks(mUpdateDisplay);
			}

			@Override
			public void onDestroy() {
				super.onDestroy();
				mVisible = false;
				mHandler.removeCallbacks(mUpdateDisplay);
			}

			@Override 
			public void onTouchEvent(MotionEvent event) {
				super.onTouchEvent(event);
				mGestureDetector.onTouchEvent(event);

			}

			private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					Toast.makeText(getApplicationContext(), "You have double tapped",
							Toast.LENGTH_SHORT).show();
					
					return true;
				}
			};
	}
}
