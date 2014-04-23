package com.yuri.flashlight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	protected void sleepExt(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void showAnimation(View view, int animId){
		view.clearAnimation();
		view.startAnimation(AnimationUtils.loadAnimation(this, animId));
	}
	
	/**
	 * start activity by class name
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * start activity by class name & include data
	 * 
	 * @param pClass
	 * @param bundle
	 */
	protected void openActivity(Class<?> pClass, Bundle bundle) {
		Intent intent = new Intent(this, pClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

}
