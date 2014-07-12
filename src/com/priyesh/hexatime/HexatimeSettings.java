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

import com.priyesh.hexatime.util.Inventory;
import com.priyesh.hexatime.util.Purchase;
import com.priyesh.hexatime.util.IabResult;
import com.priyesh.hexatime.util.IabHelper;

import android.util.Log;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

public class HexatimeSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

	IabHelper mHelper;
	static final String ITEM_SKU = "com.priyesh.hexatime.donate";
	private static final String TAG = "com.priyesh.hexatime";
	
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
       
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8"
        		+ "AMIIBCgKCAQEAvCkRv5LFEf30z2omRbkygc7gxsDr+i1pd2Scz55/"
        		+ "PO/11a3yt7fEf0zM98wV6JKJITMpbve0RQ7J7M2Yjz8F4QpkkqLZ4"
        		+ "7PM6j0XlkU63dTZoHP0lHQsphr/eE4cjUymFHOyU9b3pQCAMGI2iD"
        		+ "twGzHtOwE12u+UZSmfr8rjEVQGMtlZDuSmJEEx5YlJXrg/BJIHM7W"
        		+ "o2IoyNVdhmuZ6xkpyv1pd8aVuAEddWvQwJHuhE7C5C4gz9zWI6frV"
        		+ "7zTgiIudEECH3PUJUnT5lfK0nk6OZ5Y4ZsyBDFLfT8qhMHgQRy6uB"
        		+ "k9Bv/3hZEMJVDeFER9HFChLLuDqeLc9sQOPewIDAQAB";
        
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        boolean enableInAppBilling = true; //set false to fix crashing on devices without GApps
        
        if (enableInAppBilling){
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				public void onIabSetupFinished(IabResult result) {
					if (!result.isSuccess()) {
						Log.d(TAG, "In-app Billing setup failed: " + 
								result);
					} else {             
						Log.d(TAG, "In-app Billing is set up OK");
					}
					mHelper.queryInventoryAsync(mReceivedInventoryListener);
				}
			});
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
				Toast.makeText(HexatimeSettings.this, "Thanks :)", Toast.LENGTH_SHORT).show();
			} else {
			}
		}
	};
	
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
