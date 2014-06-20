package com.priyesh.hexatime;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HexatimeSettings extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // tell the preference API what file name should be used
        getPreferenceManager().setSharedPreferencesName("wallpaper_settings"); 

        // load the preferences from an XML resource
        addPreferencesFromResource(R.xml.hexatime_settings);

    }

}
