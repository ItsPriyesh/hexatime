/*
 * Copyright (C) 2014 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.priyesh.hexatime;

import java.util.Calendar;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class HexatimeService extends WallpaperService{

	private static final String TAG = "Wallpaper";
	public static final String SHARED_PREFS_NAME="hexatime_settings";

	public int oneSecond = 1000;
	public int day, hour, twelveHour, min, sec;
	public Calendar cal;
	private SharedPreferences mPrefs = null;
	public int horizontalClockOffset;

	private int fontStyleValue = 1;
	private Typeface fontStyle;

	private int clockSizeValue = 1;
	private int clockSize;

	private float clockHorizontalAlignment;
	private float clockVerticalAlignment;

	private int clockAddonsValue = 1;
	private String clockAddons;

	private int separatorStyleValue = 1;
	private String separatorStyle;

	private int clockVisibilityValue = 0;

	private int timeFormatValue = 1;
	private int colorRangeValue = 0;
	private int amountToDim;

	private boolean enableImageOverlayValue;
	private int imageOverlayValue;
	private int imageOverlay;
	private int imageOverlayOpacity;
	private int imageOverlayScale;

	@Override
	public Engine onCreateEngine() {
		return new HexatimeEngine(this);
	}

	private class HexatimeEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

		private boolean mVisible = false;
		private final Handler mHandler = new Handler();

		private Canvas c;
		private Paint hexClock, bg, dimLayer, imageLayer;

		private final Runnable mUpdateDisplay = new Runnable() {

			@Override
			public void run() {
				draw();
			}};

			HexatimeEngine(WallpaperService ws) {
				mPrefs = HexatimeService.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
				mPrefs.registerOnSharedPreferenceChangeListener(this);
				onSharedPreferenceChanged(mPrefs, null);
			}

			private void draw() {
				cal = Calendar.getInstance();
				day = cal.get(Calendar.DAY_OF_YEAR) - 1;
				hour = cal.get(Calendar.HOUR_OF_DAY);
				twelveHour = cal.get(Calendar.HOUR);
				min = cal.get(Calendar.MINUTE);
				sec = cal.get(Calendar.SECOND);

				SurfaceHolder holder = getSurfaceHolder();
				c = null;
				try {
					c = holder.lockCanvas();
					if (c != null) {
						hexClock = new Paint();
						bg = new Paint();

						hexClock.setTextSize(clockSize);
						hexClock.setTypeface(fontStyle);
						hexClock.setColor(Color.WHITE);
						hexClock.setAntiAlias(true);

						String hexTime;
						if (timeFormatValue == 0){
							hexTime = String.format(clockAddons, twelveHour, min, sec); 
						}
						else {
							hexTime = String.format(clockAddons, hour, min, sec);
						}
						String hexValue;
						int red=0, green=0, blue=0;
						float d = hexClock.measureText(hexTime, 0, hexTime.length());
						horizontalClockOffset = (int) d / 2;
						int w = c.getWidth();
						int h = c.getHeight();

						if(colorRangeValue == 1) {
							Double tempTime = ( ( day * 86400 ) + ( hour * 3600 ) + ( min * 60 ) + sec ) * 0.53200202942669;
							hexValue = String.format("%6s", Integer.toHexString(tempTime.intValue())).replace(" ", "0");
							red = Integer.parseInt(hexValue.substring(0, 2), 16);
							green = Integer.parseInt(hexValue.substring(2, 4), 16);
							blue = Integer.parseInt(hexValue.substring(4, 6), 16);
						} else {
							hexValue = String.format("%02d%02d%02d", hour, min, sec);
							red = hour;
							green = min;
							blue = sec;
						}

						bg.setColor(Color.argb(255, red, green, blue));
						c.drawRect(0, 0, w, h, bg);

						dimLayer = new Paint();
						dimLayer.setColor(Color.BLACK);
						dimLayer.setAlpha(amountToDim);
						c.drawRect(0, 0, w, h, dimLayer);

						if (enableImageOverlayValue) {
							imageLayer = new Paint();							
							Bitmap initialOverlay = BitmapFactory.decodeResource(getResources(), imageOverlay);
							Bitmap overlayScaled = Bitmap.createScaledBitmap(initialOverlay, imageOverlayScale, imageOverlayScale, false);
							
							BitmapDrawable imageOverlay = new BitmapDrawable (overlayScaled); 
							imageOverlay.setTileModeX(Shader.TileMode.REPEAT); 
							imageOverlay.setTileModeY(Shader.TileMode.REPEAT);
							imageOverlay.setAlpha(imageOverlayOpacity);
							imageOverlay.setBounds(0, 0, w, h);

							imageOverlay.draw(c);				       
							imageOverlay.getBitmap().recycle();
						}						

						if (clockVisibilityValue == 0){
							c.drawText(hexTime, clockHorizontalAlignment, clockVerticalAlignment, hexClock);
						}
						else if (clockVisibilityValue == 1) {
							KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
							boolean lockscreenShowing = km.inKeyguardRestrictedInputMode();
							if(!lockscreenShowing){  
								c.drawText(hexTime, clockHorizontalAlignment, clockVerticalAlignment, hexClock);
							}
						}
						else if (clockVisibilityValue == 2) {
							// Don't draw the clock...ever
						}
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
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				if(key != null){
					if(key.equals("FONT_STYLE")){
						changeFontStyle(prefs.getString("FONT_STYLE", "1"));
					}
					else if(key.equals("CLOCK_SIZE")){
						changeClockSize(prefs.getString("CLOCK_SIZE", "1"));
					}
					else if(key.equals("CLOCK_VERTICAL_ALIGNMENT")){
						changeClockVerticalAlignment(prefs.getFloat("CLOCK_VERTICAL_ALIGNMENT", 0.5f));
					}
					else if(key.equals("CLOCK_HORIZONTAL_ALIGNMENT")){
						changeClockHorizontalAlignment(prefs.getFloat("CLOCK_HORIZONTAL_ALIGNMENT", 0.5f));
					}
					else if(key.equals("CLOCK_VISIBILITY")){
						changeClockVisibility(prefs.getString("CLOCK_VISIBILITY", "0"));
					}
					else if(key.equals("TIME_FORMAT")){
						changeTimeFormat(prefs.getString("TIME_FORMAT", "1"));
					}
					else if(key.equals("COLOR_RANGE")){
						changeColorRange(prefs.getString("COLOR_RANGE", "0"));
					}
					else if(key.equals("CLOCK_ADDONS")){
						changeClockAddons(prefs.getString("CLOCK_ADDONS", "1"));
					}
					else if(key.equals("SEPARATOR_STYLE")){
						changeSeparatorStyle(prefs.getString("SEPARATOR_STYLE", "1"));
					}
					else if(key.equals("DIM_BACKGROUND")){
						changeDimBackground(prefs.getFloat("DIM_BACKGROUND", 0.0f));
					}
					else if(key.equals("ENABLE_IMAGE_OVERLAY")){
						enableImageOverlay(prefs.getBoolean("ENABLE_IMAGE_OVERLAY", false));
					}
					else if(key.equals("IMAGE_OVERLAY")){
						changeImageOverlay(prefs.getString("IMAGE_OVERLAY", "0"));
					}
					else if(key.equals("IMAGE_OVERLAY_OPACITY")){
						changeImageOverlayOpacity(prefs.getFloat("IMAGE_OVERLAY_OPACITY", 0.5f));
					}
					else if(key.equals("IMAGE_OVERLAY_SCALE")){
						changeImageOverlayScale(prefs.getFloat("IMAGE_OVERLAY_SCALE", 0.5f));
					}
					
				}
				else {	                        
					changeFontStyle(prefs.getString("FONT_STYLE", "1"));
					changeClockSize(prefs.getString("CLOCK_SIZE", "1"));
					changeClockVerticalAlignment(prefs.getFloat("CLOCK_VERTICAL_ALIGNMENT", 0.5f));
					changeClockHorizontalAlignment(prefs.getFloat("CLOCK_HORIZONTAL_ALIGNMENT", 0.5f));
					changeClockVisibility(prefs.getString("CLOCK_VISIBILITY", "0"));
					changeTimeFormat(prefs.getString("TIME_FORMAT", "1"));
					changeColorRange(prefs.getString("COLOR_RANGE", "0"));
					changeClockAddons(prefs.getString("CLOCK_ADDONS", "1"));
					changeSeparatorStyle(prefs.getString("SEPARATOR_STYLE", "1"));
					changeDimBackground(prefs.getFloat("DIM_BACKGROUND", 0.0f)); 
					enableImageOverlay(prefs.getBoolean("ENABLE_IMAGE_OVERLAY", false));
					changeImageOverlay(prefs.getString("IMAGE_OVERLAY", "0"));
					changeImageOverlayOpacity(prefs.getFloat("IMAGE_OVERLAY_OPACITY", 0.5f));
					changeImageOverlayScale(prefs.getFloat("IMAGE_OVERLAY_SCALE", 0.5f));
				}
				return;
			}

			private void changeFontStyle(String value){
				fontStyleValue = Integer.parseInt(value);
				if(fontStyleValue == 0){ 
					fontStyle = Typeface.createFromAsset(getAssets(), "Lato.ttf");
				}
				else if (fontStyleValue == 1){
					fontStyle = Typeface.createFromAsset(getAssets(), "LatoLight.ttf");                    
				}
				else if (fontStyleValue == 2){
					fontStyle = Typeface.createFromAsset(getAssets(), "Roboto.ttf");                    
				}
				else if (fontStyleValue == 3){
					fontStyle = Typeface.createFromAsset(getAssets(), "RobotoLight.ttf");                    
				}
				else if (fontStyleValue == 4){
					fontStyle = Typeface.createFromAsset(getAssets(), "RobotoSlab.ttf");                    
				}
				else if (fontStyleValue == 5){
					fontStyle = Typeface.createFromAsset(getAssets(), "RobotoSlabLight.ttf");                    
				}
			}

			private void changeClockSize(String value){
				clockSizeValue = Integer.parseInt(value);
				if(clockSizeValue == 0){
					clockSize = (getResources().getDimensionPixelSize(R.dimen.clockFontSizeSmall));
				}
				else if (clockSizeValue == 1){
					clockSize = (getResources().getDimensionPixelSize(R.dimen.clockFontSizeMed));
				}
				else if (clockSizeValue == 2){
					clockSize = (getResources().getDimensionPixelSize(R.dimen.clockFontSizeLarge));
				}
			}

			private void changeClockVerticalAlignment(float value){
				WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int h = size.y;				
				clockVerticalAlignment = h - (h * value);
			}

			private void changeClockHorizontalAlignment(float value){
				WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int w = size.x;
				clockHorizontalAlignment = (w * value) - horizontalClockOffset;
			}

			private void changeClockAddons(String value){
				clockAddonsValue = Integer.parseInt(value);
				if(clockAddonsValue == 0){
					clockAddons = "%02d%02d%02d";
				}
				else if (clockAddonsValue == 1){ 
					clockAddons = "#%02d%02d%02d";
				}
				else if (clockAddonsValue == 2){ 
					clockAddons = "%02d" + separatorStyle + "%02d" + separatorStyle + "%02d";
				}
				else if (clockAddonsValue == 3){ 
					clockAddons = "#%02d" + separatorStyle + "%02d" + separatorStyle + "%02d";
				}
			}

			private void changeSeparatorStyle(String value){
				separatorStyleValue = Integer.parseInt(value);
				if(separatorStyleValue == 0){ 
					separatorStyle = ":";
					changeClockAddons(mPrefs.getString("CLOCK_ADDONS", "1"));
				}
				else if (separatorStyleValue == 1){
					separatorStyle = " ";
					changeClockAddons(mPrefs.getString("CLOCK_ADDONS", "1"));
				}
				else if (separatorStyleValue == 2){
					separatorStyle = ".";
					changeClockAddons(mPrefs.getString("CLOCK_ADDONS", "1"));
				}
				else if (separatorStyleValue == 3){
					separatorStyle = "|";
					changeClockAddons(mPrefs.getString("CLOCK_ADDONS", "1"));
				}
				else if (separatorStyleValue == 4){
					separatorStyle = "/";
					changeClockAddons(mPrefs.getString("CLOCK_ADDONS", "1"));
				}
			}

			private void changeClockVisibility(String value){
				clockVisibilityValue = Integer.parseInt(value);
			}

			private void changeTimeFormat(String value){
				timeFormatValue = Integer.parseInt(value);
			}

			private void changeColorRange(String value){
				colorRangeValue = Integer.parseInt(value);
			}

			private void changeDimBackground(float value){
				amountToDim = (int) (value * 255);
			}

			private void enableImageOverlay(boolean value){
				enableImageOverlayValue = value;
			}

			private void changeImageOverlay(String value){
				imageOverlayValue = Integer.parseInt(value);
				if (imageOverlayValue == 0){
					imageOverlay = R.drawable.hex;
				}
				else if (imageOverlayValue == 1){
					imageOverlay = R.drawable.grid;
				}
				else if (imageOverlayValue == 2){
					imageOverlay = R.drawable.dots;
				}
				else if (imageOverlayValue == 3){
					imageOverlay = R.drawable.circles;
				}
				
			}
			private void changeImageOverlayOpacity(float value){
				imageOverlayOpacity = (int) (value * 255);
			}

			private void changeImageOverlayScale(float value){
				imageOverlayScale = (int) (500 * value);
				if (imageOverlayScale == 0) {
					imageOverlayScale = 1;
				}
			}
	}
}
