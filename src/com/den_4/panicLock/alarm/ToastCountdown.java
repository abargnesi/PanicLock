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
 * Threaded task that creates a {@link Toast} that counts down each second starting
 * from a max and ending at zero.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class ToastCountdown {
	private static ToastCountdown instance;
	private ToastCountdownTask currentCountdownTask;
	
	public synchronized static ToastCountdown getInstance() {
		synchronized (ToastCountdown.class) {
			if(instance == null) {
				instance = new ToastCountdown();
			}
		}
		
		return instance;
	}
	
	private ToastCountdown() {
		super();
	}
	
	/**
	 * Checks <code>countdownFrom</code> argument for validity and then begins executing
	 * {@link AsyncTask} which starts the toast countdown.
	 * 
	 * @param coundownFrom
	 * @param context
	 */
	public synchronized void runCountdown(int countdownFrom, Context context) {
		if(countdownFrom <= 0) {
			throw new IllegalArgumentException("The countdownFrom argument must be greater than zero.");
		}
		
		currentCountdownTask = new ToastCountdownTask(context);
		currentCountdownTask.execute(countdownFrom);
	}
	
	public synchronized void cancelCountdown() {
		if(currentCountdownTask != null) {
			currentCountdownTask.cancel(true);
		}
	}
}
