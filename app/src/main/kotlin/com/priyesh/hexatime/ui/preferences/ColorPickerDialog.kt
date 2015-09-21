/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.priyesh.hexatime.ui.preferences

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.priyesh.hexatime.KEY_CUSTOM_COLOR
import com.priyesh.hexatime.R
import kotlin.properties.Delegates

public class ColorPickerDialog(context: Context) : AlertDialog.Builder(context),
        SeekBar.OnSeekBarChangeListener {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val view = LayoutInflater.from(context).inflate(R.layout.color_picker_dialog, null)

    private fun findView<T>(id: Int) = view.findViewById(id) as T

    private val colorView by Delegates.lazy { findView<View>(R.id.color_view) }

    private val sliderR by Delegates.lazy { findView<SeekBar>(R.id.seekbar_red) }
    private val sliderG by Delegates.lazy { findView<SeekBar>(R.id.seekbar_green) }
    private val sliderB by Delegates.lazy { findView<SeekBar>(R.id.seekbar_blue) }

    private val progressLabelR by Delegates.lazy { findView<TextView>(R.id.progress_label_red) }
    private val progressLabelG by Delegates.lazy { findView<TextView>(R.id.progress_label_green) }
    private val progressLabelB by Delegates.lazy { findView<TextView>(R.id.progress_label_blue) }

    private val slidersToProgress: Map<SeekBar, TextView> = mapOf(
            sliderR to progressLabelR,
            sliderG to progressLabelG,
            sliderB to progressLabelB
    )

    init {
        setView(view)

        val colorFromPrefs = prefs.getInt(KEY_CUSTOM_COLOR, Color.parseColor("#333333"))
        val r = Color.red(colorFromPrefs)
        val g = Color.green(colorFromPrefs)
        val b = Color.blue(colorFromPrefs)

        colorView.setBackgroundColor(colorFromPrefs)
        listOf(sliderR to r, sliderG to g, sliderB to b) forEach {
            val slider = it.first
            val value = it.second

            slidersToProgress.get(slider)?.setText(value.toString())
            slider.setProgress(value)
            slider.setOnSeekBarChangeListener(this)
        }

        setNegativeButton("Cancel", { dialog, id -> dialog.dismiss() })
        setPositiveButton("OK", { dialog, id -> save()})
    }

    private fun save(): Unit = prefs.edit().putInt(KEY_CUSTOM_COLOR, getCurrentColor()).apply()

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        slidersToProgress.get(seekBar)?.setText(progress.toString())
        updateColorView()
    }

    private fun updateColorView(): Unit = colorView.setBackgroundColor(getCurrentColor())

    private fun getCurrentColor(): Int = Color.rgb(
            sliderR.getProgress(),
            sliderG.getProgress(),
            sliderB.getProgress())

    public fun display(): Unit = create().show()
}