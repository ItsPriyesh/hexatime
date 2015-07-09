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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceFragment
import com.priyesh.hexatime.BuildConfig
import com.priyesh.hexatime.R

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

        findPreference("background_dim").setOnPreferenceClickListener {
            SliderPreference("Background dim", "background_dim", context).display()
            true
        }

        findPreference("version").setSummary(VERSION_STRING)

        findPreference("source").setOnPreferenceClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse("https://github.com/ItsPriyesh/HexaTime"))
            startActivity(intent)
            true
        }
    }
}