package com.priyesh.hexatime;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HexatimeActivity extends Activity {

    private Button setWallpaperButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hexatime_activity);

        setWallpaperButton = (Button) findViewById(R.id.btn_set_wallpaper);
        setWallpaperButton.setOnClickListener(setWallpaperListener);  

    } 

    private View.OnClickListener setWallpaperListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            
            startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(HexatimeActivity.this, HexatimeService.class))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            
            finish();

        }
    };

}