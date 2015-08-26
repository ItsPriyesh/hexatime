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
import android.net.Uri
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import com.priyesh.hexatime.*
import com.priyesh.hexatime.ui.preferences.ClockPositionDialog
import com.priyesh.hexatime.ui.preferences.SliderPreference

public class SettingsFragment : PreferenceFragment() {

    private val VERSION_STRING = "${BuildConfig.VERSION_NAME} - ${BuildConfig.BUILD_TYPE}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)

        val context = getActivity()

        val saturation = findPreference(KEY_BACKGROUND_SATURATION)
        val brightness = findPreference(KEY_BACKGROUND_BRIGHTNESS)

        fun updateHSBPrefs(hslEnabled: Boolean): Unit {
            saturation setEnabled hslEnabled
            brightness setEnabled hslEnabled
        }

        val colorMode = findPreference(KEY_COLOR_MODE) as ListPreference
        updateHSBPrefs(colorMode.getValue() equals "1")

        findPreference(KEY_COLOR_MODE) setOnPreferenceChangeListener { preference, newValue ->
            updateHSBPrefs(newValue as String equals "1")
            true
        }

        onPreferenceClick("clock_position", { ClockPositionDialog(context).create().show() })

        fun displaySlider(title: String, key: String, def: Int): Unit {
            SliderPreference(title, key, def, context).display()
        }

        onPreferenceClick(KEY_BACKGROUND_SATURATION, {
            displaySlider("Saturation", KEY_BACKGROUND_SATURATION, 50)
        })

        onPreferenceClick(KEY_BACKGROUND_BRIGHTNESS, {
            displaySlider("Brightness", KEY_BACKGROUND_BRIGHTNESS, 50)
        })

        onPreferenceClick(KEY_BACKGROUND_OVERLAY_OPACITY, {
            displaySlider("Overlay opacity", KEY_BACKGROUND_OVERLAY_OPACITY, 10)
        })

        onPreferenceClick(KEY_BACKGROUND_OVERLAY_SCALE, {
            displaySlider("Overlay scale", KEY_BACKGROUND_OVERLAY_SCALE, 50)
        })

        onPreferenceClick("source", {
            val intent = Intent()
            intent setAction Intent.ACTION_VIEW
            intent addCategory Intent.CATEGORY_BROWSABLE
            intent setData Uri.parse("https://github.com/ItsPriyesh/HexaTime")
            startActivity(intent)
        })

        findPreference("version") setSummary(VERSION_STRING)
    }

    private fun onPreferenceClick(key: String, onClick: () -> Unit) {
        findPreference(key) setOnPreferenceClickListener {
            onClick()
            true
        }
    }

}