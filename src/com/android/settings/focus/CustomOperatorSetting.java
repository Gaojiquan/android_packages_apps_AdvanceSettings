package com.android.settings.focus;

import com.focus.advsettings.R;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;

public class CustomOperatorSetting extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {

	/**
	 * What text to show as carrier label 0: use system default 1: show spn 2:
	 * show plmn 3: show custom string default: 0
	 * 
	 * @hide
	 */
	public static final String CARRIER_LABEL_TYPE = "carrier_label_type";

	/**
	 * The custom string to show as carrier label
	 * 
	 * @hide
	 */
	public static final String CARRIER_LABEL_CUSTOM_STRING = "carrier_label_custom_string";

	private static final String OPERATOR_STYLE = "pref_custom_operator";
	private static final String OPERATOR_TEXT = "operator_display";

	private ListPreference mOperatorDisplayStyle;
	private EditTextPreference mOperatorDisplayText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getPreferenceManager() != null) {
			addPreferencesFromResource(R.xml.operator_preferences);
			PreferenceScreen prefSet = getPreferenceScreen();

			mOperatorDisplayStyle = (ListPreference) prefSet
					.findPreference(OPERATOR_STYLE);
			mOperatorDisplayStyle.setOnPreferenceChangeListener(this);

			mOperatorDisplayText = (EditTextPreference) prefSet
					.findPreference(OPERATOR_TEXT);
			mOperatorDisplayText.setOnPreferenceChangeListener(this);

			if (mOperatorDisplayText != null) {
				String operLabel = mOperatorDisplayText.getText();
				if (TextUtils.isEmpty(operLabel)) {
					mOperatorDisplayText
							.setSummary(getString(R.string.operator_display_summary));
				} else {
					mOperatorDisplayText.setSummary(mOperatorDisplayText
							.getText());
				}
			}

			int index = Settings.System.getInt(getContentResolver(),
					CARRIER_LABEL_TYPE, 0);
			index = index > (mOperatorDisplayStyle.getEntries().length - 1) ? 0
					: index;
			mOperatorDisplayStyle
					.setSummary(mOperatorDisplayStyle.getEntries()[index]);

			mOperatorDisplayText.setEnabled(index == 3);
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference == mOperatorDisplayStyle) {
			int value = Integer.valueOf((String) newValue);
			int index = mOperatorDisplayStyle
					.findIndexOfValue((String) newValue);

			mOperatorDisplayStyle
					.setSummary(mOperatorDisplayStyle.getEntries()[index]);
			Settings.System.putInt(getContentResolver(), CARRIER_LABEL_TYPE,
					value);

			mOperatorDisplayText.setEnabled(index == 3);

			return true;
		} else if (preference == mOperatorDisplayText) {
			String operLabel = newValue.toString();
			mOperatorDisplayText.setSummary(newValue.toString());
			Settings.System.putString(getContentResolver(),
					CARRIER_LABEL_CUSTOM_STRING, operLabel);
			return true;
		}
		return false;
	}

}
