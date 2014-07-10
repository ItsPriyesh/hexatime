package com.priyesh.hexatime;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class HexatimeSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
		getPreferenceManager().setSharedPreferencesName(HexatimeService.SHARED_PREFS_NAME); 
		addPreferencesFromResource(R.xml.hexatime_settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setFitsSystemWindows(true);
        ActionBar ab = getActionBar();
        ab.setBackgroundDrawable(new ColorDrawable (getResources().getColor(R.color.c2)));
        getListView().setBackgroundColor(getResources().getColor(R.color.c2));
        setTheme(R.style.Settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			Window win = getWindow();
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}       
       
        Preference contact = (Preference) findPreference("contact");
		contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent email = new Intent();
				email.setAction(Intent.ACTION_VIEW);
				email.addCategory(Intent.CATEGORY_BROWSABLE);
				email.setData(Uri.parse("mailto:priyesh.96@hotmail.com"));
				startActivity(email);
				return true; 
			}
		});
		
        Preference source = (Preference) findPreference("source");
		source.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent git = new Intent();
				git.setAction(Intent.ACTION_VIEW);
				git.addCategory(Intent.CATEGORY_BROWSABLE);
				git.setData(Uri.parse("https://github.com/ItsPriyesh/Hexatime"));
				startActivity(git);
				return true; 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.apply:
	        	startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
