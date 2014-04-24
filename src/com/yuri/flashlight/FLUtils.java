package com.yuri.flashlight;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.view.Window;
import android.view.WindowManager;

public class FLUtils {
	
	/**
	 * force show virtual menu key </br>
	 * must call after setContentView() 
	 * @param window you can use getWindow()
	 */
	public static void forceShowMenuKey(Window window){
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

	/**
	 * exist shortcut in screen
	 * @return
	 */
	public static boolean shortcutInScreen(ContentResolver cr) {
		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);

		Cursor cursor = cr.query(CONTENT_URI,
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
	
	/**
	 * create a shortcut in screen
	 * @param context Application context
	 */
	public static void createShortcut(Context context){
		Intent installShortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
				R.drawable.ic_launcher);// 获取快捷方式图标

		Intent flashlightIntent = new Intent();
		flashlightIntent.setClassName("com.yuri.flashlight",
				"com.yuri.flashlight.MainUIActivity");
		flashlightIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		flashlightIntent.setAction(Intent.ACTION_MAIN);
		flashlightIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		installShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
		installShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
		installShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				flashlightIntent);

		context.sendBroadcast(installShortcut);
	}
	
	/**
	 * remove the shortcut from screen
	 * @param context
	 */
	public static void removeShortcut(Context context){
		Intent uninstallShortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		uninstallShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));

		Intent flashlightIntent = new Intent();
		flashlightIntent.setClassName("com.yuri.flashlight",
				"com.yuri.flashlight.MainUIActivity");

		uninstallShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				flashlightIntent);

		flashlightIntent.setAction(Intent.ACTION_MAIN);
		flashlightIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		context.sendBroadcast(uninstallShortcut);
	}
}
