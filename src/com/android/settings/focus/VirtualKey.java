/*
 * Copyright (C) 2011 The CyanogenMod Project
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

public class VirtualKey extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {

	private CheckBoxPreference mFloatWindowPref;
	private CheckBoxPreference mFloatWindowBootOptionPref;
	private CheckBoxPreference mAutoHidePref;
	private ListPreference mOpenStylePref;
	private ListPreference mAutoHidePeroidPref;
	private ListPreference mCircleSizePref;

	private final static String KEY_AUTOHIDE = "vkey_autohide";
	private final static String KEY_AUTOHIDE_PERIOD = "vkey_autohide_period";
	private final static String KEY_OPENONCLICK = "vkey_open_type";
	private final static String KEY_CIRCLE_SIZE = "vkcirclesize";

	public static final String FLOAT_SERVICE = "focus.system.FloatSysPop";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.virtualkey_preferences);

		mFloatWindowPref = (CheckBoxPreference) getPreferenceScreen()
				.findPreference("float_box_on_off");
		mFloatWindowPref.setOnPreferenceChangeListener(this);

		mFloatWindowBootOptionPref = (CheckBoxPreference) getPreferenceScreen()
				.findPreference("float_box_on_off_onboot");
		mFloatWindowBootOptionPref.setOnPreferenceChangeListener(this);

		mAutoHidePref = (CheckBoxPreference) getPreferenceScreen()
				.findPreference("float_box_autohide");
		mAutoHidePref.setOnPreferenceChangeListener(this);

		int value = Settings.System.getInt(getContentResolver(), KEY_AUTOHIDE,
				1);
		mAutoHidePref.setChecked(value == 1);

		mAutoHidePeroidPref = (ListPreference) getPreferenceScreen()
				.findPreference("float_box_autohide_peroid");
		mAutoHidePeroidPref.setOnPreferenceChangeListener(this);

		value = Settings.System.getInt(getContentResolver(),
				KEY_AUTOHIDE_PERIOD, 5000);
		mAutoHidePeroidPref.setValue(Integer.toString(value));
		mAutoHidePeroidPref.setSummary(mAutoHidePeroidPref.getEntry());

		mOpenStylePref = (ListPreference) getPreferenceScreen().findPreference(
				"pref_float_box_expand_style");
		mOpenStylePref.setOnPreferenceChangeListener(this);

		value = Settings.System
				.getInt(getContentResolver(), KEY_OPENONCLICK, 0);
		mOpenStylePref.setValue(Integer.toString(value));
		mOpenStylePref.setSummary(mOpenStylePref.getEntry());

		mCircleSizePref = (ListPreference) getPreferenceScreen()
				.findPreference("float_box_float_box_size");
		mCircleSizePref.setOnPreferenceChangeListener(this);

		value = Settings.System.getInt(getContentResolver(), KEY_CIRCLE_SIZE,
				190);
		mCircleSizePref.setValue(Integer.toString(value));
		mCircleSizePref.setSummary(mCircleSizePref.getEntry());

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference == mFloatWindowPref) {
			boolean floatWindowOnOff = (Boolean) newValue;
			if (floatWindowOnOff) {
				Intent service = new Intent(FLOAT_SERVICE);
				startService(service);
			} else {
				Intent service = new Intent(FLOAT_SERVICE);
				stopService(service);
			}
			return true;
		} else if (preference == mFloatWindowBootOptionPref) {
			return true;
		} else if (preference == mAutoHidePref) {
			boolean value = (Boolean) newValue;
			Settings.System.putInt(getContentResolver(), KEY_AUTOHIDE,
					value ? 1 : 0);
			return true;
		} else if (preference == mOpenStylePref) {
			int value = Integer.valueOf((String) newValue);
			int index = mOpenStylePref.findIndexOfValue((String) newValue);

			mOpenStylePref.setSummary(mOpenStylePref.getEntries()[index]);
			Settings.System
					.putInt(getContentResolver(), KEY_OPENONCLICK, value);
			return true;
		} else if (preference == mAutoHidePeroidPref) {
			int value = Integer.valueOf((String) newValue);
			int index = mAutoHidePeroidPref.findIndexOfValue((String) newValue);

			mAutoHidePeroidPref
					.setSummary(mAutoHidePeroidPref.getEntries()[index]);

			Settings.System.putInt(getContentResolver(), KEY_AUTOHIDE_PERIOD,
					value);

			return true;
		} else if (preference == mCircleSizePref) {
			int value = Integer.valueOf((String) newValue);
			int index = mCircleSizePref.findIndexOfValue((String) newValue);

			mCircleSizePref.setSummary(mCircleSizePref.getEntries()[index]);

			Settings.System
					.putInt(getContentResolver(), KEY_CIRCLE_SIZE, value);

			return true;
		}
		return false;
	}
}