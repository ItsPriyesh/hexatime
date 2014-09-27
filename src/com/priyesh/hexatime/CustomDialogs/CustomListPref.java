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

import com.priyesh.hexatime.R;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CustomListPref extends ListPreference implements OnItemClickListener {

    private int entryIndexNum;

    public CustomListPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListPref(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView() {
        View view = View.inflate(getContext(), R.layout.list_pref, null);

        ListView list = (ListView) view.findViewById(android.R.id.list);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.list_item, getEntries());
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setItemChecked(findIndexOfValue(getValue()), true);
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        if (getEntries() == null || getEntryValues() == null) {
            super.onPrepareDialogBuilder(builder);
            return;
        }

        entryIndexNum = findIndexOfValue(getValue());
        builder.setTitle(null);
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	entryIndexNum = position;
        CustomListPref.this.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
        getDialog().dismiss();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && entryIndexNum >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[entryIndexNum].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }
}