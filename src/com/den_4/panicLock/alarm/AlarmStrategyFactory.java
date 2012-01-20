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
package com.den_4.panicLock.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.den_4.panicLock.R;
import com.den_4.panicLock.alarm.service.PanicLockService;
import com.den_4.panicLock.model.PanicThresholds;
import com.den_4.panicLock.util.PreferencesUtil;

/**
 * Strategy to determine what actions to take on press of PANIC button.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class AlarmStrategyFactory {
	public static void processAlarmAction(long clickTime, Activity callingActivity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callingActivity.getApplicationContext());
		Context context = callingActivity.getApplicationContext();
		
		if(PanicThresholds.PANICLOCK_ENABLE_THRESHOLD.withinThreshold(clickTime)) {
			boolean panicLock = prefs.getBoolean(context.getString(R.string.panicLock), false);
			if(panicLock) {
				ToastCountdown.getInstance().cancelCountdown();
				
				AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				am.cancel(PendingIntent.getService(context, 0, new Intent(context, PanicLockService.class), 0));
				
				context.getApplicationContext().stopService(new Intent(context, PanicLockService.class));
				
				popUpPanicButton(callingActivity);
				PreferencesUtil.disabledPanicLock(context);
			}
			else {
				pushDownPanicButton(callingActivity);
				
				Editor prefsEditor = prefs.edit();
				prefsEditor.putBoolean(context.getString(R.string.panicLock), true);
				prefsEditor.commit();

				ToastCountdown.getInstance().runCountdown(3, context);
				
				PendingIntent panicLockServiceIntent = PendingIntent.getService(context, 0, new Intent(context, PanicLockService.class), 0);
	            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+3000, panicLockServiceIntent);
			}
		}
		else if(PanicThresholds.DEAD_MANS_SWITCH_THRESHOLD.withinThreshold(clickTime)) {
			boolean externalAlarm = prefs.getBoolean(context.getString(R.string.externalAlarm), false);
			PanicAlarmNotifier panicAlarm = PanicAlarmNotifier.getInstance(context);
			if(panicAlarm.isInPanicMode()) {
				panicAlarm.stopPanicAlarm();
				context.getApplicationContext().stopService(new Intent(context, PanicLockService.class));
				
				if(externalAlarm) {
					disableExternalAlarm(prefs, context);
				}
			}
			else {
				Integer alarmResource = PreferencesUtil.getCurrentlySelectedDeadMansSwitchAlarm(context);
				
				if(alarmResource != null) {
					panicAlarm.startPanicAlarm(alarmResource);
				}
				
				if(externalAlarm) {
					enableExternalAlarm(prefs, context);
				}
			}
			
			popUpPanicButton(callingActivity);
		}
		else {
			boolean panicLock = prefs.getBoolean(context.getString(R.string.panicLock), false);
			
			if(panicLock) {
				ToastCountdown.getInstance().cancelCountdown();

				context.getApplicationContext().stopService(new Intent(context, PanicLockService.class));
				
				AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				am.cancel(PendingIntent.getService(context, 0, new Intent(context, PanicLockService.class), 0));
				
				PreferencesUtil.disabledPanicLock(context);
				disableAudibleAlarm(context);
			}
			else {
				boolean externalAlarm = prefs.getBoolean(context.getString(R.string.externalAlarm), false);
				PanicAlarmNotifier panicAlarm = PanicAlarmNotifier.getInstance(context);
				if(panicAlarm.isInPanicMode()) {
					panicAlarm.stopPanicAlarm();
					
					if(externalAlarm) {
						disableExternalAlarm(prefs, context);
					}
				}
				else {
					Integer alarmResource = PreferencesUtil.getCurrentlySelectedQuickPanicAlarm(context);
					
					if(alarmResource != null) {
						panicAlarm.startPanicAlarm(alarmResource);
					}
					
					if(externalAlarm) {
						enableExternalAlarm(prefs, context);
					}
				}
			}
			
			popUpPanicButton(callingActivity);
		}
	}

	private static void disableAudibleAlarm(Context context) {
		PanicAlarmNotifier panicAlarm = PanicAlarmNotifier.getInstance(context);
		if(panicAlarm.isInPanicMode()) {
			panicAlarm.stopPanicAlarm();
		}
	}
	
	private static void enableExternalAlarm(SharedPreferences prefs, Context context) {
		String alarmOnUrl = prefs.getString(context.getString(R.string.alarmOnUrl), "");
		ExternalPanicNotifier.getInstance(context).sendExternalAlarmActivate(alarmOnUrl);
	}
	
	private static void disableExternalAlarm(SharedPreferences prefs, Context context) {
		String alarmOffUrl = prefs.getString(context.getString(R.string.alarmOffUrl), "");
		ExternalPanicNotifier.getInstance(context).sendExternalAlarmDeactivate(alarmOffUrl);
	}
	
	private static void popUpPanicButton(Activity activity) {
		((ImageView) activity.findViewById(R.id.panicButtonView)).setImageResource(R.drawable.panic_default);
	}
	
	private static void pushDownPanicButton(Activity activity) {
		((ImageView) activity.findViewById(R.id.panicButtonView)).setImageResource(R.drawable.panic_depressed);
	}
}
