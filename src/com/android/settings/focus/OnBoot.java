package com.android.settings.focus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OnBoot extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		boolean status = sharedPrefs
				.getBoolean("float_box_on_off_onboot", true);
		editor.putBoolean("float_box_on_off", status);
		if (status) {
			Intent service = new Intent(mGeneralFragmentActivity.FLOAT_SERVICE);
			context.startService(service);
		}
		editor.commit();

		intent = new Intent(mGeneralFragmentActivity.FLASH_SERVICE);
		context.sendBroadcast(intent);

		/*
		 * mDNIeScenario.restore(context); mDNIeMode.restore(context);
		 * mDNIeOutdoor.restore(context); mDNIeNegative.restore(context);
		 * CABC.restore(context); SensorsFragmentActivity.restore(context);
		 */
	}
}
