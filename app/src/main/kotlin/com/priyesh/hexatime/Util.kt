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

package com.priyesh.hexatime

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager

public fun Context.getPixels(dpValue: Int): Float =
        dpValue * getResources().getDisplayMetrics().density + 0.5f

public fun log(message: String): Int = if (BuildConfig.DEBUG) Log.d("HexaTime", message) else 0

public fun darkenColor(color: Int, factor: Float): Int {
    var hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[2] *= factor
    return Color.HSVToColor(hsv);
}

public fun getScreenWidth(c: Context): Int = getScreenSize(c).x
public fun getScreenHeight(c: Context): Int = getScreenSize(c).y

private fun getScreenSize(c: Context): Point {
    val display = (c.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
    val point = Point()
    display.getRealSize(point)
    return point
}

public fun isLollipop(): Boolean = Build.VERSION.SDK_INT >= 21