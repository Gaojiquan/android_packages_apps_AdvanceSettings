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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

import com.focus.advsettings.R;

public class DeviceSettings extends FragmentActivity {

	public static final String SHARED_PREFERENCES_BASENAME = "com.cyanogenmod.settings.device";
	public static final String ACTION_UPDATE_PREFERENCES = "com.cyanogenmod.settings.device.UPDATE";
	public static final String KEY_MDNIE_SCENARIO = "mdnie_scenario";
	public static final String KEY_MDNIE_MODE = "mdnie_mode";
	public static final String KEY_MDNIE_OUTDOOR = "mdnie_outdoor_mode";
	public static final String KEY_MDNIE_NEGATIVE = "mdnie_negative_mode";
	public static final String KEY_HSPA = "hspa";
	public static final String KEY_USE_GYRO_CALIBRATION = "use_gyro_calibration";
	public static final String KEY_CALIBRATE_GYRO = "calibrate_gyro";
	public static final String KEY_TOUCHSCREEN_SENSITIVITY = "touchscreen_sensitivity";
	public static final String KEY_TOUCHKEY_LIGHT = "touchkey_light";
	public static final String KEY_TOUCHKEY_BLN = "touchkey_bln";
	public static final String KEY_CABC = "cabc";

	public static final String CATEGORY_MDNIE = "mdnie";
	public static final String CATEGORY_SENSORS = "sensors";
	public static final String CATEGORY_TOUCHSCREEN = "touchscreen";
	public static final String CATEGORY_TOUCHKEY = "touchkey";

	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE,
				ActionBar.DISPLAY_SHOW_TITLE);
<<<<<<< HEAD

		// Remove title and icon
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
=======
		bar.setTitle(R.string.app_name);
>>>>>>> f0aea56237ccb42e5e45058a2faa744d3ebd7e8c

		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(
				bar.newTab().setText(R.string.category_generor_title),
				mGeneralFragmentActivity.class, null);
<<<<<<< HEAD
		/**
		 * mTabsAdapter.addTab(
		 * bar.newTab().setText(R.string.category_mdnie_title),
		 * mDNIeFragmentActivity.class, null); mTabsAdapter.addTab(
		 * bar.newTab().setText(R.string.category_sensors_title),
		 * SensorsFragmentActivity.class, null);
		 */
=======
/**
		mTabsAdapter.addTab(
				bar.newTab().setText(R.string.category_mdnie_title),
				mDNIeFragmentActivity.class, null);
		mTabsAdapter.addTab(
				bar.newTab().setText(R.string.category_sensors_title),
				SensorsFragmentActivity.class, null);*/
>>>>>>> f0aea56237ccb42e5e45058a2faa744d3ebd7e8c

		mTabsAdapter.addTab(
				bar.newTab().setText(R.string.hardware_keys_bindings_title),
				HardwareKeys.class, null);

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
	}

	@TargetApi(11)
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@TargetApi(11)
	public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		@TargetApi(11)
		public TabsAdapter(Activity activity, ViewPager pager) {
			super(activity.getFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		@TargetApi(11)
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@TargetApi(11)
		@Override
		public int getCount() {
			return mTabs.size();
		}

		@TargetApi(11)
		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onPageScrollStateChanged(int state) {
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@TargetApi(11)
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@TargetApi(11)
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
