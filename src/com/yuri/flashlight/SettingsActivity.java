package com.yuri.flashlight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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

	/**
	 * exist shortcut in screen
	 * @return
	 */
	private boolean shortcutInScreen() {
		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);

		Cursor cursor = getContentResolver()
				.query(CONTENT_URI,
						null,
						"intent like ?",
						new String[] { "%component=com.yuri.flashlight/.MainUIActivity%" },
						null);

		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void onClick_AddShortcut(View view) {
		if (!shortcutInScreen()) {
			Intent installShortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
					R.drawable.ic_launcher);// 获取快捷方式图标

			Intent flashlightIntent = new Intent();
			flashlightIntent.setClassName("com.yuri.flashlight",
					"com.yuri.flashlight.MainUIActivity");
			flashlightIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			flashlightIntent.setAction(Intent.ACTION_MAIN);
			flashlightIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			installShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
			installShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
			installShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
					flashlightIntent);

			sendBroadcast(installShortcut);
			Toast.makeText(this, R.string.shortcut_added, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, R.string.shortcut_exist, Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onClick_RemoveShortcut(View view) {
		if (shortcutInScreen()) {
			Intent uninstallShortcut = new Intent(
					"com.android.launcher.action.UNINSTALL_SHORTCUT");
			uninstallShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

			Intent flashlightIntent = new Intent();
			flashlightIntent.setClassName("com.yuri.flashlight",
					"com.yuri.flashlight.MainUIActivity");

			uninstallShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
					flashlightIntent);

			flashlightIntent.setAction(Intent.ACTION_MAIN);
			flashlightIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			sendBroadcast(uninstallShortcut);
		} else {
			Toast.makeText(this, R.string.no_shortcut, Toast.LENGTH_LONG).show();
		}
	}
}
