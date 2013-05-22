/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.focus;

import java.util.Collections;
import java.util.List;

import com.focus.advsettings.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class HardwareKeys extends PreferenceFragment implements
		OnPreferenceChangeListener {

	private static final String HARDWARE_KEYS_CATEGORY_BINDINGS = "hardware_keys_bindings";
	private static final String HARDWARE_KEYS_ENABLE_CUSTOM = "hardware_keys_enable_custom";
	private static final String HARDWARE_KEYS_HOME_LONG_PRESS = "hardware_keys_home_long_press";
	private static final String HARDWARE_KEYS_MENU_PRESS = "hardware_keys_menu_press";
	private static final String HARDWARE_KEYS_MENU_LONG_PRESS = "hardware_keys_menu_long_press";
	private static final String HARDWARE_KEYS_ASSIST_PRESS = "hardware_keys_assist_press";
	private static final String HARDWARE_KEYS_ASSIST_LONG_PRESS = "hardware_keys_assist_long_press";
	private static final String HARDWARE_KEYS_APP_SWITCH_PRESS = "hardware_keys_app_switch_press";
	private static final String HARDWARE_KEYS_APP_SWITCH_LONG_PRESS = "hardware_keys_app_switch_long_press";
	private static final String HARDWARE_KEYS_SHOW_OVERFLOW = "hardware_keys_show_overflow";

	// Available custom actions to perform on a key press.
	// Must match values for KEY_HOME_LONG_PRESS_ACTION in:
	// frameworks/base/core/java/android/provider/Settings.java
	private static final int ACTION_NOTHING = 0;
	private static final int ACTION_MENU = 1;
	private static final int ACTION_APP_SWITCH = 2;
	private static final int ACTION_SEARCH = 3;
	private static final int ACTION_VOICE_SEARCH = 4;
	private static final int ACTION_IN_APP_SEARCH = 5;
	private static final int ACTION_CUSTOM_APP = 6;

	// Masks for checking presence of hardware keys.
	// Must match values in frameworks/base/core/res/res/values/config.xml
	private static final int KEY_MASK_HOME = 0x01;
	private static final int KEY_MASK_BACK = 0x02;
	private static final int KEY_MASK_MENU = 0x04;
	private static final int KEY_MASK_ASSIST = 0x08;
	private static final int KEY_MASK_APP_SWITCH = 0x10;

	private CheckBoxPreference mEnableCustomBindings;
	private ListPreference mHomeLongPressAction;
	private ListPreference mMenuPressAction;
	private ListPreference mMenuLongPressAction;
	private ListPreference mAssistPressAction;
	private ListPreference mAssistLongPressAction;
	private ListPreference mAppSwitchPressAction;
	private ListPreference mAppSwitchLongPressAction;
	private CheckBoxPreference mShowActionOverflow;

	private CheckBoxPreference mVolumeWake;
	private CheckBoxPreference mBackbuttonKillApp;
	private CheckBoxPreference mHomeKeyAnswerIncall;

	private static final String KEY_VOLUME_WAKE = "pref_volume_wake";

	private static final String KEY_BACKBUTTON_KILL_APP = "pref_backbutton_kill";
	private static final String KEY_HOME_ANSWER_CALL = "pref_home_answer_incall";

	private List<ResolveInfo> mApps = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final int deviceKeys = 7;
		final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
		final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
		final boolean hasAssistKey = (deviceKeys & KEY_MASK_ASSIST) != 0;
		final boolean hasAppSwitchKey = (deviceKeys & KEY_MASK_APP_SWITCH) != 0;

		addPreferencesFromResource(R.xml.hardware_keys);
		PreferenceScreen prefSet = getPreferenceScreen();

		mEnableCustomBindings = (CheckBoxPreference) prefSet
				.findPreference(HARDWARE_KEYS_ENABLE_CUSTOM);
		mHomeLongPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_HOME_LONG_PRESS);
		mMenuPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_MENU_PRESS);
		mMenuLongPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_MENU_LONG_PRESS);
		mAssistPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_ASSIST_PRESS);
		mAssistLongPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_ASSIST_LONG_PRESS);
		mAppSwitchPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_APP_SWITCH_PRESS);
		mAppSwitchLongPressAction = (ListPreference) prefSet
				.findPreference(HARDWARE_KEYS_APP_SWITCH_LONG_PRESS);
		mShowActionOverflow = (CheckBoxPreference) prefSet
				.findPreference(HARDWARE_KEYS_SHOW_OVERFLOW);
		PreferenceCategory bindingsCategory = (PreferenceCategory) prefSet
				.findPreference(HARDWARE_KEYS_CATEGORY_BINDINGS);

		if (hasHomeKey) {
			int homeLongPressAction;
			if (hasAppSwitchKey) {
				homeLongPressAction = Settings.System.getInt(getActivity()
						.getContentResolver(), "key_home_long_press_action",
						ACTION_NOTHING);
			} else {
				homeLongPressAction = Settings.System.getInt(getActivity()
						.getContentResolver(), "key_home_long_press_action",
						ACTION_APP_SWITCH);
			}
			mHomeLongPressAction
					.setValue(Integer.toString(homeLongPressAction));

			if (homeLongPressAction == ACTION_CUSTOM_APP) {

				String label = getInfo("key_home_long_press_custom_action");
				mHomeLongPressAction.setSummary(label);
			} else {

				mHomeLongPressAction
						.setSummary(mHomeLongPressAction.getEntry());
			}

			mHomeLongPressAction.setOnPreferenceChangeListener(this);
		} else {
			bindingsCategory.removePreference(mHomeLongPressAction);
		}

		if (hasMenuKey) {
			int menuPressAction = Settings.System.getInt(getActivity()
					.getContentResolver(), "key_menu_action", ACTION_MENU);
			mMenuPressAction.setValue(Integer.toString(menuPressAction));
			mMenuPressAction.setSummary(mMenuPressAction.getEntry());
			mMenuPressAction.setOnPreferenceChangeListener(this);

			int menuLongPressAction;
			if (hasAssistKey) {
				menuLongPressAction = Settings.System.getInt(getActivity()
						.getContentResolver(), "key_menu_long_press_action",
						ACTION_NOTHING);
			} else {
				menuLongPressAction = Settings.System.getInt(getActivity()
						.getContentResolver(), "key_menu_long_press_action",
						ACTION_SEARCH);
			}
			mMenuLongPressAction
					.setValue(Integer.toString(menuLongPressAction));

			if (menuLongPressAction == ACTION_CUSTOM_APP) {

				String label = getInfo("key_menu_long_press_custom_action");
				mMenuLongPressAction.setSummary(label);
			} else {

				mMenuLongPressAction
						.setSummary(mMenuLongPressAction.getEntry());
			}

			mMenuLongPressAction.setOnPreferenceChangeListener(this);
		} else {
			bindingsCategory.removePreference(mMenuPressAction);
			bindingsCategory.removePreference(mMenuLongPressAction);
		}

		if (hasAssistKey) {
			int assistPressAction = Settings.System.getInt(getActivity()
					.getContentResolver(), "key_assist_action", ACTION_SEARCH);
			mAssistPressAction.setValue(Integer.toString(assistPressAction));
			mAssistPressAction.setSummary(mAssistPressAction.getEntry());
			mAssistPressAction.setOnPreferenceChangeListener(this);

			int assistLongPressAction = Settings.System.getInt(getActivity()
					.getContentResolver(), "key_assist_long_press_action",
					ACTION_VOICE_SEARCH);
			mAssistLongPressAction.setValue(Integer
					.toString(assistLongPressAction));

			if (assistLongPressAction == ACTION_CUSTOM_APP) {

				String label = getInfo("key_assist_long_press_custom_action");
				mAssistLongPressAction.setSummary(label);
			} else {

				mAssistLongPressAction.setSummary(mAssistLongPressAction
						.getEntry());
			}

			mAssistLongPressAction.setOnPreferenceChangeListener(this);
		} else {
			bindingsCategory.removePreference(mAssistPressAction);
			bindingsCategory.removePreference(mAssistLongPressAction);
		}

		if (hasAppSwitchKey) {
			int appSwitchPressAction = Settings.System.getInt(getActivity()
					.getContentResolver(), "key_app_switch_action",
					ACTION_APP_SWITCH);
			mAppSwitchPressAction.setValue(Integer
					.toString(appSwitchPressAction));
			mAppSwitchPressAction.setSummary(mAppSwitchPressAction.getEntry());
			mAppSwitchPressAction.setOnPreferenceChangeListener(this);

			int appSwitchLongPressAction = Settings.System.getInt(getActivity()
					.getContentResolver(), "key_app_switch_long_press_action",
					ACTION_NOTHING);
			mAppSwitchLongPressAction.setValue(Integer
					.toString(appSwitchLongPressAction));

			if (appSwitchLongPressAction == ACTION_CUSTOM_APP) {

				String label = getInfo("key_app_switch_long_press_custom_action");
				mAppSwitchLongPressAction.setSummary(label);
			} else {

				mAppSwitchLongPressAction.setSummary(mAppSwitchLongPressAction
						.getEntry());
			}

			mAppSwitchLongPressAction.setOnPreferenceChangeListener(this);
		} else {
			bindingsCategory.removePreference(mAppSwitchPressAction);
			bindingsCategory.removePreference(mAppSwitchLongPressAction);
		}

		mEnableCustomBindings.setChecked((Settings.System.getInt(getActivity()
				.getApplicationContext().getContentResolver(),
				"hardware_key_rebinding", 0) == 1));
		mShowActionOverflow.setChecked((Settings.System.getInt(getActivity()
				.getApplicationContext().getContentResolver(),
				"ui_force_overflow_button", 0) == 1));

		mVolumeWake = (CheckBoxPreference) findPreference(KEY_VOLUME_WAKE);
		if (mVolumeWake != null) {
			mVolumeWake.setChecked(Settings.System.getInt(getActivity()
					.getContentResolver(), "volume_wake_screen", 0) == 1);
		}

		mBackbuttonKillApp = (CheckBoxPreference) findPreference(KEY_BACKBUTTON_KILL_APP);
		if (mBackbuttonKillApp != null) {
			mBackbuttonKillApp.setChecked(Settings.System.getInt(getActivity()
					.getContentResolver(), "kill_app_longpress_back", 0) == 1);
		}

		mHomeKeyAnswerIncall = (CheckBoxPreference) findPreference(KEY_HOME_ANSWER_CALL);
		if (mHomeKeyAnswerIncall != null) {

			final int incallHomeBehavior = Settings.System.getInt(getActivity()
					.getContentResolver(), "ring_home_button_behavior", 1);
			final boolean homeButtonAnswersCall = (incallHomeBehavior == 2);
			mHomeKeyAnswerIncall.setChecked(homeButtonAnswersCall);
		}
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mHomeLongPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mHomeLongPressAction
					.findIndexOfValue((String) newValue);

			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {
				mHomeLongPressAction.setSummary(mHomeLongPressAction
						.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_home_long_press_action", value);
			}

			return true;
		} else if (preference == mMenuPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mMenuPressAction.findIndexOfValue((String) newValue);

			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {

				mMenuPressAction
						.setSummary(mMenuPressAction.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_menu_action", value);
			}

			return true;
		} else if (preference == mMenuLongPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mMenuLongPressAction
					.findIndexOfValue((String) newValue);

			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {

				mMenuLongPressAction.setSummary(mMenuLongPressAction
						.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_menu_long_press_action", value);
			}

			return true;
		} else if (preference == mAssistPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mAssistPressAction.findIndexOfValue((String) newValue);
			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {
				mAssistPressAction
						.setSummary(mAssistPressAction.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_assist_action", value);
			}

			return true;
		} else if (preference == mAssistLongPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mAssistLongPressAction
					.findIndexOfValue((String) newValue);
			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {
				mAssistLongPressAction.setSummary(mAssistLongPressAction
						.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_assist_long_press_action", value);
			}

			return true;
		} else if (preference == mAppSwitchPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mAppSwitchPressAction
					.findIndexOfValue((String) newValue);
			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {
				mAppSwitchPressAction.setSummary(mAppSwitchPressAction
						.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_app_switch_action", value);
			}

			return true;
		} else if (preference == mAppSwitchLongPressAction) {
			int value = Integer.valueOf((String) newValue);
			int index = mAppSwitchLongPressAction
					.findIndexOfValue((String) newValue);
			if (index == ACTION_CUSTOM_APP) {
				this.showPickupDialog(preference);
			} else {
				mAppSwitchLongPressAction.setSummary(mAppSwitchLongPressAction
						.getEntries()[index]);
				Settings.System.putInt(getActivity().getContentResolver(),
						"key_app_switch_long_press_action", value);
			}

			return true;
		}
		return false;
	}

	@SuppressLint("NewApi")
	protected void showPickupDialog(final Preference preference) {

		AlertDialog.Builder builder = new Builder(getActivity());

		mApps = getData();

		final AppInfoAdapter adapter = new AppInfoAdapter(getActivity());

		builder.setSingleChoiceItems(adapter, 1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Context context = preference.getContext();
						ResolveInfo info = (ResolveInfo) adapter.getItem(arg1);

						if (preference == mHomeLongPressAction) {

							mHomeLongPressAction.setSummary(info
									.loadLabel(context.getPackageManager()));
							setInfo(info, "key_home_long_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_home_long_press_action",
									ACTION_CUSTOM_APP);

						} else if (preference == mMenuPressAction) {

							mMenuPressAction.setSummary(info.loadLabel(context
									.getPackageManager()));
							setInfo(info, "key_menu_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_menu_press_action", ACTION_CUSTOM_APP);

						} else if (preference == mMenuLongPressAction) {

							mMenuLongPressAction.setSummary(info
									.loadLabel(context.getPackageManager()));
							setInfo(info, "key_menu_long_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_menu_long_press_action",
									ACTION_CUSTOM_APP);

						} else if (preference == mAssistPressAction) {

							mAssistPressAction.setSummary(info
									.loadLabel(context.getPackageManager()));
							setInfo(info, "key_assist_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_assist_press_action",
									ACTION_CUSTOM_APP);

						} else if (preference == mAssistLongPressAction) {

							mAssistLongPressAction.setSummary(info
									.loadLabel(context.getPackageManager()));
							setInfo(info, "key_assist_long_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_assist_long_press_action",
									ACTION_CUSTOM_APP);

						} else if (preference == mAppSwitchPressAction) {

							mAppSwitchPressAction.setSummary(info
									.loadLabel(context.getPackageManager()));
							setInfo(info, "key_app_switch_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_app_switch_press_action",
									ACTION_CUSTOM_APP);

						} else if (preference == mAppSwitchLongPressAction) {

							mAppSwitchLongPressAction.setSummary(info
									.loadLabel(context.getPackageManager()));
							setInfo(info,
									"key_app_switch_long_press_custom_action");

							Settings.System.putInt(
									context.getContentResolver(),
									"key_app_switch_long_press_action",
									ACTION_CUSTOM_APP);
						}

						arg0.dismiss();
					}
				});

		builder.setTitle(R.string.pick_up_app);
		builder.show();

	}

	private void setInfo(ResolveInfo info, String key) {
		String action = String.format("%s;%s",
				info.activityInfo.applicationInfo.packageName,
				info.activityInfo.name);

		Settings.System.putString(getActivity().getContentResolver(), key,
				action);

	}

	private String getInfo(String key) {
		String result = "";

		String formatString = Settings.System.getString(getActivity()
				.getContentResolver(), key);
		String[] paramArr = formatString.split(";");
		if (paramArr.length >= 2) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName(paramArr[0], paramArr[1]);
			final ResolveInfo resolveInfo = getActivity().getPackageManager()
					.resolveActivity(intent, 0);

			CharSequence label = "";
			if (resolveInfo != null) {
				label = resolveInfo
						.loadLabel(getActivity().getPackageManager());
			} else {
				label = getActivity().getText(R.string.custom_app_not_found);
			}

			result = String.format(
					"%s: %s",
					getActivity().getText(
							R.string.hardware_keys_action_custom_app), label);
		}

		return result;
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == mEnableCustomBindings) {
			Settings.System.putInt(getActivity().getContentResolver(),
					"hardware_key_rebinding",
					mEnableCustomBindings.isChecked() ? 1 : 0);
			return true;
		} else if (preference == mShowActionOverflow) {
			boolean enabled = mShowActionOverflow.isChecked();
			Settings.System.putInt(getActivity().getContentResolver(),
					"ui_force_overflow_button", enabled ? 1 : 0);
			// Show appropriate
			if (enabled) {
				Toast.makeText(getActivity(),
						R.string.hardware_keys_show_overflow_toast_enable,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(),
						R.string.hardware_keys_show_overflow_toast_disable,
						Toast.LENGTH_LONG).show();
			}
			return true;
		} else if (preference == mVolumeWake) {
			Settings.System.putInt(getActivity().getContentResolver(),
					"volume_wake_screen", mVolumeWake.isChecked() ? 1 : 0);
			return true;
		} else if (preference == mBackbuttonKillApp) {
			Settings.System.putInt(getActivity().getContentResolver(),
					"kill_app_longpress_back",
					mBackbuttonKillApp.isChecked() ? 1 : 0);
			return true;
		} else if (preference == mHomeKeyAnswerIncall) {

			Settings.System.putInt(getActivity().getContentResolver(),
					"ring_home_button_behavior",
					(mHomeKeyAnswerIncall.isChecked() ? 2 : 1));
		}
		return false;
	}

	private List<ResolveInfo> getData() {

		List<ResolveInfo> data = null;
		PackageManager pManager = getActivity().getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		data = pManager.queryIntentActivities(intent, 0);
		Collections.sort(data, new ResolveInfo.DisplayNameComparator(
				getActivity().getPackageManager()));

		return data;
	}

	public class AppInfo {

		CharSequence label;
		CharSequence dbFormat;
	}

	public class AppInfoAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private PackageManager mManager;

		public AppInfoAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			this.mManager = context.getPackageManager();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mApps.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mApps.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = mInflater.inflate(R.layout.app_item, null);
			ImageView icon = (ImageView) convertView.findViewById(R.id.img);
			TextView label = (TextView) convertView.findViewById(R.id.title);
			TextView info = (TextView) convertView.findViewById(R.id.info);

			ResolveInfo resolveInfo = (ResolveInfo) mApps.get(position);

			icon.setImageDrawable(resolveInfo.loadIcon(mManager));
			label.setText(resolveInfo.loadLabel(mManager));
			info.setText(resolveInfo.activityInfo.packageName);

			return convertView;
		}
	}
}
