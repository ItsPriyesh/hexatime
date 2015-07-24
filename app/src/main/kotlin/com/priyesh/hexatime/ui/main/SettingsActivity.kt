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

package com.priyesh.hexatime.ui.main

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.priyesh.hexatime.R
import com.priyesh.hexatime.core.Background
import com.priyesh.hexatime.core.Clock
import com.priyesh.hexatime.darkenColor
import kotlin.properties.Delegates

public class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    val clock: Clock by Delegates.lazy { Clock(this) }
    val background: Background by Delegates.lazy { Background(clock) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .registerOnSharedPreferenceChangeListener(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        getFragmentManager().beginTransaction()
                .add(R.id.container, SettingsFragment())
                .commit()

        val handler = Handler()
        var colorOld = getResources().getColor(R.color.primary)
        val updateToolbar = object : Runnable {
            override fun run(): Unit {
                clock.updateCalendar()

                val colorStart = colorOld
                val colorEnd = background.getColor()
                colorOld = colorEnd

                val drawableStart = ColorDrawable(colorStart)
                val drawableEnd = ColorDrawable(colorEnd)

                val transition = TransitionDrawable(arrayOf(drawableStart, drawableEnd))
                toolbar.setBackground(transition)
                transition.startTransition(300)
                getWindow().setStatusBarColor(darkenColor(background.getColor(), 0.8f))

                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateToolbar, 1000)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        background.onPreferenceChange(prefs, key)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.action_done) finishAffinity()
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }
}