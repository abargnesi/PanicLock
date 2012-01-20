/*
 * PanicLock
 *
 * URLs: http://paniclock.den-4.com/
 * Copyright (C) 2009-2012, Den 4
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.den_4.panicLock.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.den_4.panicLock.R;

/**
 * Utility methods for working with "Panic Lock" application preferences.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class PreferencesUtil {
	public static int getCurrentlySelectedQuickPanicAlarm(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		String quickPanicAlarmValue = preferences.getString(context.getString(R.string.quickPanicAlarm), context.getString(R.string.quickPanicAlarmDefaultValue));
		return getAlarmResourceByLabel(quickPanicAlarmValue);
	}
	
	public static int getCurrentlySelectedDeadMansSwitchAlarm(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		String quickPanicAlarmValue = preferences.getString(context.getString(R.string.deadMansSwitchAlarm), context.getString(R.string.deadMansSwitchAlarmDefaultValue));
		return getAlarmResourceByLabel(quickPanicAlarmValue);
	}
	
	public static int getCurrentlySelectedPanicLockDisturbanceAlarm(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		String quickPanicAlarmValue = preferences.getString(context.getString(R.string.panicLockDisturbanceAlarm), context.getString(R.string.panicLockDisturbanceAlarmDefaultValue));
		return getAlarmResourceByLabel(quickPanicAlarmValue);
	}
	
	public static void disabledPanicLock(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(context.getString(R.string.panicLock), false);
        prefsEditor.commit();
    }
	
	private static Integer getAlarmResourceByLabel(String alarmLabel) { 
		if("Car Alarm".equals(alarmLabel)) {
			return R.raw.car_alarm;
		}
		else if("Default Alarm".equals(alarmLabel)) {
			return R.raw.alarm;
		}
		else if("House Alarm".equals(alarmLabel)) {
			return R.raw.house_alarm;
		}
		else if("Smoke Alarm".equals(alarmLabel)) {
			return R.raw.smoke_alarm;
		}
		
		return null;
	}
}
