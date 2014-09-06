/* 
 * AndroidSliderPreference Library originally Copyright (C) 2012 Jay Weisskopf
 * and licensed under the MIT license:
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 *==============================================================================
 * 
 * Additions to AndroidSliderPreference Library Copyright (C) 2014 Priyesh Patel
 * and licensed under Apache License, Version 2.0:
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

import com.priyesh.hexatime.R;
import com.priyesh.hexatime.R.id;
import com.priyesh.hexatime.R.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class VerticalAlignSeekbarPref extends DialogPreference {

	protected final static int SEEKBAR_RESOLUTION = 10000;

	protected float mValue;
	protected int mSeekBarValue;
	protected CharSequence[] mSummaries;
	SeekBar seekbar;
	TextView seekbarProgress;
	
	public VerticalAlignSeekbarPref(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.vertical_align_seekbar);
	}

	@Override
	protected View onCreateDialogView() {
		mSeekBarValue = (int) (mValue * SEEKBAR_RESOLUTION);
		View view = super.onCreateDialogView();
		
		SharedPreferences sharedPreferences = getSharedPreferences();
		float vAlignFloat = sharedPreferences.getFloat("CLOCK_VERTICAL_ALIGNMENT", 0.5f);
	    int verticalAlignInPrefs = (int) (vAlignFloat * SEEKBAR_RESOLUTION);
	    
		seekbar = (SeekBar) view.findViewById(R.id.slider_preference_seekbar);
		seekbar.setMax(SEEKBAR_RESOLUTION);
		seekbar.setProgress(verticalAlignInPrefs);
		
		seekbarProgress = (TextView) view.findViewById(R.id.seekbar_progress);
		seekbarProgress.setText((verticalAlignInPrefs/100) + "%");
		
		Button leftButton = (Button) view.findViewById(R.id.bottomButton);
		leftButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	mSeekBarValue = (int) (0.35 * SEEKBAR_RESOLUTION);
		    	seekbar.setProgress(mSeekBarValue);
		    }
		});
		Button centerButton = (Button) view.findViewById(R.id.vCenterButton);
		centerButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	mSeekBarValue = (int) (0.5 * SEEKBAR_RESOLUTION);
		    	seekbar.setProgress(mSeekBarValue);
		    }
		});
		Button rightButton = (Button) view.findViewById(R.id.topButton);
		rightButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	mSeekBarValue = (int) (0.65 * SEEKBAR_RESOLUTION);
		    	seekbar.setProgress(mSeekBarValue);
		    }
		});
		
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					VerticalAlignSeekbarPref.this.mSeekBarValue = progress;					
				}
				seekbarProgress.setText((mSeekBarValue/100) + "%");			
			}
		});
		
		Button positiveButton = (Button) view.findViewById(R.id.positive_button);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				double newVal = (Integer.parseInt((String) seekbarProgress.getText().toString().subSequence(0, seekbarProgress.getText().toString().length() - 1)))/100.00;
				float newValFloat = (float) newVal;
		        Editor editor = getEditor();
		        editor.putFloat("CLOCK_VERTICAL_ALIGNMENT", newValFloat);
		        editor.commit();
		        getDialog().dismiss();
			}			
		});
		
		return view;
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton(null,null);
		builder.setPositiveButton(null,null);
		builder.setTitle(null);
	}
	
}
