package com.priyesh.hexatime;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class HexatimeActivity extends Activity implements Handler.Callback{

	private Handler mHandler = new Handler(this);

    private int mDelay = 1500;
    
	LinearLayout layout;
	private IntroView mIntroView;
	TransitionDrawable trans;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.hexatime_activity);

		layout =(LinearLayout)findViewById(R.id.linearlayout);

		mIntroView = (IntroView) findViewById(R.id.intro);
		mIntroView.setSvgResource(R.raw.hexatime);

		ColorDrawable c1 = new ColorDrawable(getResources().getColor(R.color.c1));
		ColorDrawable c2 = new ColorDrawable(getResources().getColor(R.color.c2));
		ColorDrawable[] color = {c1, c2}; 
		trans = new TransitionDrawable(color);
		layout.setBackgroundDrawable(trans); 
		start();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			Window win = getWindow();
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			win.setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
	
	private Runnable mEvent = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(mEvent);
            mHandler.postDelayed(mEvent, mDelay);
            Message message = mHandler.obtainMessage();
            mHandler.sendMessage(message);
        }
    };

    public boolean handleMessage(Message message) {
    	TransitionDrawable drawable = (TransitionDrawable) layout.getBackground();    
    	drawable.reverseTransition(mDelay);
        return true;
    }

    public void start() {
        mHandler.post(mEvent);
    }

    public void stop() {
        mHandler.removeCallbacks(mEvent);
    }
}