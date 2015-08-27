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
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.priyesh.hexatime.R
import com.priyesh.hexatime.core.Background
import com.priyesh.hexatime.core.Clock
import com.priyesh.hexatime.darkenColor
import com.priyesh.hexatime.isLollipop
import kotlin.properties.Delegates

public class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    val toolbar: Toolbar by Delegates.lazy { findViewById(R.id.toolbar) as Toolbar }
    val clock: Clock by Delegates.lazy { Clock(this) }
    val background: Background by Delegates.lazy { Background(clock) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .registerOnSharedPreferenceChangeListener(this)

        getFragmentManager().beginTransaction()
                .add(R.id.container, SettingsFragment())
                .commit()

        val handler = Handler(Looper.getMainLooper())
        var colorOld = getResources().getColor(R.color.primary)
        val updateToolbar = object : Runnable {
            override fun run(): Unit {
                clock.updateCalendar()

                val start = colorOld
                val end = background.getColor()
                colorOld = end

                updateToolbarColor(start, end)
                if (isLollipop()) updateStatusBarColor()

                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateToolbar, 1000)
    }

    private fun updateToolbarColor(start: Int, end: Int) {
        val transition = TransitionDrawable(arrayOf(ColorDrawable(start), ColorDrawable(end)))
        toolbar.setBackground(transition)
        transition.startTransition(300)
    }

    private fun updateStatusBarColor() {
        val statusBarColor = darkenColor(background.getColor(), 0.8f)
        getWindow().setStatusBarColor(statusBarColor)
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