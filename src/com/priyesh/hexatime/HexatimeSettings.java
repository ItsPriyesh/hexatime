package com.priyesh.hexatime;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HexatimeSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

	private static final String TAG = "com.priyesh.hexatime.HexatimeSettings";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// tell the preference API what file name should be used
		getPreferenceManager().setSharedPreferencesName(HexatimeService.SHARED_PREFS_NAME); 

		// load the preferences from an XML resource
		addPreferencesFromResource(R.xml.hexatime_settings);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
    protected void onResume() {
            super.onResume();
            return;
    }

    @Override
    protected void onDestroy() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
            return;
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            return;
    }
}
