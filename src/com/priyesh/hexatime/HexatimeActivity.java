package com.priyesh.hexatime;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class HexatimeActivity extends Activity {

	LinearLayout layout;
    private IntroView mIntroView;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.hexatime_activity);
 
		layout =(LinearLayout)findViewById(R.id.linearlayout);
		
		mIntroView = (IntroView) findViewById(R.id.intro);
	    mIntroView.setSvgResource(R.raw.hexatime);
	    
	    layout.setBackgroundDrawable(new ColorDrawable (getResources().getColor(R.color.c1)));
	    
        
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			Window win = getWindow();
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

}