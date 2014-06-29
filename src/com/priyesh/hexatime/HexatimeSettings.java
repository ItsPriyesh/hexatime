package com.priyesh.hexatime;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Window;
import android.view.WindowManager;

public class HexatimeSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
		getPreferenceManager().setSharedPreferencesName(HexatimeService.SHARED_PREFS_NAME); 
		addPreferencesFromResource(R.xml.hexatime_settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        findViewById(android.R.id.list).setFitsSystemWindows(true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			Window win = getWindow();
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}       
       
        ActionBar ab = getActionBar();
        ab.setBackgroundDrawable(new ColorDrawable (getResources().getColor(R.color.c2)));
        
        getListView().setBackgroundColor(getResources().getColor(R.color.c2));
        setTheme(R.style.Settings);

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
