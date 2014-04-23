package com.yuri.flashlight;

import com.yuri.flashlight.widget.HideTextView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;

public class PoliceLightActivity extends BaseActivity {
	private boolean mPoliceState;
	private FrameLayout mUIPoliceLight;
	private HideTextView mHideTextView;
	
	private int mLightLevel = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_policelight);
		mUIPoliceLight = (FrameLayout) findViewById(R.id.framelayout_police_light);
		mHideTextView = (HideTextView) findViewById(R.id.tv_hide_police_light);
		mHideTextView.hide();
		
		SharedPreferences sp = getSharedPreferences(Constants.SHARED_NAME, MODE_PRIVATE);
		mLightLevel = sp.getInt(Constants.POLICELIGHT_LEVEL, Constants.DEFAULT_POLICELIGHT_LEVEL);
		
		new PoliceThread().start();
	}

	class PoliceThread extends Thread {
		public void run() {
			mPoliceState = true;
			while (mPoliceState) {
				mHandler.sendEmptyMessage(Color.BLUE);
				sleepExt(mLightLevel);
				mHandler.sendEmptyMessage(Color.BLACK);
				sleepExt(mLightLevel);
				mHandler.sendEmptyMessage(Color.RED);
				sleepExt(mLightLevel);
				mHandler.sendEmptyMessage(Color.BLACK);
				sleepExt(mLightLevel);
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			int color = message.what;
			mUIPoliceLight.setBackgroundColor(color);
		}
	};
	
	protected void onStop() {
		super.onStop();
		//stop thread
		mPoliceState = false;
	};
}
