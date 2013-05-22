package com.android.settings.focus;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SwitchWidget implements CompoundButton.OnCheckedChangeListener {
	public static Context mContext;
	public Switch mSwitch;
	public AtomicBoolean mConnected = new AtomicBoolean(false);

	public boolean mStateMachineEvent;

	/*
	 * public SwitchWidget(Context context, Switch switch_) { super(context,
	 * switch_); mContext = context; mSwitch = switch_; }
	 */
	public SwitchWidget() {
	}

	@TargetApi(14)
	public void resume() {
		mSwitch.setOnCheckedChangeListener(this);
	}

	@TargetApi(14)
	public void pause() {
		mSwitch.setOnCheckedChangeListener(null);
	}

	@TargetApi(14)
	public void setSwitch(Switch switch_) {
		/* Stub! */
		if (mSwitch == switch_)
			return;
		mSwitch.setOnCheckedChangeListener(null);
		mSwitch = switch_;
		mSwitch.setOnCheckedChangeListener(this);

		setState(switch_);
	}

	@SuppressLint("NewApi")
	public void setState(Switch switch_) {
		/* Stub */
		return;
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		/* Stub! */
		return;
	}
}
