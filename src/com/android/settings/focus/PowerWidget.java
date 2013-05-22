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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.focus.advsettings.R;

public class PowerWidget extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {
	private static final String TAG = "PowerWidget";
	private static final String SEPARATOR = "OV=I=XseparatorX=I=VO";
	private static final String UI_EXP_WIDGET = "expanded_widget";
	private static final String UI_EXP_WIDGET_HIDE_ONCHANGE = "expanded_hide_onchange";
	private static final String UI_EXP_WIDGET_HIDE_SCROLLBAR = "expanded_hide_scrollbar";

	private CheckBoxPreference mPowerWidget;
	private CheckBoxPreference mPowerWidgetHideOnChange;
	private CheckBoxPreference mPowerWidgetHideScrollBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getPreferenceManager() != null) {
			addPreferencesFromResource(R.xml.power_widget_settings);

			PreferenceScreen prefSet = getPreferenceScreen();

			mPowerWidget = (CheckBoxPreference) prefSet
					.findPreference(UI_EXP_WIDGET);
			mPowerWidgetHideOnChange = (CheckBoxPreference) prefSet
					.findPreference(UI_EXP_WIDGET_HIDE_ONCHANGE);
			mPowerWidgetHideScrollBar = (CheckBoxPreference) prefSet
					.findPreference(UI_EXP_WIDGET_HIDE_SCROLLBAR);

			mPowerWidget.setChecked((Settings.System.getInt(
					getContentResolver(), "expanded_view_widget", 1) == 1));
			mPowerWidgetHideOnChange.setChecked((Settings.System.getInt(
					getContentResolver(), "expanded_hide_onchange", 0) == 1));
			mPowerWidgetHideScrollBar.setChecked((Settings.System.getInt(
					getContentResolver(), "expanded_hide_scrollbar", 0) == 1));
		}
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		boolean value;

		if (preference == mPowerWidget) {
			value = mPowerWidget.isChecked();
			Settings.System.putInt(getContentResolver(),
					"expanded_view_widget", value ? 1 : 0);
		} else if (preference == mPowerWidgetHideOnChange) {
			value = mPowerWidgetHideOnChange.isChecked();
			Settings.System.putInt(getContentResolver(),
					"expanded_hide_onchange", value ? 1 : 0);
		} else if (preference == mPowerWidgetHideScrollBar) {
			value = mPowerWidgetHideScrollBar.isChecked();
			Settings.System.putInt(getContentResolver(),
					"expanded_hide_scrollbar", value ? 1 : 0);
		} else {
			startPreferencePanel(preference.getFragment(), null,
					preference.getTitleRes(), preference.getTitle(), null, 0);
		}

		return true;
	}

	public static class PowerWidgetChooser extends PreferenceFragment implements
			Preference.OnPreferenceChangeListener {

		public PowerWidgetChooser() {
		}

		private static final String TAG = "PowerWidgetActivity";

		private static final String BUTTONS_CATEGORY = "pref_buttons";
		private static final String BUTTON_MODES_CATEGORY = "pref_buttons_modes";
		private static final String SELECT_BUTTON_KEY_PREFIX = "pref_button_";

		private static final String EXP_BRIGHTNESS_MODE = "pref_brightness_mode";
		private static final String EXP_NETWORK_MODE = "pref_network_mode";
		private static final String EXP_SCREENTIMEOUT_MODE = "pref_screentimeout_mode";
		private static final String EXP_RING_MODE = "pref_ring_mode";
		private static final String EXP_FLASH_MODE = "pref_flash_mode";

		private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

		ListPreference mFlashMode;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			addPreferencesFromResource(R.xml.power_widget);

			PreferenceScreen prefSet = getPreferenceScreen();

			if (getActivity().getApplicationContext() == null) {
				return;
			}

			mFlashMode = (ListPreference) prefSet
					.findPreference(EXP_FLASH_MODE);
			mFlashMode.setOnPreferenceChangeListener(this);

			// TODO: set the default values of the items

			// Update the summary text
			mFlashMode.setSummary(mFlashMode.getEntry());

			// Add the available buttons to the list
			PreferenceCategory prefButtons = (PreferenceCategory) prefSet
					.findPreference(BUTTONS_CATEGORY);

			// Add the available mode buttons, incase they need to be removed
			// later
			PreferenceCategory prefButtonsModes = (PreferenceCategory) prefSet
					.findPreference(BUTTON_MODES_CATEGORY);

			// empty our preference category and set it to order as added
			prefButtons.removeAll();
			prefButtons.setOrderingAsAdded(false);

			// emtpy our checkbox map
			mCheckBoxPrefs.clear();

			// get our list of buttons
			ArrayList<String> buttonList = PowerWidgetUtil
					.getButtonListFromString(PowerWidgetUtil
							.getCurrentButtons(getActivity()
									.getApplicationContext()));

			// Don't show mobile data options if not supported
			boolean isMobileData = getActivity().getPackageManager()
					.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
			if (!isMobileData) {
				PowerWidgetUtil.BUTTONS
						.remove(PowerWidgetUtil.BUTTON_MOBILEDATA);
				PowerWidgetUtil.BUTTONS
						.remove(PowerWidgetUtil.BUTTON_NETWORKMODE);
				PowerWidgetUtil.BUTTONS.remove(PowerWidgetUtil.BUTTON_WIFIAP);
			}

			// fill that checkbox map!
			for (PowerWidgetUtil.ButtonInfo button : PowerWidgetUtil.BUTTONS
					.values()) {
				// create a checkbox
				CheckBoxPreference cb = new CheckBoxPreference(getActivity()
						.getApplicationContext());

				// set a dynamic key based on button id
				cb.setKey(SELECT_BUTTON_KEY_PREFIX + button.getId());

				// set vanity info
				cb.setTitle(button.getTitleResId());

				// set our checked state
				if (buttonList.contains(button.getId())) {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}

				// add to our prefs set
				mCheckBoxPrefs.put(cb, button.getId());
				// add to the category
				prefButtons.addPreference(cb);
			}
		}

		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
			// we only modify the button list if it was one of our checks that
			// was clicked
			boolean buttonWasModified = false;
			ArrayList<String> buttonList = new ArrayList<String>();
			for (Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs
					.entrySet()) {
				if (entry.getKey().isChecked()) {
					buttonList.add(entry.getValue());
				}

				if (preference == entry.getKey()) {
					buttonWasModified = true;
				}
			}

			if (buttonWasModified) {
				// now we do some wizardry and reset the button list
				PowerWidgetUtil.saveCurrentButtons(getActivity()
						.getApplicationContext(), PowerWidgetUtil
						.mergeInNewButtonString(PowerWidgetUtil
								.getCurrentButtons(getActivity()
										.getApplicationContext()),
								PowerWidgetUtil
										.getButtonStringFromList(buttonList)));
				return true;
			}

			return false;
		}

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference == mFlashMode) {
				int value = Integer.valueOf((String) newValue);
				int index = mFlashMode.findIndexOfValue((String) newValue);
				Settings.System.putInt(getActivity().getApplicationContext()
						.getContentResolver(), "expanded_flash_mode", value);
				mFlashMode.setSummary(mFlashMode.getEntries()[index]);
			}
			return true;
		}

		public static String[] parseStoredValue(CharSequence val) {
			if (TextUtils.isEmpty(val)) {
				return null;
			} else {
				return val.toString().split(SEPARATOR);
			}
		}

	}

	public static class PowerWidgetOrder extends ListFragment {
		private static final String TAG = "PowerWidgetOrderActivity";

		private ListView mButtonList;
		private ButtonAdapter mButtonAdapter;
		View mContentView = null;
		Context mContext;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mContentView = inflater.inflate(
					R.layout.order_power_widget_buttons_activity, null);
			return mContentView;
		}

		/** Called when the activity is first created. */
		// @Override
		// public void onCreate(Bundle icicle)
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			mContext = getActivity().getApplicationContext();

			mButtonList = getListView();
			((TouchInterceptor) mButtonList).setDropListener(mDropListener);
			mButtonAdapter = new ButtonAdapter(mContext);
			setListAdapter(mButtonAdapter);
		}

		@Override
		public void onDestroy() {
			((TouchInterceptor) mButtonList).setDropListener(null);
			setListAdapter(null);
			super.onDestroy();
		}

		@Override
		public void onResume() {
			super.onResume();
			// reload our buttons and invalidate the views for redraw
			mButtonAdapter.reloadButtons();
			mButtonList.invalidateViews();
		}

		private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
			public void drop(int from, int to) {
				// get the current button list
				ArrayList<String> buttons = PowerWidgetUtil
						.getButtonListFromString(PowerWidgetUtil
								.getCurrentButtons(mContext));

				// move the button
				if (from < buttons.size()) {
					String button = buttons.remove(from);

					if (to <= buttons.size()) {
						buttons.add(to, button);

						// save our buttons
						PowerWidgetUtil.saveCurrentButtons(mContext,
								PowerWidgetUtil
										.getButtonStringFromList(buttons));

						// tell our adapter/listview to reload
						mButtonAdapter.reloadButtons();
						mButtonList.invalidateViews();
					}
				}
			}
		};

		private class ButtonAdapter extends BaseAdapter {
			private Context mContext;
			private Resources mSystemUIResources = null;
			private LayoutInflater mInflater;
			private ArrayList<PowerWidgetUtil.ButtonInfo> mButtons;

			public ButtonAdapter(Context c) {
				mContext = c;
				mInflater = LayoutInflater.from(mContext);

				PackageManager pm = mContext.getPackageManager();
				if (pm != null) {
					try {
						mSystemUIResources = pm
								.getResourcesForApplication("com.focus.SharedSystemUI");
					} catch (Exception e) {
						mSystemUIResources = null;
						Log.e(TAG, "Could not load SystemUI resources", e);
					}
				}

				reloadButtons();
			}

			public void reloadButtons() {
				ArrayList<String> buttons = PowerWidgetUtil
						.getButtonListFromString(PowerWidgetUtil
								.getCurrentButtons(mContext));

				mButtons = new ArrayList<PowerWidgetUtil.ButtonInfo>();
				for (String button : buttons) {
					if (PowerWidgetUtil.BUTTONS.containsKey(button)) {
						mButtons.add(PowerWidgetUtil.BUTTONS.get(button));
					}
				}
			}

			public int getCount() {
				return mButtons.size();
			}

			public Object getItem(int position) {
				return mButtons.get(position);
			}

			public long getItemId(int position) {
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				final View v;
				if (convertView == null) {
					v = mInflater.inflate(
							R.layout.order_power_widget_button_list_item, null);
				} else {
					v = convertView;
				}

				PowerWidgetUtil.ButtonInfo button = mButtons.get(position);

				final TextView name = (TextView) v.findViewById(R.id.name);
				final ImageView icon = (ImageView) v.findViewById(R.id.icon);

				name.setText(button.getTitleResId());

				// assume no icon first
				icon.setVisibility(View.GONE);

				// attempt to load the icon for this button
				if (mSystemUIResources != null) {
					int resId = mSystemUIResources.getIdentifier(
							button.getIcon(), null, null);
					if (resId > 0) {
						try {
							Drawable d = mSystemUIResources.getDrawable(resId);
							icon.setVisibility(View.VISIBLE);
							icon.setImageDrawable(d);
						} catch (Exception e) {
							Log.e(TAG, "Error retrieving icon drawable", e);
						}
					}
				}

				return v;
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}