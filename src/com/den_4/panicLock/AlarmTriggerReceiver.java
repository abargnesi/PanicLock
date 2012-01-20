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
package com.den_4.panicLock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.den_4.panicLock.alarm.PanicAlarmNotifier;
import com.den_4.panicLock.util.PreferencesUtil;

/**
 * {@link BroadcastReceiver} triggered by {@link PanicWidget} when the "Quick Panic!" button is pressed.  The implementation of this receiver
 * acts as a toggle to turn the quick panic alarm on and off.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class AlarmTriggerReceiver extends BroadcastReceiver {

    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        PanicAlarmNotifier panicAlarmNotifier = PanicAlarmNotifier.getInstance(context);

        if(!panicAlarmNotifier.isInPanicMode()) {
            panicAlarmNotifier.startPanicAlarm(PreferencesUtil.getCurrentlySelectedQuickPanicAlarm(context));
        }
        else {
            panicAlarmNotifier.stopPanicAlarm();
        }
    }
}
