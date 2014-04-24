package com.yuri.flashlight;

import java.util.HashMap;
import java.util.Map;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MorseActivity extends BaseActivity {
	private final int DOT_TIME = 200; // 点停留的时间，单位：毫秒
	private final int LINE_TIME = DOT_TIME * 3; // 线停留的时间
	private final int DOT_LINE_TIME = DOT_TIME; // 点到线的时间间隔

	private final int CHAR_CHAR_TIME = DOT_TIME * 3; // 字符对哦啊字符之间的时间间隔

	private final int WORD_WORD_TIME = DOT_TIME * 7; // 单词到单词直接的时间间隔
	
	private boolean mSendState = true;

	private String mMorseCode;

	private Map<Character, String> mMorseCodeMap = new HashMap<Character, String>();

	private Camera mCamera;
	private Parameters mParameters;
	
	private TextView mTipView;
	private EditText mEditText;
	private Button mSendBtn;
	
	private static final int MSG_SEND_OVER = 0;
	private static final int MSG_SENDING = 1;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SEND_OVER:
				mSendBtn.setEnabled(true);
				mTipView.setVisibility(View.GONE);
				showAnimation(mTipView, R.anim.slide_up_out);
				
				mTipView.setVisibility(View.VISIBLE);
				showAnimation(mTipView, R.anim.slide_down_in);
				mTipView.setText(R.string.send_over);
				break;
			case MSG_SENDING:
				mTipView.setVisibility(View.VISIBLE);
				showAnimation(mTipView, R.anim.slide_down_in);
				mTipView.setText(getString(R.string.sending, mMorseCode));
				break;
			default:
				break;
			}
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_morse);
		
		mTipView = (TextView) findViewById(R.id.tv_morse_tip);
		mEditText = (EditText) findViewById(R.id.et_morse_code);
		mSendBtn = (Button) findViewById(R.id.btn_send);
		mSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSend();
			}
		});
		
		mMorseCodeMap.put('a', ".-");
		mMorseCodeMap.put('b', "-...");
		mMorseCodeMap.put('c', "-.-.");
		mMorseCodeMap.put('d', "-..");
		mMorseCodeMap.put('e', ".");
		mMorseCodeMap.put('f', "..-.");
		mMorseCodeMap.put('g', "--.");
		mMorseCodeMap.put('h', "....");
		mMorseCodeMap.put('i', "..");
		mMorseCodeMap.put('j', ".---");
		mMorseCodeMap.put('k', "-.-");
		mMorseCodeMap.put('l', ".-..");
		mMorseCodeMap.put('m', "--");
		mMorseCodeMap.put('n', "-.");
		mMorseCodeMap.put('o', "---");
		mMorseCodeMap.put('p', ".--.");
		mMorseCodeMap.put('q', "--.-");
		mMorseCodeMap.put('r', ".-.");
		mMorseCodeMap.put('s', "...");
		mMorseCodeMap.put('t', "-");
		mMorseCodeMap.put('u', "..-");
		mMorseCodeMap.put('v', "...-");
		mMorseCodeMap.put('w', ".--");
		mMorseCodeMap.put('x', "-..-");
		mMorseCodeMap.put('y', "-.--");
		mMorseCodeMap.put('z', "--..");

		mMorseCodeMap.put('0', "-----");
		mMorseCodeMap.put('1', ".----");
		mMorseCodeMap.put('2', "..---");
		mMorseCodeMap.put('3', "...--");
		mMorseCodeMap.put('4', "....-");
		mMorseCodeMap.put('5', ".....");
		mMorseCodeMap.put('6', "-....");
		mMorseCodeMap.put('7', "--...");
		mMorseCodeMap.put('8', "---..");
		mMorseCodeMap.put('9', "----.");
	}

	public void startSend() {
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)) {
			Toast.makeText(this, R.string.no_support_flashlight, Toast.LENGTH_LONG).show();
			return;
		}
		if (verifyMorseCode()) {
			mSendBtn.setEnabled(false);
			mHandler.sendMessage(mHandler.obtainMessage(MSG_SENDING));
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendSentense(mMorseCode);
				}
			}).start();
		}
	}

	// 发送点
	private void sendDot() {
		openFlashlight();
		sleepExt(DOT_TIME);
		closeFlashlight();
	}

	// 发送线
	private void sendLine() {
		openFlashlight();
		sleepExt(LINE_TIME);
		closeFlashlight();
	}

	// 发送字符
	private void sendChar(char c) {
		String morseCode = mMorseCodeMap.get(c);
		if (morseCode != null) {
			char lastChar = ' ';
			for (int i = 0; i < morseCode.length(); i++){
				char dotLine = morseCode.charAt(i);
				if (dotLine == '.') {
					sendDot();
				} else if (dotLine == '-') {
					sendLine();
				}
				//终止线程运行
				if (!mSendState) {
					return;
				}
				if (i > 0 && i < morseCode.length() - 1) {
					if (lastChar == '.' && dotLine == '-') {
						sleepExt(DOT_LINE_TIME);
					}
				}
				lastChar = dotLine;
			}
		}
	}

	private void sendWord(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			sendChar(c);
			//终止线程运行
			if (!mSendState) {
				return;
			}
			if (i < s.length() - 1) {
				sleepExt(CHAR_CHAR_TIME);
			}
		}
	}

	private void sendSentense(String s) {
		String[] words = s.split(" +");
		for (int i = 0; i < words.length; i++) {
			sendWord(words[i]);
			//终止线程运行
			if (!mSendState) {
				return;
			}
			if (i < words.length - 1) {
				sleepExt(WORD_WORD_TIME);
			}
		}
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_OVER));
	}

	private boolean verifyMorseCode() {
		mMorseCode = mEditText.getText().toString().toLowerCase();
		if ("".equals(mMorseCode)) {
			Toast.makeText(this, R.string.input_morse_code, Toast.LENGTH_LONG).show();
			return false;
		}

		for (int i = 0; i < mMorseCode.length(); i++) {
			char c = mMorseCode.charAt(i);
			if (!(c >= 'a' && c <= 'z') && !(c >= '0' && c <= '9') && c != ' ') {
				Toast.makeText(this, R.string.morse_input_tip, Toast.LENGTH_LONG)
						.show();
				return false;
			}
		}
		return true;
	}

	// 打开闪光灯
	private void openFlashlight() {
		try {
			mCamera = Camera.open();
			int textureId = 0;
			mCamera.setPreviewTexture(new SurfaceTexture(textureId));
			mCamera.startPreview();

			mParameters = mCamera.getParameters();
			mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 关闭闪光灯
	private void closeFlashlight() {
		if (mCamera != null) {
			mParameters = mCamera.getParameters();
			mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(mParameters);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSendState= false;
	}
}
