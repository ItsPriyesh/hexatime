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

package com.priyesh.hexatime.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.larvalabs.svgandroid.SVGBuilder
import com.priyesh.hexatime.R
import com.priyesh.hexatime.core.HexatimeService
import com.priyesh.hexatime.getPixels
import kotlin.properties.Delegates

public class MainActivity : AppCompatActivity() {

    private val handler = Handler()

    private var parentLayout: ViewGroup by Delegates.notNull()
    private var logoView: ImageView by Delegates.notNull()
    private var activateButton: Button by Delegates.notNull()
    private var settingsButton: ImageButton by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        parentLayout = findViewById(R.id.parent) as ViewGroup
        logoView = findViewById(R.id.logo_view) as ImageView

        activateButton = findViewById(R.id.button_activate) as Button
        settingsButton = findViewById(R.id.button_settings) as ImageButton

        activateButton.setOnClickListener { activate() }
        settingsButton.setOnClickListener { openSettings() }

        activateButton.setTypeface(Typeface.createFromAsset(getAssets(), "Lato-Light.ttf"))

        logoView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        logoView.setImageDrawable(getLogoDrawable())

        parentLayout.post { handler.postDelayed({ animateIn() }, 400) }
    }

    private fun animateIn() {
        val backgroundAlpha = ObjectAnimator
                .ofFloat(parentLayout, View.ALPHA, 1f)
                .setDuration(500)

        val logoAlpha = ObjectAnimator
                .ofFloat(logoView, View.ALPHA, 1f)
                .setDuration(400)

        val logoTranslation = ObjectAnimator
                .ofFloat(logoView, View.TRANSLATION_Y, -logoView.getHeight().toFloat())
                .setDuration(400)

        val activateAlpha = ObjectAnimator
                .ofFloat(activateButton, View.ALPHA, 1f)
                .setDuration(250)

        val activateTranslation = ObjectAnimator
                .ofFloat(activateButton, View.TRANSLATION_Y,
                        logoView.getHeight().toFloat() - activateButton.getHeight().toFloat())
                .setDuration(300)

        val settingsAlpha = ObjectAnimator
                .ofFloat(settingsButton, View.ALPHA, 1f)
                .setDuration(200)

        val settingsTranslation = ObjectAnimator
                .ofFloat(settingsButton, View.TRANSLATION_Y,
                        logoView.getHeight().toFloat() + getPixels(24))
                .setDuration(500)

        val settingsRotation = ObjectAnimator
                .ofFloat(settingsButton, View.ROTATION, 180f)
                .setDuration(500)

        val setOne = AnimatorSet()
        setOne.setInterpolator(AccelerateDecelerateInterpolator())
        setOne.playSequentially(
                backgroundAlpha,
                logoAlpha,
                logoTranslation,
                activateAlpha,
                activateTranslation
        )

        val setTwo = AnimatorSet()
        setTwo.setInterpolator(FastOutSlowInInterpolator())
        setTwo.setStartDelay(500 + 400 + 400 + 250 + 300)
        setTwo.playTogether(
                settingsAlpha,
                settingsTranslation,
                settingsRotation
        )

        setOne.start()
        setTwo.start()
    }

    private fun getLogoDrawable(): Drawable
            = SVGBuilder()
            .readFromResource(getResources(), R.raw.hexatime)
            .setWhiteMode(true)
            .build()
            .getDrawable()

    private fun activate() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, HexatimeService().javaClass))

        startActivity(intent)
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity().javaClass))
    }
}