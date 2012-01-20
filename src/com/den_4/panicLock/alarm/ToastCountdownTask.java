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
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Provides a countdown that provides one-second updates via {@link Toast}s that ends when the countdown hits 0.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class ToastCountdownTask extends AsyncTask<Integer, Integer, Integer> {
	private Context context;
	private Toast countdownToast;
	
	public ToastCountdownTask(Context context) {
		this.context = context;
	}
	
	/**
	 * Create toast for each second in the countdown, except 0.
	 */
	@Override
	protected Integer doInBackground(Integer... params) {
		int countdownStart = params[0];
		
		while(countdownStart > 0) {
			publishProgress(countdownStart);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
				//thread interrupted, countdown cancelled.
				countdownStart = 0;
			}
			
			countdownStart--;
		}
		
		return 0;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(countdownToast != null) {
			countdownToast.setText(String.valueOf(values[0]));
		}
		else {
			countdownToast = Toast.makeText(context, String.valueOf(values[0]), Toast.LENGTH_LONG);
		}
		
		countdownToast.show();
	}

	@Override
	protected void onPostExecute(Integer result) {
		cancelCurrentCountdown();
	}

	/**
	 * Kill the countdown toast if this task has been cancelled.
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
		
		cancelCurrentCountdown();
	}
	
	/**
	 * Cancels the toast countdown if it exists.
	 */
	private void cancelCurrentCountdown() {
		if(countdownToast != null) {
			countdownToast.cancel();
		}
	}
}
