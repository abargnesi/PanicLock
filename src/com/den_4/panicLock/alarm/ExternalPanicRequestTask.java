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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.den_4.panicLock.R;
import com.den_4.panicLock.model.AlarmType;

/**
 * {@link AsyncTask} used to make an HTTP request to a URL endpoint when an alarm is armed and disarmed.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class ExternalPanicRequestTask extends AsyncTask<String, Integer, Integer> {
	private AlarmType alarmType;
	private Context context;
	
	public ExternalPanicRequestTask(AlarmType alarmType, Context context) {
		super();
		this.alarmType = alarmType;
		this.context = context;
	}

	@Override
	protected Integer doInBackground(String... params) {
		try {
			HttpURLConnection httpConnection = getConnection(params[0]);
			
			return httpConnection.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return HttpURLConnection.HTTP_BAD_REQUEST;
	}
	
	protected boolean isResponseOk(int httpResponseCode) {
		return httpResponseCode == HttpURLConnection.HTTP_OK;
	}
	
	protected HttpURLConnection getConnection(String url) throws IOException {
		URL alarmUrl = new URL(url);
		
		if(alarmUrl.getProtocol().equalsIgnoreCase("https")) {
			return (HttpsURLConnection) alarmUrl.openConnection();
		}
		else {
			return (HttpURLConnection) alarmUrl.openConnection();
		}
	}
	
	protected void onPostExecute(Integer httpResponseCode) {
		int alarmMessageResourceId;
		if(AlarmType.ALARM_ACTIVATED.equals(alarmType)) {
			if(isResponseOk(httpResponseCode)) {
				alarmMessageResourceId = R.string.externalAlarmOnToast;
			}
			else {
				alarmMessageResourceId = R.string.externalAlarmOnFailedToast;
			}
		}
		else {
			if(isResponseOk(httpResponseCode)) {
				alarmMessageResourceId = R.string.externalAlarmOffToast;
			}
			else {
				alarmMessageResourceId = R.string.externalAlarmOffFailedToast;
			}
		}
		
		Toast externalAlarmOnToast = Toast.makeText(context, context.getResources().getString(alarmMessageResourceId), Toast.LENGTH_SHORT);
		externalAlarmOnToast.show();
    }
}
