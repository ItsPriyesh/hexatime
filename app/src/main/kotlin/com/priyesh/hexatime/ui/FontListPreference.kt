/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.priyesh.hexatime.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.preference.ListPreference
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.priyesh.hexatime.CustomTypefaceSpan

import com.priyesh.hexatime.R
import kotlin.properties.Delegates

public class FontListPreference : ListPreference, AdapterView.OnItemClickListener {

    private var entryIndexNum: Int by Delegates.notNull()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { }

    constructor(context: Context) : super(context) { }

    override fun onCreateDialogView(): View {
        val assets = getContext().getAssets()
        val fonts = arrayOf("Lato", "Roboto", "Advent Pro")
        val spannables = Array<Spannable>(fonts.size(), { i ->
            val spannable = SpannableString(fonts[i])
            spannable.setSpan(CustomTypefaceSpan("sans-serif",
                    Typeface.createFromAsset(assets, "${fonts[i]}.ttf")), 0,
                    fonts[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable
        })

        setEntries(spannables)
        setEntryValues(fonts)

        val view = View.inflate(getContext(), R.layout.font_list_preference, null)
        val list = view.findViewById(android.R.id.list) as ListView
        val adapter = ArrayAdapter(getContext(), android.R.layout.simple_list_item_single_choice, getEntries())
        list.setAdapter(adapter)
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
        list.setItemChecked(findIndexOfValue(getValue()), true)
        list.setOnItemClickListener(this)

        return view
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder): Unit {
        if (getEntries() == null || getEntryValues() == null) {
            super<ListPreference>.onPrepareDialogBuilder(builder)
            return
        }

        entryIndexNum = findIndexOfValue(getValue())
        builder.setTitle(null)
        builder.setPositiveButton(null, null)
        builder.setNegativeButton(null, null)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Unit {
        entryIndexNum = position
        onClick(getDialog(), DialogInterface.BUTTON_POSITIVE)
        getDialog().dismiss()
    }

    override fun onDialogClosed(positiveResult: Boolean): Unit {
        super<ListPreference>.onDialogClosed(positiveResult)

        if (positiveResult && entryIndexNum >= 0 && getEntryValues() != null) {
            val value = getEntryValues()[entryIndexNum].toString()
            if (callChangeListener(value)) {
                setValue(value)
            }
        }
    }
}