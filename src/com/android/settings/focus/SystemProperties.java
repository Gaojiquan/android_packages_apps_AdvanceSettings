package com.android.settings.focus;

import java.lang.reflect.Method;

public class SystemProperties {

	private static Method mGetObjectMethod = null;
	private static Method mSetObjectMethod = null;

	public static Object get(String key, Object def) {
		try {

			// this field is not available in Android SDK
			if (mGetObjectMethod == null) {
				mGetObjectMethod = Class.forName("android.os.SystemProperties")
						.getMethod("get",
								new Class[] { String.class, String.class });
			}
			return mGetObjectMethod.invoke(null, new Object[] { key, def });
		} catch (Exception e) {
			throw new RuntimeException("Platform error", e);
		}
	}

	public static void set(String key, String def) {
		try {

			// this field is not available in Android SDK
			if (mSetObjectMethod == null) {
				mSetObjectMethod = Class.forName("android.os.SystemProperties")
						.getMethod("set",
								new Class[] { String.class, String.class });
			}
			mSetObjectMethod.invoke(null, new Object[] { key, def });
		} catch (Exception e) {
			throw new RuntimeException("Platform error", e);
		}

	}

}
