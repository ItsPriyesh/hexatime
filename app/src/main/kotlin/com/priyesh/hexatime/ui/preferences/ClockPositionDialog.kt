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
import com.priyesh.hexatime.KEY_CLOCK_POSITION_X
import com.priyesh.hexatime.KEY_CLOCK_POSITION_Y
import com.priyesh.hexatime.R
import kotlin.properties.Delegates

public class ClockPositionDialog(context: Context) : AlertDialog.Builder(context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    private var sliderX: SeekBar by Delegates.notNull()
    private var sliderY: SeekBar by Delegates.notNull()

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.clock_position_dialog, null)
        setView(view)

        sliderX = view.findViewById(R.id.horizontal_seekbar) as SeekBar
        sliderY = view.findViewById(R.id.vertical_seekbar) as SeekBar

        sliderX.setProgress(prefs.getInt(KEY_CLOCK_POSITION_X, 50))
        sliderY.setProgress(prefs.getInt(KEY_CLOCK_POSITION_Y, 50))

        view.findViewById(R.id.center_horizontal).setOnClickListener({ sliderX.center() })
        view.findViewById(R.id.center_vertical).setOnClickListener({ sliderY.center() })

        setTitle("Clock position")
        setNegativeButton("Cancel", { dialog, id -> dialog.dismiss() })
        setPositiveButton("OK", { dialog, ide ->
            prefs.edit().putInt(KEY_CLOCK_POSITION_X, sliderX.getProgress()).commit()
            prefs.edit().putInt(KEY_CLOCK_POSITION_Y, sliderY.getProgress()).commit()
        })
    }

    private fun SeekBar.center() {
        setProgress(getMax() / 2)
    }

    public fun display(): Unit =
            create().show()
}