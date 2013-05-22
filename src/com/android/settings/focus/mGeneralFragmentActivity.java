package com.android.settings.focus;

import com.focus.advsettings.R;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

@TargetApi(11)
public class mGeneralFragmentActivity extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener {

	public static final String INTENT_ENABLE_DIGITAL_AUDIO = "com.cyanogenmod.dockaudio.ENABLE_DIGITAL_AUDIO";

	public static final String EXPANDED_VIEW_WIDGET = "expanded_view_widget";
	public static final String EXPANDED_VIEW_WIDGET_BRIGHTNESS = "expanded_view_widget_brightness";
	public static final String WIDGET_BUTTONS = "expanded_widget_buttons";
	public static final String FLASH_SERVICE = "android.appwidget.action.APPWIDGET_UPDATE";

	private static final String OPERATOR_DISPLAY = "operator_display";
	private static final String CUSTOM_SPNNAME = "custom_spn_name";
	private static final String CDMA_SIGNAL_DISPLAY_PATTERN = "cdma_signal_display_pattern";
	private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";
	private static final String STATUS_BAR_AM_PM_STYLE = "status_bar_am_pm_style";
	private static final String ACTION_CHANGE_CDMA_SIGNAL_DISPLAY = "android.intent.action.CHANGE_CDMA_SIGNAL_DISPLAY";

	private static final String SIGNAL_DISPLAY_PATTERN = "status_bar_signal";

	private static final String STATUS_BAR_BATTERY = "status_bar_battery";
	private static final String STATUS_BAR_CLOCK = "status_bar_show_clock";

	private ListPreference mStatusBarAmPm;
	private ListPreference mStatusBarAmPmStyle;
	private EditTextPreference mOperatorPref;
	private CheckBoxPreference mCdmaSignalPref;
	private CheckBoxPreference mSignalTypePref;
	private CheckBoxPreference mStatusBarClock;
	private ListPreference mStatusBarBattery;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String customSpnName = "custom_spn_name";

		addPreferencesFromResource(R.xml.generor_preferences);

		mCdmaSignalPref = (CheckBoxPreference) getPreferenceScreen()
				.findPreference("cdma_signal_display");
		if (mCdmaSignalPref != null) {
			mCdmaSignalPref.setOnPreferenceChangeListener(this);
		}

		mSignalTypePref = (CheckBoxPreference) getPreferenceScreen()
				.findPreference("signal_display");
		if (mSignalTypePref != null) {
			mSignalTypePref.setOnPreferenceChangeListener(this);
		}

		mStatusBarAmPm = (ListPreference) getPreferenceScreen().findPreference(
				STATUS_BAR_AM_PM);
		mStatusBarAmPmStyle = (ListPreference) getPreferenceScreen()
				.findPreference(STATUS_BAR_AM_PM_STYLE);

		if (mOperatorPref != null) {
			mOperatorPref = (EditTextPreference) findPreference(OPERATOR_DISPLAY);
			String operLabel = mOperatorPref.getText();
			if (TextUtils.isEmpty(operLabel)) {
				mOperatorPref
						.setSummary(getString(R.string.operator_display_summary));
			} else {
				mOperatorPref.setSummary(mOperatorPref.getText());
			}
			mOperatorPref.setOnPreferenceChangeListener(this);
		}

		try {
			if (Settings.System.getInt(getActivity().getContentResolver(),
					Settings.System.TIME_12_24) == 24) {
				// mStatusBarAmPm.setEnabled(false);
				mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
				// mStatusBarAmPmStyle.setEnabled(false);
				mStatusBarAmPmStyle
						.setSummary(R.string.status_bar_am_pm_summary);
			}
		} catch (SettingNotFoundException e) {
		}

		int statusBarAmPm = Settings.System.getInt(getActivity()
				.getContentResolver(), STATUS_BAR_AM_PM, 2);
		int statusBarAmPmStyle = Settings.System.getInt(getActivity()
				.getContentResolver(), STATUS_BAR_AM_PM_STYLE, 1);
		mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
		mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
		mStatusBarAmPm.setOnPreferenceChangeListener(this);

		mStatusBarAmPmStyle.setValue(String.valueOf(statusBarAmPmStyle));
		mStatusBarAmPmStyle.setSummary(mStatusBarAmPmStyle.getEntry());
		mStatusBarAmPmStyle.setOnPreferenceChangeListener(this);

		mStatusBarClock = (CheckBoxPreference) findPreference(STATUS_BAR_CLOCK);
		mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY);

		int statusBarBattery = Settings.System.getInt(getActivity()
				.getApplicationContext().getContentResolver(),
				"status_bar_battery", 0);
		mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
		mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
		mStatusBarBattery.setOnPreferenceChangeListener(this);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mStatusBarAmPm) {
			int statusBarAmPm = Integer.valueOf((String) newValue);
			int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
			Settings.System.putInt(getActivity().getContentResolver(),
					STATUS_BAR_AM_PM, statusBarAmPm);
			mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
			return true;
		} else if (preference == mStatusBarAmPmStyle) {
			int statusBarAmPmStyle = Integer.valueOf((String) newValue);
			int index = mStatusBarAmPmStyle.findIndexOfValue((String) newValue);
			Settings.System.putInt(getActivity().getContentResolver(),
					STATUS_BAR_AM_PM_STYLE, statusBarAmPmStyle);
			mStatusBarAmPmStyle
					.setSummary(mStatusBarAmPmStyle.getEntries()[index]);
			return true;
		} else if (preference == mOperatorPref) {
			String operLabel = newValue.toString();
			mOperatorPref.setSummary(newValue.toString());
			Settings.System.putString(getActivity().getContentResolver(),
					CUSTOM_SPNNAME, operLabel);
		} else if (preference == mCdmaSignalPref) {

			Settings.System.putInt(getActivity().getContentResolver(),
					CDMA_SIGNAL_DISPLAY_PATTERN, ((Boolean) newValue) ? 1 : 0);
			getActivity().sendBroadcast(
					new Intent(ACTION_CHANGE_CDMA_SIGNAL_DISPLAY));

		} else if (preference == mSignalTypePref) {

			Settings.System.putInt(getActivity().getContentResolver(),
					SIGNAL_DISPLAY_PATTERN, ((Boolean) newValue) ? 1 : 0);

		} else if (preference == mStatusBarBattery) {
			int statusBarBattery = Integer.valueOf((String) newValue);
			int index = mStatusBarBattery.findIndexOfValue((String) newValue);
			Settings.System.putInt(getActivity().getApplicationContext()
					.getContentResolver(), "status_bar_battery",
					statusBarBattery);
			mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
			return true;
		}

		return true;
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		boolean value;
		if (preference == mStatusBarClock) {
			value = mStatusBarClock.isChecked();
			Settings.System.putInt(getActivity().getContentResolver(),
					"status_bar_clock", value ? 1 : 0);
			return true;
		}

		return false;
	}
}