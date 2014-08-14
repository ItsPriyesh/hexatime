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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
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

	private int fontStyleValue = 1;
	private Typeface fontStyle;

	private int clockSizeValue = 1; // 0 = small, 1 = medium, 2 = large
	private int clockSize;

	private int clockAlignmentValue = 1; // 0 = top, 1 = center, 2 = bottom
	private float clockAlignment;

	private String clockAddons;
	private int clockAddonsValue = 1;

	private String separatorStyle;
	private int separatorStyleValue = 1;

	private int clockVisibilityValue = 0;

	private int timeFormatValue = 1;

	private int colorRangeValue = 0; // 0 = day, 1 = year
	
	private int amountToDim = 0;

	@Override
	public Engine onCreateEngine() {
		return new HexatimeEngine(this);
	}

	private class HexatimeEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

		private boolean mVisible = false;
		private final Handler mHandler = new Handler();

		private Canvas c;
		private Paint hexClock, bg, dimLayer;

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
						int offset = (int) d / 2;
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
						
						KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
						boolean lockscreenShowing = km.inKeyguardRestrictedInputMode();

						if (clockVisibilityValue == 0){
							c.drawText(hexTime, w/2- offset, clockAlignment, hexClock);
						}
						else if (clockVisibilityValue == 1) {
							if(!lockscreenShowing){  
								c.drawText(hexTime, w/2- offset, clockAlignment, hexClock);
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
					else if(key.equals("CLOCK_ALIGNMENT")){
						changeClockAlignment(prefs.getString("CLOCK_ALIGNMENT", "1"));
					}
					else if(key.equals("CLOCK_VISIBILITY")){
						changeClockVisibility(prefs.getString("CLOCK_VISIBILITY", "1"));
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
				}
				else {	                        
					changeFontStyle(prefs.getString("FONT_STYLE", "1"));
					changeClockSize(prefs.getString("CLOCK_SIZE", "1"));
					changeClockAlignment(prefs.getString("CLOCK_ALIGNMENT", "1"));
					changeClockVisibility(prefs.getString("CLOCK_VISIBILITY", "0"));
					changeTimeFormat(prefs.getString("TIME_FORMAT", "1"));
					changeColorRange(prefs.getString("COLOR_RANGE", "0"));
					changeClockAddons(prefs.getString("CLOCK_ADDONS", "1"));
					changeSeparatorStyle(prefs.getString("SEPARATOR_STYLE", "1"));
					changeDimBackground(prefs.getFloat("DIM_BACKGROUND", 0.0f)); 
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
				return;
			}

			private void changeClockSize(String value){
				clockSizeValue = Integer.parseInt(value);
				if(clockSizeValue == 0){ // small
					clockSize = (getResources().getDimensionPixelSize(R.dimen.clockFontSizeSmall));
				}
				else if (clockSizeValue == 1){ // medium
					clockSize = (getResources().getDimensionPixelSize(R.dimen.clockFontSizeMed));
				}
				else if (clockSizeValue == 2){ //large
					clockSize = (getResources().getDimensionPixelSize(R.dimen.clockFontSizeLarge));
				}
				return;
			}

			private void changeClockAlignment(String value){
				clockAlignmentValue = Integer.parseInt(value);				
				WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int h = size.y;

				if(clockAlignmentValue == 0){ // top
					clockAlignment = (float) (h - (h*0.65));
				}
				else if (clockAlignmentValue == 1){ // center
					clockAlignment = (float) (h - (h*0.50));
				}
				else if (clockAlignmentValue == 2){ //bottom
					clockAlignment = (float) (h - (h*0.35));
				}
				return;
			}

			private void changeClockAddons(String value){
				clockAddonsValue = Integer.parseInt(value);
				if(clockAddonsValue == 0){ // hide #
					clockAddons = "%02d%02d%02d";
				}
				else if (clockAddonsValue == 1){ // show #
					clockAddons = "#%02d%02d%02d";
				}
				else if (clockAddonsValue == 2){ // show separator
					clockAddons = "%02d"+separatorStyle+"%02d"+separatorStyle+"%02d";
				}
				else if (clockAddonsValue == 3){ // # and separator
					clockAddons = "#%02d"+separatorStyle+"%02d"+separatorStyle+"%02d";
				}
				return;
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
				return;
			}

			private void changeClockVisibility(String value){
				clockVisibilityValue = Integer.parseInt(value);
				return;
			}
			private void changeTimeFormat(String value){
				timeFormatValue = Integer.parseInt(value);
				return;
			}

			private void changeColorRange(String value){
				colorRangeValue = Integer.parseInt(value);
				return;		
			}
			
			private void changeDimBackground(Float value){
				amountToDim = (int) (value * 255);
			}
	}
}
