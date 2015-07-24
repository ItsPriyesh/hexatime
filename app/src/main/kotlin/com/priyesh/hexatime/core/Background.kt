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

package com.priyesh.hexatime.core

import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import com.priyesh.hexatime.KEY_BACKGROUND_BRIGHTNESS
import com.priyesh.hexatime.KEY_BACKGROUND_SATURATION
import com.priyesh.hexatime.KEY_COLOR_MODE
import com.priyesh.hexatime.log

public class Background(clock: Clock) : PreferenceDelegate {

    private val clock = clock
    private var colorMode = 0
    private var saturation: Float = 0.5f
    private var brightness: Float = 0.5f

    private fun rgbEnabled() = colorMode == 0

    init {
        initializeFromPrefs(PreferenceManager.getDefaultSharedPreferences(clock.getContext()))
    }

    private fun initializeFromPrefs(prefs: SharedPreferences) {
        val keys = arrayOf(KEY_COLOR_MODE, KEY_BACKGROUND_SATURATION, KEY_BACKGROUND_BRIGHTNESS)
        for (key in keys) onPreferenceChange(prefs, key)
    }

    override fun onPreferenceChange(prefs: SharedPreferences, key: String) {
        when (key) {
            KEY_COLOR_MODE -> colorMode = prefs.getString(KEY_COLOR_MODE, "0").toInt()
            KEY_BACKGROUND_SATURATION -> saturation = (prefs.getInt(KEY_BACKGROUND_SATURATION, 50) / 100.0).toFloat()
            KEY_BACKGROUND_BRIGHTNESS -> brightness = (prefs.getInt(KEY_BACKGROUND_BRIGHTNESS, 50) / 100.0).toFloat()
        }
    }

    public fun getColor(): Int = if (rgbEnabled()) getRGBColor() else getHSLColor()

    private fun getRGBColor() = clock.getColor()
    private fun getHSLColor() = colorFromHSV(clock.getHue(), saturation, brightness)

    private fun colorFromHSV(vararg i: Float) = Color.HSVToColor(floatArrayOf(i[0], i[1], i[2]))

}