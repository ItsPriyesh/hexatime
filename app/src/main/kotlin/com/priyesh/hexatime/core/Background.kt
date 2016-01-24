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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.preference.PreferenceManager
import com.priyesh.hexatime.*
import kotlin.properties.Delegates

public class Background(clock: Clock) : PreferenceDelegate {

    private val clock = clock
    private var colorMode = 0
    private var saturation: Float = 0.5f
    private var brightness: Float = 0.5f

    private var customColorEnabled = false
    private var customColor = Color.parseColor("#333333")

    private var overlay: BitmapDrawable by Delegates.notNull()

    init { initializeFromPrefs(PreferenceManager.getDefaultSharedPreferences(clock.getContext())) }

    override fun initializeFromPrefs(prefs: SharedPreferences) {
        val keys = arrayOf(KEY_COLOR_MODE, KEY_BACKGROUND_SATURATION,
                KEY_BACKGROUND_BRIGHTNESS, KEY_BACKGROUND_OVERLAY,
                KEY_BACKGROUND_OVERLAY_OPACITY, KEY_BACKGROUND_OVERLAY_SCALE,
                KEY_ENABLE_CUSTOM_COLOR, KEY_CUSTOM_COLOR)

        for (key in keys) onPreferenceChange(prefs, key)
    }

    override fun onPreferenceChange(prefs: SharedPreferences, key: String) {
        fun getSliderValue(key: String, def: Int) = (prefs.getInt(key, def) / 100.0).toFloat()
        fun getInt(key: String) = prefs.getString(key, "0").toInt()

        when (key) {
            KEY_BACKGROUND_SATURATION -> saturation = getSliderValue(KEY_BACKGROUND_SATURATION, 50)
            KEY_BACKGROUND_BRIGHTNESS -> brightness = getSliderValue(KEY_BACKGROUND_BRIGHTNESS, 50)
            KEY_COLOR_MODE -> colorMode = getInt(KEY_COLOR_MODE)
            KEY_ENABLE_CUSTOM_COLOR -> customColorEnabled = prefs.getBoolean(KEY_ENABLE_CUSTOM_COLOR, false)
            KEY_CUSTOM_COLOR -> customColor = prefs.getInt(KEY_CUSTOM_COLOR, customColor)
            KEY_BACKGROUND_OVERLAY, KEY_BACKGROUND_OVERLAY_OPACITY, KEY_BACKGROUND_OVERLAY_SCALE -> {
                val overlayRef = getInt(KEY_BACKGROUND_OVERLAY)
                val opacity = (getSliderValue(KEY_BACKGROUND_OVERLAY_OPACITY, 10) * 255).toInt()
                val scale = Math.max(getSliderValue(KEY_BACKGROUND_OVERLAY_SCALE, 50), 0.1f)
                updateOverlay(overlayRef, opacity, scale)
            }
        }
    }

    public fun getColor(): Int = if (customColorEnabled) customColor
            else (if (rgbEnabled()) getRGBColor() else getHSBColor())

    private fun rgbEnabled() = colorMode == 0
    private fun getRGBColor() = clock.getColor()
    private fun getHSBColor() = colorFromHSB(clock.getHue(), saturation, brightness)
    private fun colorFromHSB(vararg i: Float) = Color.HSVToColor(floatArrayOf(i[0], i[1], i[2]))

    public fun getBackgroundOverlay(): BitmapDrawable = overlay

    private fun getOverlayId(i: Int) = when (i) {
        0 -> R.drawable.overlay_dots
        1 -> R.drawable.overlay_hex
        2 -> R.drawable.overlay_circles
        3 -> R.drawable.overlay_grid
        4 -> R.drawable.overlay_paisley
        5 -> R.drawable.overlay_sativa
        6 -> R.drawable.overlay_skulls
        else -> R.drawable.overlay_dots
    }

    private fun updateOverlay(overlayRef: Int, opacity: Int, scale: Float) {
        val context = clock.getContext()
        val res = context.resources
        val bitmap = BitmapFactory.decodeResource(res, getOverlayId(overlayRef))
        val bitmapScaled = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(), false);

        overlay = BitmapDrawable(res, bitmapScaled)
        overlay.tileModeX = Shader.TileMode.REPEAT
        overlay.tileModeY = Shader.TileMode.REPEAT
        overlay.alpha = opacity
        overlay.setBounds(0, 0, getScreenWidth(context), getScreenHeight(context))
    }

}