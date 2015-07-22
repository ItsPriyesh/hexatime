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

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.preference.PreferenceManager
import com.priyesh.hexatime.*
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.Calendar
import kotlin.properties.Delegates

public class Clock(context: Context) : PreferenceDelegate {

    private val context = context

    private var enable24Hour = false
    private var enableNumberSign = true
    private var dividerStyle = 0
    private var enableHexFormat = false;
    private var positionX = 50
    private var positionY = 50

    private val HOUR = Calendar.HOUR
    private val HOUR_24 = Calendar.HOUR_OF_DAY
    private val MINUTE = Calendar.MINUTE
    private val SECOND = Calendar.SECOND

    private var startOfDay = Calendar.getInstance()
    private var calendar = Calendar.getInstance()
    private var paint = Paint()
    private var canvas: Canvas by Delegates.notNull()

    private fun hour() =
            if (enable24Hour) calendar.get(HOUR_24)
            else if (calendar.get(HOUR) == 0) 12
            else calendar.get(HOUR)

    private fun minute() = calendar.get(MINUTE)
    private fun second() = calendar.get(SECOND)

    private fun numberSign() = if (enableNumberSign) "#" else ""

    private fun divider() = when (dividerStyle) {
        0 -> ""
        1 -> "."
        2 -> ":"
        3 -> " "
        4 -> "|"
        5 -> "/"
        else -> ""
    }

    init {
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        paint.setAntiAlias(true)
        paint.setTextAlign(Paint.Align.CENTER)
        paint.setColor(Color.WHITE)
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "Lato-Hairline.ttf"))

        initializeFromPrefs(PreferenceManager.getDefaultSharedPreferences(context))
    }

    private fun initializeFromPrefs(prefs: SharedPreferences) {
        enable24Hour = prefs.getBoolean(KEY_ENABLE_24_HOUR, false)
        enableNumberSign = prefs.getBoolean(KEY_ENABLE_NUMBER_SIGN, true)
        dividerStyle = prefs.getString(KEY_CLOCK_DIVIDER, "0").toInt()
        enableHexFormat = prefs.getBoolean(KEY_ENABLE_HEX_FORMAT, false)
        positionX = prefs.getInt(KEY_CLOCK_POSITION_X, 50)
        positionY = prefs.getInt(KEY_CLOCK_POSITION_Y, 50)
        updateClockSize(prefs.getString(KEY_CLOCK_SIZE, "2").toInt())
    }

    override fun onPreferenceChange(prefs: SharedPreferences, key: String) {
        when (key) {
            KEY_ENABLE_24_HOUR -> enable24Hour = prefs.getBoolean(key, false)
            KEY_ENABLE_NUMBER_SIGN -> enableNumberSign = prefs.getBoolean(key, true)
            KEY_CLOCK_DIVIDER -> dividerStyle = prefs.getString(key, "0").toInt()
            KEY_ENABLE_HEX_FORMAT -> enableHexFormat = prefs.getBoolean(key, false)
            KEY_CLOCK_SIZE -> updateClockSize(prefs.getString(key, "2").toInt())
            KEY_CLOCK_POSITION_X -> positionX = prefs.getInt(key, 50)
            KEY_CLOCK_POSITION_Y -> positionY = prefs.getInt(key, 50)
        }
    }

    private fun updateClockSize(sizeReference: Int) {
        paint.setTextSize(context.getPixels(when (sizeReference) {
            0 -> 10
            1 -> 30
            2 -> 50
            3 -> 70
            4 -> 90
            else -> 50
        }))
    }

    private fun getSecondOfDay(): Int =
            ((calendar.getTimeInMillis() - startOfDay.getTimeInMillis()) / 1000).toInt()

    public fun getColor(): Int = Color.parseColor(getHexString())
    public fun getHue(): Float = getSecondOfDay() / 240f

    public fun getHexString(): String =
            "#${formatTwoDigit(hour())}${formatTwoDigit(minute())}${formatTwoDigit(second())}"

    public fun getTime(): String {
        updateCalendar()
        fun formatter(i: Int) = if (enableHexFormat) convertToHex(i) else formatTwoDigit(i)
        return "${numberSign()}${formatter(hour())}${divider()}${formatter(minute())}${divider()}${formatter(second())}"
    }

    private fun convertToHex(num: Int): String = Integer.toHexString(formatTwoDigit(num).toInt())
    private fun formatTwoDigit(num: Int): String = java.lang.String.format("%02d", num)

    private fun updateCalendar() {
        calendar = Calendar.getInstance()
    }

    public fun getPaint(): Paint = paint

    public fun getX(): Float =
            (canvas.getWidth() * (positionX / 100.0)).toFloat()

    public fun getY(): Float =
            ((canvas.getHeight() - (paint.descent() + paint.ascent()) / 2)
                    * ((100 - positionY) / 100.0)).toFloat()

    public fun updateCanvas(canvas: Canvas) {
        this.canvas = canvas
    }

    public fun getContext(): Context {
        return context
    }

}