package com.yuri.flashlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainUIActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnMenuItemClickListener {
	private static final String TAG = "FL/MainUIActivity";
	
	private TextView mBatteryTV;
	private ToggleButton mPowerButton;
	
	private Camera mCamera;
	private Parameters mParameters;
	
	private boolean mPowerOn = false;
	
	private CustomMenu mCustomMenu;
	
	private MediaPlayer mMediaPlayer;
	
	private BatteryReceiver mBatteryReceiver = null;


	/**
	 * Receiver battery change
	 */
	private class BatteryReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//判断它是否是为电量变化的Broadcast Action
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
				//获取当前电量
				int level = intent.getIntExtra("level", 0);
				//电量的总刻度
				int scale = intent.getIntExtra("scale", 100);
				//把它转成百分比
				int percent = (level * 100) / scale;
				if (percent <= 20 && percent > 5) {
					mBatteryTV.setBackgroundResource(R.drawable.battery2);
				} else if (percent <= 5) {
					mBatteryTV.setBackgroundResource(R.drawable.battery3);
				} else {
					mBatteryTV.setBackgroundResource(R.drawable.battery1);
				}
				mBatteryTV.setText(percent +"%");
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
		
		mMediaPlayer = new MediaPlayer();
		
		forceShowMenuKey(getWindow());
		
		mBatteryTV = (TextView) findViewById(R.id.tv_battery);
		mPowerButton = (ToggleButton) findViewById(R.id.tb_power);
		mPowerButton.setOnCheckedChangeListener(this);
		
		// 注册广播接受者java代码
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		// 创建广播接受者对象
		mBatteryReceiver = new BatteryReceiver();
		// 注册receiver
		registerReceiver(mBatteryReceiver, intentFilter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		mCustomMenu = new CustomMenu(getApplicationContext(), menu, R.style.PopupAnimation);
		mCustomMenu.setOnMenuItemClick(this);
		mCustomMenu.update();
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (mCustomMenu != null) {
			if (mCustomMenu.isShowing())
				mCustomMenu.dismiss();
			else {
				mCustomMenu.showAtLocation(findViewById(R.id.rl_main),
						Gravity.BOTTOM, 0, 0);
			}
		}
		return false;//true:show system menu
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_police:
			openActivity(PoliceLightActivity.class);
			break;
		case R.id.menu_warnning:
			openActivity(WarningLightActivity.class);
			break;
		case R.id.menu_morse:
			openActivity(MorseActivity.class);
			break;
		case R.id.menu_color:
			openActivity(ColorLightActivity.class);
			break;
		case R.id.menu_settings:
			openActivity(SettingsActivity.class);
			break;
		case R.id.menu_exit:
			MainUIActivity.this.finish();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		playSounds(R.raw.click);
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)) {
			Toast.makeText(this, R.string.no_support_flashlight, Toast.LENGTH_LONG).show();
			return;
		}
		
		mPowerOn = isChecked;
		if (mPowerOn) {
			openFlashlight();
		} else {
			closeFlashlight();
		}
	}

	// open flash light
	protected void openFlashlight() {
		try {
			mCamera = Camera.open();
			int textureId = 0;
			mCamera.setPreviewTexture(new SurfaceTexture(textureId));
			mCamera.startPreview();

			mParameters = mCamera.getParameters();

			mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			Log.e(TAG, "ERROR:" + e.toString());
		}
	}

	//close flash light
	protected void closeFlashlight() {
		if (mCamera != null) {
			mParameters = mCamera.getParameters();
			mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(mParameters);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	//play sound
	private void playSounds(int sid) {
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		mMediaPlayer = MediaPlayer.create(MainUIActivity.this, sid);
		mMediaPlayer.start();
	}
	
	/**
	 * force show virtual menu key </br>
	 * must call after setContentView() 
	 * @param window you can use getWindow()
	 */
	private void forceShowMenuKey(Window window){
		try {
			window.addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	private long mExitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 再按一次back退出
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - mExitTime > 2000)) {
				Toast.makeText(this, R.string.double_back_exit, Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				mExitTime = 0;
				this.finish();
			}
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPowerOn) {
			closeFlashlight();
		}
		
		unregisterReceiver(mBatteryReceiver);
	}

}