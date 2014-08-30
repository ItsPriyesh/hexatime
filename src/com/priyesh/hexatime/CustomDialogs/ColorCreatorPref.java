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

package com.priyesh.hexatime.CustomDialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.priyesh.hexatime.R;

public class ColorCreatorPref extends DialogPreference {

	protected final static int SEEKBAR_RESOLUTION = 10000;
	protected float mValue;
	protected int mSeekBarValue;
	SeekBar redSeekbar, greenSeekbar, blueSeekbar;
	TextView redSeekbarProgress, greenSeekbarProgress, blueSeekbarProgress;
	EditText colorCode;
	ImageView colorSample;
	
	public ColorCreatorPref(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
		setDialogLayoutResource(R.layout.color_creator);
	}

	@Override
	protected View onCreateDialogView() {
		
		mSeekBarValue = (int) (mValue * SEEKBAR_RESOLUTION);
		View view = super.onCreateDialogView();
		
		colorCode = (EditText)view.findViewById(R.id.color_code_et);
		
		SharedPreferences sharedPreferences = getSharedPreferences();
	    String colorCodeInPrefs = sharedPreferences.getString("SET_CUSTOM_COLOR", "000000");
		colorCode.setText(colorCodeInPrefs);
		
		colorSample = (ImageView)view.findViewById(R.id.color_sample);
		colorSample.setImageDrawable(new ColorDrawable (Color.parseColor("#" + colorCode.getText().toString())));
		
		redSeekbar = (SeekBar) view.findViewById(R.id.red_seekbar);
		redSeekbar.setMax(SEEKBAR_RESOLUTION);
		redSeekbar.setProgress((int) ((Integer.parseInt(colorCode.getText().toString().substring(0,2), 16)) * 39.2156862745));
		
		redSeekbarProgress = (TextView) view.findViewById(R.id.red_seekbar_progress);
		redSeekbarProgress.setText((Integer.parseInt(colorCode.getText().toString().substring(0,2), 16) + ""));
		
		redSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					ColorCreatorPref.this.mSeekBarValue = progress;
					
					redSeekbarProgress.setText((int) (mSeekBarValue/39.2156862745) + "");
					
					String old = colorCode.getText().toString();
					String rValHex = String.format("%02X",(Integer.parseInt(redSeekbarProgress.getText().toString())));
					String rVal = rValHex + old.substring(2, 6);
					colorCode.setText(rVal);
					
					colorSample.setImageDrawable(new ColorDrawable (Color.parseColor("#" + colorCode.getText().toString())));
				}
			}
		});
		
		greenSeekbar = (SeekBar) view.findViewById(R.id.green_seekbar);
		greenSeekbar.setMax(SEEKBAR_RESOLUTION);
		greenSeekbar.setProgress((int) ((Integer.parseInt(colorCode.getText().toString().substring(2,4), 16)) * 39.2156862745));
		
		greenSeekbarProgress = (TextView) view.findViewById(R.id.green_seekbar_progress);
		greenSeekbarProgress.setText((Integer.parseInt(colorCode.getText().toString().substring(2,4), 16) + ""));
		
		greenSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					ColorCreatorPref.this.mSeekBarValue = progress;

					greenSeekbarProgress.setText((int) (mSeekBarValue/39.2156862745) + "");
					
					String old = colorCode.getText().toString();
					String gValHex = String.format("%02X",(Integer.parseInt(greenSeekbarProgress.getText().toString())));
					String gVal = old.substring(0, 2) + gValHex + old.substring(4, 6);
					colorCode.setText(gVal);
					
					colorSample.setImageDrawable(new ColorDrawable (Color.parseColor("#" + colorCode.getText().toString())));
				}
			}
		});
		
		blueSeekbar = (SeekBar) view.findViewById(R.id.blue_seekbar);
		blueSeekbar.setMax(SEEKBAR_RESOLUTION);
		blueSeekbar.setProgress((int) ((Integer.parseInt(colorCode.getText().toString().substring(4,6), 16)) * 39.2156862745));
		
		blueSeekbarProgress = (TextView) view.findViewById(R.id.blue_seekbar_progress);
		blueSeekbarProgress.setText((Integer.parseInt(colorCode.getText().toString().substring(4,6), 16) + ""));
		
		blueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					ColorCreatorPref.this.mSeekBarValue = progress;
					
					blueSeekbarProgress.setText((int) (mSeekBarValue/39.2156862745) + "");
					
					String old = colorCode.getText().toString();
					String bValHex = String.format("%02X",(Integer.parseInt(blueSeekbarProgress.getText().toString())));
					String bVal = old.substring(0, 4) + bValHex;
					colorCode.setText(bVal);
					
					colorSample.setImageDrawable(new ColorDrawable (Color.parseColor("#" + colorCode.getText().toString())));
				}

			}
		});
		
		colorCode.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {}
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){
	        	// Disable stuff if the hex code is invalid
	        	if (!(s.toString().matches("[A-Fa-f0-9]{6}"))) {
	        		AlertDialog dialog = (AlertDialog) getDialog();
	        		Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
	        		positiveButton.setEnabled(false);	        		
	        		redSeekbar.setEnabled(false);
	        		greenSeekbar.setEnabled(false);
	        		blueSeekbar.setEnabled(false);
				}
	        	else {
	        		AlertDialog dialog = (AlertDialog) getDialog();
	        		Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
	        		positiveButton.setEnabled(true);
	        		redSeekbar.setEnabled(true);
	        		greenSeekbar.setEnabled(true);
	        		blueSeekbar.setEnabled(true);
	        	}
	        	
	        	// Updates view when user types in a custom hex code
	        	if (s.toString().length() == 6){
					colorSample.setImageDrawable(new ColorDrawable (Color.parseColor("#" + colorCode.getText().toString())));
					
					redSeekbar.setProgress((int) ((Integer.parseInt(colorCode.getText().toString().substring(0,2), 16)) * 39.2156862745));
					greenSeekbar.setProgress((int) ((Integer.parseInt(colorCode.getText().toString().substring(2,4), 16)) * 39.2156862745));
					blueSeekbar.setProgress((int) ((Integer.parseInt(colorCode.getText().toString().substring(4,6), 16)) * 39.2156862745));

					redSeekbarProgress.setText(Integer.parseInt(colorCode.getText().toString().substring(0,2), 16) + "");
					greenSeekbarProgress.setText(Integer.parseInt(colorCode.getText().toString().substring(2,4), 16) + "");	
					blueSeekbarProgress.setText(Integer.parseInt(colorCode.getText().toString().substring(4,6), 16) + "");	
				}
	        }
	    }); 
		
		// Close dialog
		Button negativeButton = (Button) view.findViewById(R.id.negative_button);
		negativeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
		        getDialog().dismiss();
			}			
		});
		
		// Save new color into SharedPreferences
		Button positiveButton = (Button) view.findViewById(R.id.positive_button);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
		        Editor editor = getEditor();
		        editor.putString("SET_CUSTOM_COLOR", colorCode.getText().toString());
		        editor.commit();
		        getDialog().dismiss();
			}			
		});
		
		return view;
	}
	
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setPositiveButton(null,null);
		builder.setNegativeButton(null,null);
		builder.setTitle(null);
	}
	
}
