package com.priyesh.hexatime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HexatimeSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
		getPreferenceManager().setSharedPreferencesName(HexatimeService.SHARED_PREFS_NAME); 
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
