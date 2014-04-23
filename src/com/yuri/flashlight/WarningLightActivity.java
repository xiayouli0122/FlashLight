package com.yuri.flashlight;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class WarningLightActivity extends BaseActivity {
	protected boolean mWarningLightFlicker; // true: …¡À∏ false£∫Õ£÷π…¡À∏
	protected boolean mWarningLightState; // true: on-off false: off-on
	
	protected ImageView mImageViewWarningLight1;
	protected ImageView mImageViewWarningLight2;

	private int mLightLevel = 300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_warning_light);
		mWarningLightFlicker = true;

		mImageViewWarningLight1 = (ImageView) findViewById(R.id.iv_warning_light1);
		mImageViewWarningLight2 = (ImageView) findViewById(R.id.iv_warning_light2);
		
		SharedPreferences sp = getSharedPreferences(Constants.SHARED_NAME, MODE_PRIVATE);
		mLightLevel = sp.getInt(Constants.WARNINGLIGHT_LEVEL, Constants.DEFAULT_WARNINGLIGHT_LEVEL);
		
		new WarningLightThread().start();
	}

	class WarningLightThread extends Thread {
		public void run() {
			mWarningLightFlicker = true;
			while (mWarningLightFlicker) {
				try {
					Thread.sleep(mLightLevel);
					mWarningHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private Handler mWarningHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mWarningLightState) {
				mImageViewWarningLight1
						.setImageResource(R.drawable.warning_light_on);
				mImageViewWarningLight2
						.setImageResource(R.drawable.warning_light_off);
				mWarningLightState = false;
			} else {
				mImageViewWarningLight1
						.setImageResource(R.drawable.warning_light_off);
				mImageViewWarningLight2
						.setImageResource(R.drawable.warning_light_on);
				mWarningLightState = true;
			}
		}
	};
	
	protected void onStop() {
		super.onStop();
		mWarningLightState = false;
	};
}
