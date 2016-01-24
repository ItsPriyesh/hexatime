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

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.priyesh.hexatime.R
import com.priyesh.hexatime.api
import com.priyesh.hexatime.core.Background
import com.priyesh.hexatime.core.Clock
import com.priyesh.hexatime.darkenColor
import kotlin.properties.Delegates

public class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }
    val clock: Clock by lazy { Clock(this) }
    val background: Background by lazy { Background(clock) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        PreferenceManager.getDefaultSharedPreferences(baseContext)
                .registerOnSharedPreferenceChangeListener(this)

        fragmentManager.beginTransaction()
                .add(R.id.container, SettingsFragment())
                .commit()

        val handler = Handler(Looper.getMainLooper())
        var colorOld = ContextCompat.getColor(this, R.color.primary)
        val updateToolbar = object : Runnable {
            override fun run(): Unit {
                clock.updateCalendar()

                val start = colorOld
                val end = background.getColor()
                colorOld = end

                updateToolbarColor(start, end)
                if (api(21)) updateStatusBarColor()

                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateToolbar, 1000)
    }

    private fun updateToolbarColor(start: Int, end: Int) {
        val transition = TransitionDrawable(arrayOf(ColorDrawable(start), ColorDrawable(end)))
        if (api(16)) toolbar.background = transition else toolbar.setBackgroundDrawable(transition);
        transition.startTransition(300)
    }

    private fun updateStatusBarColor() {
        val statusBarColor = darkenColor(background.getColor(), 0.8f)
        window.statusBarColor = statusBarColor
    }

    private fun goHome() {
        val home = Intent(Intent.ACTION_MAIN)
        home.addCategory(Intent.CATEGORY_HOME)
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(home)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        background.onPreferenceChange(prefs, key)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.action_done) goHome()
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }
}