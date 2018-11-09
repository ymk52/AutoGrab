package com.ymk.autograb;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AutoGrabMainActivity extends Activity {

	ImageButton mStarPermission;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_grab_main);
		mStarPermission = (ImageButton)findViewById(R.id.start_permission);
		mStarPermission.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(intent);
			}
		});
	}


}
