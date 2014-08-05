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

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.preference.ListPreference;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class FontPickerDialog extends ListPreference {
	
	public FontPickerDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    protected View onCreateDialogView() {

        String font1 = getContext().getResources().getString(R.string.lato_regular);
        String font2 = getContext().getResources().getString(R.string.lato_light);
        String font3 = getContext().getResources().getString(R.string.roboto_regular);
        String font4 = getContext().getResources().getString(R.string.roboto_light);
        String font5 = getContext().getResources().getString(R.string.robotoslab_regular);
        String font6 = getContext().getResources().getString(R.string.robotoslab_light);

        Spannable ss1 = new SpannableString(font1);
        ss1.setSpan(new CustomTypefaceSpan("sans-serif",Typeface.createFromAsset(getContext().getAssets(), "Lato.ttf")),
        			0, font1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        Spannable ss2 = new SpannableString(font2);
        ss2.setSpan(new CustomTypefaceSpan("sans-serif",Typeface.createFromAsset(getContext().getAssets(), "LatoLight.ttf")),
        			0, font2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        Spannable ss3 = new SpannableString(font3);
        ss3.setSpan(new CustomTypefaceSpan("sans-serif",Typeface.createFromAsset(getContext().getAssets(), "Roboto.ttf")),
        			0, font3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        Spannable ss4 = new SpannableString(font4);
        ss4.setSpan(new CustomTypefaceSpan("sans-serif",Typeface.createFromAsset(getContext().getAssets(), "RobotoLight.ttf")),
        			0, font4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        Spannable ss5 = new SpannableString(font5);
        ss5.setSpan(new CustomTypefaceSpan("sans-serif",Typeface.createFromAsset(getContext().getAssets(), "RobotoSlab.ttf")),
        			0, font5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        Spannable ss6 = new SpannableString(font6);
        ss6.setSpan(new CustomTypefaceSpan("sans-serif",Typeface.createFromAsset(getContext().getAssets(), "RobotoSlabLight.ttf")),
        			0, font6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        CharSequence[] entries = {ss1, ss2, ss3, ss4, ss5, ss6};
		CharSequence[] entryValues = { "0", "1", "2", "3", "4", "5"};
        setEntries(entries);
        setEntryValues(entryValues);

        return null;
    }
    
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton(null,null);
		builder.setTitle(null);		
	}
}
