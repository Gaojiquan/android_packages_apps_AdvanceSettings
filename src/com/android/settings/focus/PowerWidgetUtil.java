/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.focus;

import com.focus.advsettings.R;

import android.content.Context;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * THIS CLASS'S DATA MUST BE KEPT UP-TO-DATE WITH THE DATA IN
 * com.android.systemui.statusbar.powerwidget.PowerWidget AND
 * com.android.systemui.statusbar.powerwidget.PowerButton IN THE SystemUI
 * PACKAGE.
 */
public class PowerWidgetUtil {
	public static final String BUTTON_WIFI = "Wifi";
	public static final String BUTTON_WIFI_DISPLAY = "WifiDisplay";
	public static final String BUTTON_GPS = "Location";
	public static final String BUTTON_BLUETOOTH = "Bluetooth";
	public static final String BUTTON_SOUND = "SilentMode";
	public static final String BUTTON_SYNC = "Sync";
	public static final String BUTTON_MOBILEDATA = "MobileData";
	public static final String BUTTON_AUTOROTATE = "AutoRotate";
	public static final String BUTTON_AIRPLANE = "AirplaneMode";
	public static final String BUTTON_FLASHLIGHT = "Flash";
	public static final String BUTTON_SLEEP = "Sleep";
	public static final String BUTTON_POWERSAVING = "PowerSaving";

	public static final String BUTTON_DONOTDISTURB = "DoNotDisturb";
	public static final String BUTTON_DRIVINGMODE = "DrivingMode";

	public static final HashMap<String, ButtonInfo> BUTTONS = new HashMap<String, ButtonInfo>();
	static {

		BUTTONS.put(
				BUTTON_WIFI_DISPLAY,
				new PowerWidgetUtil.ButtonInfo(BUTTON_WIFI_DISPLAY,
						R.string.title_toggle_wifidisplay,
						"com.android.systemui:drawable/tw_quick_panel_icon_wifi_display_on"));
		BUTTONS.put(
				BUTTON_DONOTDISTURB,
				new PowerWidgetUtil.ButtonInfo(BUTTON_DONOTDISTURB,
						R.string.title_toggle_donotdisturb,
						"com.android.systemui:drawable/tw_quick_panel_icon_notification_on"));
		BUTTONS.put(BUTTON_DRIVINGMODE, new PowerWidgetUtil.ButtonInfo(
				BUTTON_DRIVINGMODE, R.string.title_toggle_drivingmode,
				"com.android.systemui:drawable/tw_quick_panel_icon_driving_on"));

		BUTTONS.put(
				BUTTON_POWERSAVING,
				new PowerWidgetUtil.ButtonInfo(BUTTON_POWERSAVING,
						R.string.title_toggle_powersaving,
						"com.android.systemui:drawable/tw_quick_panel_icon_powersave_on"));
		BUTTONS.put(
				BUTTON_AIRPLANE,
				new PowerWidgetUtil.ButtonInfo(BUTTON_AIRPLANE,
						R.string.title_toggle_airplane,
						"com.android.systemui:drawable/tw_quick_panel_icon_airplane_on"));
		BUTTONS.put(
				BUTTON_AUTOROTATE,
				new PowerWidgetUtil.ButtonInfo(BUTTON_AUTOROTATE,
						R.string.title_toggle_autorotate,
						"com.android.systemui:drawable/tw_quick_panel_icon_rotation_on"));
		BUTTONS.put(
				BUTTON_BLUETOOTH,
				new PowerWidgetUtil.ButtonInfo(BUTTON_BLUETOOTH,
						R.string.title_toggle_bluetooth,
						"com.android.systemui:drawable/tw_quick_panel_icon_bluetooth_on"));
		BUTTONS.put(BUTTON_FLASHLIGHT, new PowerWidgetUtil.ButtonInfo(
				BUTTON_FLASHLIGHT, R.string.title_toggle_flashlight,
				"com.android.systemui:drawable/stat_flashlight_on"));
		BUTTONS.put(BUTTON_GPS, new PowerWidgetUtil.ButtonInfo(BUTTON_GPS,
				R.string.title_toggle_gps,
				"com.android.systemui:drawable/tw_quick_panel_icon_gps_on"));
		BUTTONS.put(
				BUTTON_MOBILEDATA,
				new PowerWidgetUtil.ButtonInfo(BUTTON_MOBILEDATA,
						R.string.title_toggle_mobiledata,
						"com.android.systemui:drawable/tw_quick_panel_icon_data_connection_on"));
		BUTTONS.put(BUTTON_SLEEP, new PowerWidgetUtil.ButtonInfo(BUTTON_SLEEP,
				R.string.title_toggle_sleep,
				"com.android.systemui:drawable/tw_quick_panel_icon_sleep"));
		BUTTONS.put(BUTTON_SOUND, new PowerWidgetUtil.ButtonInfo(BUTTON_SOUND,
				R.string.title_toggle_sound,
				"com.android.systemui:drawable/tw_quick_panel_icon_silent_on"));
		BUTTONS.put(BUTTON_SYNC, new PowerWidgetUtil.ButtonInfo(BUTTON_SYNC,
				R.string.title_toggle_sync,
				"com.android.systemui:drawable/tw_quick_panel_icon_sync_on"));
		BUTTONS.put(BUTTON_WIFI, new PowerWidgetUtil.ButtonInfo(BUTTON_WIFI,
				R.string.title_toggle_wifi,
				"com.android.systemui:drawable/tw_quick_panel_icon_wifi_on"));
	}

	private static final String BUTTON_DELIMITER = "|";
	private static final String BUTTONS_DEFAULT = BUTTON_WIFI
			+ BUTTON_DELIMITER + BUTTON_BLUETOOTH + BUTTON_DELIMITER
			+ BUTTON_GPS + BUTTON_DELIMITER + BUTTON_SOUND;

	public static String getCurrentButtons(Context context) {
		String buttons = Settings.System.getString(
				context.getContentResolver(),
				mGeneralFragmentActivity.WIDGET_BUTTONS);
		if (buttons == null) {
			buttons = BUTTONS_DEFAULT;
		}
		return buttons;
	}

	public static void saveCurrentButtons(Context context, String buttons) {
		Settings.System.putString(context.getContentResolver(),
				mGeneralFragmentActivity.WIDGET_BUTTONS, buttons);
	}

	public static String mergeInNewButtonString(String oldString,
			String newString) {
		ArrayList<String> oldList = getButtonListFromString(oldString);
		ArrayList<String> newList = getButtonListFromString(newString);
		ArrayList<String> mergedList = new ArrayList<String>();

		// add any items from oldlist that are in new list
		for (String button : oldList) {
			if (newList.contains(button)) {
				mergedList.add(button);
			}
		}

		// append anything in newlist that isn't already in the merged list to
		// the end of the list
		for (String button : newList) {
			if (!mergedList.contains(button)) {
				mergedList.add(button);
			}
		}

		// return merged list
		return getButtonStringFromList(mergedList);
	}

	public static ArrayList<String> getButtonListFromString(String buttons) {
		return new ArrayList<String>(Arrays.asList(buttons.split("\\|")));
	}

	public static String getButtonStringFromList(ArrayList<String> buttons) {
		if (buttons == null || buttons.size() <= 0) {
			return "";
		} else {
			String s = buttons.get(0);
			for (int i = 1; i < buttons.size(); i++) {
				s += BUTTON_DELIMITER + buttons.get(i);
			}
			return s;
		}
	}

	public static class ButtonInfo {
		private String mId;
		private int mTitleResId;
		private String mIcon;

		public ButtonInfo(String id, int titleResId, String icon) {
			mId = id;
			mTitleResId = titleResId;
			mIcon = icon;
		}

		public String getId() {
			return mId;
		}

		public int getTitleResId() {
			return mTitleResId;
		}

		public String getIcon() {
			return mIcon;
		}
	}
}
