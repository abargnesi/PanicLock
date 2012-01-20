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

import android.content.Context;

import com.den_4.panicLock.model.AlarmType;

/**
 * Singleton used to send an HTTP request to some configured URL when an alarm is armed / disarmed. 
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class ExternalPanicNotifier {
	private static ExternalPanicNotifier instance;
	private static Context context;
	
	private ExternalPanicNotifier() {
		
	}
	
	public static ExternalPanicNotifier getInstance(Context context) {
		if(instance == null) {
			instance = new ExternalPanicNotifier();
		}
		
		ExternalPanicNotifier.context = context;
		return instance;
	}
	
	public void sendExternalAlarmActivate(String activateUrl) {
		ExternalPanicRequestTask panicRequest = new ExternalPanicRequestTask(AlarmType.ALARM_ACTIVATED, context);
		panicRequest.execute(activateUrl);
	}
	
	public void sendExternalAlarmDeactivate(String deactivateUrl) {
		ExternalPanicRequestTask panicRequest = new ExternalPanicRequestTask(AlarmType.ALARM_DEACTIVATED, context);
		panicRequest.execute(deactivateUrl);
	}
}
