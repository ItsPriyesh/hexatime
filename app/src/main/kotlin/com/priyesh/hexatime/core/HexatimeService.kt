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

import android.app.KeyguardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.priyesh.hexatime.EngineIntermediate
import com.priyesh.hexatime.KEY_CLOCK_VISIBILITY
import com.priyesh.hexatime.KEY_ENABLE_BACKGROUND_OVERLAY
import com.priyesh.hexatime.log
import kotlin.properties.Delegates

public class HexatimeService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine = HexatimeEngine()

    private inner class HexatimeEngine :
            EngineIntermediate(this),
            SharedPreferences.OnSharedPreferenceChangeListener {

        private final val handler = Handler()
        private final val updater = Runnable { draw() }

        private final var clockDelegate: PreferenceDelegate by Delegates.notNull()
        private final var backgroundDelegate: PreferenceDelegate by Delegates.notNull()

        private final val UPDATE_FREQ: Long = 1000

        private var visible = false
        private var canvas: Canvas? = null
        private var clock = Clock(getBaseContext())
        private var background = Background(clock)
        private var clockVisibility = 0
        private var enableBackgroundOverlay = false

        private val ALWAYS_VISIBLE = 0
        private val HIDDEN_LOCK_SCREEN = 2

        init {
            PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext())
                    .registerOnSharedPreferenceChangeListener(this)

            val prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
            clockDelegate = clock
            backgroundDelegate = background
            clockVisibility = prefs.getString(KEY_CLOCK_VISIBILITY, "0").toInt()
            enableBackgroundOverlay = prefs.getBoolean(KEY_ENABLE_BACKGROUND_OVERLAY, false)
        }

        override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
            clockDelegate.onPreferenceChange(prefs, key)
            backgroundDelegate.onPreferenceChange(prefs, key)

            when (key) {
                KEY_CLOCK_VISIBILITY -> clockVisibility = prefs.getString(key, "0").toInt()
                KEY_ENABLE_BACKGROUND_OVERLAY -> enableBackgroundOverlay = prefs.getBoolean(key, false)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) handler.post(updater)
            else handler.removeCallbacks(updater)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super<EngineIntermediate>.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(updater)
        }

        private fun draw() {
            var surfaceHolder = getSurfaceHolder()
            try {
                canvas = surfaceHolder.lockCanvas()
                val canvasVal: Canvas? = canvas
                if (canvasVal != null) draw(canvasVal)
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }

            handler.removeCallbacks(updater)

            if (visible) handler.postDelayed(updater, UPDATE_FREQ)
        }

        private fun draw(canvas: Canvas) {
            canvas.drawColor(background.getColor())

            if (enableBackgroundOverlay) background.getBackgroundOverlay().draw(canvas)

            if (shouldDrawClock()) {
                clock.updateCanvas(canvas)
                canvas.drawText(clock.getTime(), clock.getX(), clock.getY(), clock.getPaint())
            }

        }

        private fun shouldDrawClock() = clockVisibility == ALWAYS_VISIBLE ||
                (clockVisibility == HIDDEN_LOCK_SCREEN && !isOnLockScreen())

        private fun isOnLockScreen() =
                (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
                        .inKeyguardRestrictedInputMode()

    }
}