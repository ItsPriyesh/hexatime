/*
 * Copyright (C) 2014 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.priyesh.hexatime;

import com.priyesh.hexatime.InAppBilling.IabHelper;
import com.priyesh.hexatime.InAppBilling.IabResult;
import com.priyesh.hexatime.InAppBilling.Inventory;
import com.priyesh.hexatime.InAppBilling.Purchase;
import com.priyesh.hexatime.InterfaceUtils.FloatingActionButton;
import com.priyesh.hexatime.InterfaceUtils.SystemBarTintManager;

import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

public class HexatimeSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

	IabHelper mHelper;
	static final String ITEM_SKU = "com.priyesh.hexatime.donate";
	private static final String TAG = "com.priyesh.hexatime";
	public static final String SHARED_PREFS_NAME="hexatime_settings";

	private SharedPreferences mPrefs = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
		super.onCreate(icicle);
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_down);		
		getPreferenceManager().setSharedPreferencesName(HexatimeService.SHARED_PREFS_NAME); 
		addPreferencesFromResource(R.xml.hexatime_settings);
		setContentView(R.layout.settings_layout);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		ListView list = (ListView) findViewById(android.R.id.list);
		list.setFitsSystemWindows(true); 
		list.setBackgroundColor(getResources().getColor(R.color.settings_bg));

		FloatingActionButton applyFab = (FloatingActionButton)findViewById(R.id.apply_fab);
		applyFab.setColor(Color.WHITE);
		applyFab.setDrawable(getResources().getDrawable(R.drawable.ic_navigation_accept));
		applyFab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			Window win = getWindow();
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.c2));
		}       
	
		mPrefs = HexatimeSettings.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
		String clockAddonIndex = mPrefs.getString("CLOCK_ADDONS", "1");
		int clockAddonIndexNum = Integer.parseInt(clockAddonIndex);
		final ListPreference clockAddon = (ListPreference)findPreference("CLOCK_ADDONS");
		final ListPreference separatorStyle = (ListPreference)findPreference("SEPARATOR_STYLE");
		if (clockAddonIndexNum == 0 || clockAddonIndexNum == 1){
			separatorStyle.setEnabled(false);
		}
		else {
			separatorStyle.setEnabled(true);
		}
		
		clockAddon.setOnPreferenceChangeListener(new
		Preference.OnPreferenceChangeListener() {
		  public boolean onPreferenceChange(Preference preference, Object newValue) {
		    final String val = newValue.toString();
		    int index = clockAddon.findIndexOfValue(val);
		    if(index == 0 || index == 1)
		    	separatorStyle.setEnabled(false);
		    else
		    	separatorStyle.setEnabled(true);
		    return true;
		  }
		});
		
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8"
				+ "AMIIBCgKCAQEAvCkRv5LFEf30z2omRbkygc7gxsDr+i1pd2Scz55/"
				+ "PO/11a3yt7fEf0zM98wV6JKJITMpbve0RQ7J7M2Yjz8F4QpkkqLZ4"
				+ "7PM6j0XlkU63dTZoHP0lHQsphr/eE4cjUymFHOyU9b3pQCAMGI2iD"
				+ "twGzHtOwE12u+UZSmfr8rjEVQGMtlZDuSmJEEx5YlJXrg/BJIHM7W"
				+ "o2IoyNVdhmuZ6xkpyv1pd8aVuAEddWvQwJHuhE7C5C4gz9zWI6frV"
				+ "7zTgiIudEECH3PUJUnT5lfK0nk6OZ5Y4ZsyBDFLfT8qhMHgQRy6uB"
				+ "k9Bv/3hZEMJVDeFER9HFChLLuDqeLc9sQOPewIDAQAB";

				
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d(TAG, "In-app Billing setup failed: " + 
							result);
				} else {             
					Log.d(TAG, "In-app Billing is set up OK");
				}
				try {
					mHelper.queryInventoryAsync(mReceivedInventoryListener);
				}
				catch (IllegalStateException e) {

				}
			}
		});
		
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

		Preference xda = (Preference) findPreference("xda");
		xda.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent git = new Intent();
				git.setAction(Intent.ACTION_VIEW);
				git.addCategory(Intent.CATEGORY_BROWSABLE);
				git.setData(Uri.parse("http://forum.xda-developers.com/android/apps-games/app-hexatime-t2829060"));
				startActivity(git);
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

		Preference donateInAppBilling = (Preference) findPreference("donate");
		donateInAppBilling.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {				
				mHelper.launchPurchaseFlow(HexatimeSettings.this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
				return true; 
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {     
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				Toast.makeText(getApplicationContext(), "Failed to make purchase.", Toast.LENGTH_LONG).show();
				return;
			}      
			else if (purchase.getSku().equals(ITEM_SKU)) {
				consumeItem();
			}
		}
	};

	public void consumeItem() {
		mHelper.queryInventoryAsync(mReceivedInventoryListener);
	}

	IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			if (result.isFailure()) {			
			}
			Purchase donate = inventory.getPurchase(ITEM_SKU);

			if (donate != null) {
				mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
			}
		}
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (result.isSuccess()) {	
				String thanks = getResources().getString(R.string.thanks);
				Toast.makeText(HexatimeSettings.this, thanks, Toast.LENGTH_SHORT).show();
			} else {
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		return;
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
		return;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

}
