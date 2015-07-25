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

        findPreference("clock_position").setOnPreferenceClickListener {
            ClockPositionDialog(context).create().show()
            true
        }

        val saturation = findPreference(KEY_BACKGROUND_SATURATION)
        val lightness = findPreference(KEY_BACKGROUND_BRIGHTNESS)

        fun updateHSLPrefs(hslEnabled: Boolean): Unit {
            saturation setEnabled hslEnabled
            lightness setEnabled hslEnabled
        }

        val colorMode = findPreference(KEY_COLOR_MODE) as ListPreference
        updateHSLPrefs(colorMode.getValue() equals "1")

        findPreference(KEY_COLOR_MODE) setOnPreferenceChangeListener { preference, newValue ->
            updateHSLPrefs(newValue as String equals "1")
            true
        }

        findPreference(KEY_BACKGROUND_SATURATION) setOnPreferenceClickListener {
            SliderPreference("Saturation", KEY_BACKGROUND_SATURATION, context).display()
            true
        }

        findPreference(KEY_BACKGROUND_BRIGHTNESS) setOnPreferenceClickListener {
            SliderPreference("Lightness", KEY_BACKGROUND_BRIGHTNESS, context).display()
            true
        }

        findPreference("version") setSummary(VERSION_STRING)

        findPreference("source") setOnPreferenceClickListener {
            val intent = Intent()
            intent setAction Intent.ACTION_VIEW
            intent addCategory Intent.CATEGORY_BROWSABLE
            intent setData Uri.parse("https://github.com/ItsPriyesh/HexaTime")
            startActivity(intent)
            true
        }
    }

}