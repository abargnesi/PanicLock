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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * {@link AppWidgetProvider} used to set up the "Quick Panic!" widget view.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class PanicWidget extends AppWidgetProvider {

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i=0; i < appWidgetIds.length; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            
            //Wire the "Panic" button to toggle the Quick Panic alarm.
            views.setOnClickPendingIntent(R.id.panicButton, PendingIntent.getBroadcast(context, 0, new Intent("com.den_4.panicLock.ALARM_TRIGGER"), PendingIntent.FLAG_UPDATE_CURRENT));
            
            //Wire the configure button to launch widget preferences.
            views.setOnClickPendingIntent(R.id.configureButton, PendingIntent.getActivity(context, 0, new Intent(context, PanicWidgetAlarmPreference.class), PendingIntent.FLAG_UPDATE_CURRENT));
            
            //Wire the launcher button to launch the main "PanicLock" application activity.
            views.setOnClickPendingIntent(R.id.mainAppLauncherButton, PendingIntent.getActivity(context, 0, new Intent(context, PanicLockMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
	}
}
