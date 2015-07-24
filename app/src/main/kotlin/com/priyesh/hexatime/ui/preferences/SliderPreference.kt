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
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import com.priyesh.hexatime.R
import kotlin.properties.Delegates

public class SliderPreference(val title: String, val key: String, context: Context) :
        AlertDialog.Builder(context) {

    private var progressLabel: TextView by Delegates.notNull()

    init {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val view = LayoutInflater.from(context).inflate(R.layout.slider_preference_dialog, null)
        setView(view)

        progressLabel = view.findViewById(R.id.progress_label) as TextView
        val slider = view.findViewById(R.id.seekbar) as SeekBar

        val currentProgress = prefs.getInt(key, 50)
        updateProgressLabel(currentProgress)
        slider.setProgress(currentProgress)

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateProgressLabel(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        setTitle(title)
        setNegativeButton("Cancel", { dialog, id -> dialog.dismiss() })
        setPositiveButton("OK", { dialog, ide ->
            prefs.edit().putInt(key, slider.getProgress()).commit()
        })
    }

    private fun updateProgressLabel(progress: Int): Unit {
        progressLabel.setText("${progress}%")
    }

    public fun display(): Unit {
        create().show()
    }
}