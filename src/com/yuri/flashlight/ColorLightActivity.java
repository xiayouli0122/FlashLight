package com.yuri.flashlight;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.yuri.flashlight.widget.HideTextView;

public class ColorLightActivity extends BaseActivity {
	protected int mCurrentColorLight = Color.RED;
	
	protected FrameLayout mUIColorLight;
	protected HideTextView mHideTextViewColorLight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_colorlight);
		mUIColorLight = (FrameLayout) findViewById(R.id.fl_color_light);
		mUIColorLight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPickerDialog colorPickerDialog = new ColorPickerDialog(ColorLightActivity.this, mCurrentColorLight ,
						getResources().getString(R.string.app_name), new ColorPickerDialog.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						mUIColorLight.setBackgroundColor(color);
						mCurrentColorLight = color;
					}
				});
				colorPickerDialog.show();
			}
		});
		mHideTextViewColorLight = (HideTextView) findViewById(R.id.tv_hide_color_light);
		mHideTextViewColorLight.hide();
	}

}
