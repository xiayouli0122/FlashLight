package com.yuri.flashlight;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity implements OnSeekBarChangeListener {

	private SeekBar mPoliceLightBar, mWarningLightBar;

	private int mCurrentWarningLightInterval = 300;
	private int mCurrentPoliceLightInterval = 100;
	
	private SharedPreferences mSharedPreferences = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_settings);
		
		mSharedPreferences = getSharedPreferences(Constants.SHARED_NAME, MODE_PRIVATE);
		mCurrentPoliceLightInterval = mSharedPreferences.getInt(
				Constants.POLICELIGHT_LEVEL, Constants.DEFAULT_POLICELIGHT_LEVEL);
		mCurrentWarningLightInterval = mSharedPreferences.getInt(
				Constants.WARNINGLIGHT_LEVEL, Constants.DEFAULT_WARNINGLIGHT_LEVEL);
		
		mPoliceLightBar = (SeekBar) findViewById(R.id.seekbar_police_light);
		mWarningLightBar = (SeekBar) findViewById(R.id.seekbar_warning_light);
		mPoliceLightBar.setOnSeekBarChangeListener(this);
		mWarningLightBar.setOnSeekBarChangeListener(this);
		
		mPoliceLightBar.setProgress(mCurrentPoliceLightInterval - 50);
		mWarningLightBar.setProgress(mCurrentWarningLightInterval - 100);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.seekbar_warning_light:
			mCurrentWarningLightInterval = progress + 100;
			break;
		case R.id.seekbar_police_light:
			mCurrentPoliceLightInterval = progress + 50;
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// do nothing
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// do nothing
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//save policelight & warninglight level
		Editor editor = mSharedPreferences.edit();
		editor.putInt(Constants.POLICELIGHT_LEVEL, mCurrentPoliceLightInterval);
		editor.putInt(Constants.WARNINGLIGHT_LEVEL, mCurrentWarningLightInterval);
		editor.commit();
	}

	public void onClick_AddShortcut(View view) {
		if (!FLUtils.shortcutInScreen(getContentResolver())) {
			FLUtils.createShortcut(getApplicationContext());
			Toast.makeText(this, R.string.shortcut_added, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.shortcut_exist, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void onClick_RemoveShortcut(View view) {
		if (FLUtils.shortcutInScreen(getContentResolver())) {
			FLUtils.removeShortcut(getApplicationContext());
			Toast.makeText(this, R.string.shortcut_removed, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.no_shortcut, Toast.LENGTH_SHORT).show();
		}
	}
}
