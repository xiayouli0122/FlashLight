package com.yuri.flashlight;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Custome menu for instead of system menu
 * @author Yuri
 *
 */
public class CustomMenu extends PopupWindow implements OnClickListener, OnItemClickListener {

	private OnMenuItemClickListener mListener;
	private LinearLayout mLayout;
	private Menu menu;
	private GridView mGridView;
	
	private Context mContext;
	
	public void setOnMenuItemClick(OnMenuItemClickListener listener){
		mListener = listener;
	}
	
	/**
	 * @param context
	 * @param menu Menu object
	 * @param myMenuAnim animation
	 */
	public CustomMenu(Context context, Menu menu,
			int myMenuAnim) {
		super(context);
		mContext = context;
		this.menu = menu;
		LayoutInflater inflater = LayoutInflater.from(context);
		mLayout = (LinearLayout) inflater.inflate(R.layout.custommenu, null);
		mGridView = (GridView) mLayout.findViewById(R.id.gv_menu);
		mGridView.setAdapter(new MyAdapter());
		mGridView.setOnItemClickListener(this);
		setContentView(this.mLayout);
		
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
//		setBackgroundDrawable(new ColorDrawable(Color.argb(255, 139, 106, 47)));
//		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		setAnimationStyle(myMenuAnim);
		setFocusable(true);

		mLayout.setFocusableInTouchMode(true);
		mLayout.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU && isShowing()) {
					dismiss();
					return true;
				}
				return false;
			}
		});
	}
	
	private class MyAdapter extends BaseAdapter{
		LayoutInflater mInflater;
		
		public MyAdapter(){
			mInflater = LayoutInflater.from(mContext);
		}
		
		@Override
		public int getCount() {
			return menu.size();
		}

		@Override
		public Object getItem(int position) {
			return menu.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view  = mInflater.inflate(R.layout.menu_item, null);
			ImageView iconView = (ImageView) view.findViewById(R.id.iv_menu_icon);
			TextView textView = (TextView) view.findViewById(R.id.iv_menu_name);
			MenuItem item = menu.getItem(position);
			iconView.setImageDrawable(item.getIcon());
			textView.setText(item.getTitle());
			return view;
		}
		
	}

	@Override
	public void onClick(View v) {
		mListener.onMenuItemClick(null);
		dismiss();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mListener.onMenuItemClick(menu.getItem(position));
		dismiss();
	}

}
