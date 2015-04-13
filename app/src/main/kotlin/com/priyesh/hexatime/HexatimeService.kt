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

import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.service.wallpaper.WallpaperService.Engine
import android.view.SurfaceHolder

public class HexatimeService : WallpaperService() {

    override fun onCreateEngine(): Engine = HexatimeEngine()

    private inner class HexatimeEngine : EngineIntermediate(this) {
        private final val handler = Handler()
        private final val updater = Runnable { draw() }

        private var visible = false

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            when {
                visible -> handler.post(updater)
                !visible -> handler.removeCallbacks(updater)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(updater)
        }

        private fun draw() {

        }

    }
}