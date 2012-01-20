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
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Singleton to enable / disable audible alarms at maximum volume.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class PanicAlarmNotifier {
	private static PanicAlarmNotifier instance;
	private static Context context;
	private MediaPlayer panicAlarmMp;
	private int originalMusicVolume;
	private boolean inPanicMode;
	
	private PanicAlarmNotifier() {
	}
	
	public synchronized static PanicAlarmNotifier getInstance(Context context) {
		if(instance == null) {
			instance = new PanicAlarmNotifier();
		}
		
		PanicAlarmNotifier.context = context;
		return instance;
	}
	
	public void startPanicAlarm(int alarmResource) {
		panicAlarmMp = MediaPlayer.create(context, alarmResource);
		panicAlarmMp.setLooping(true);
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		originalMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
		panicAlarmMp.start();
		inPanicMode = true;
	}
	
	public void stopPanicAlarm() {
		panicAlarmMp.stop();
		panicAlarmMp.release();
		panicAlarmMp = null;
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, false);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMusicVolume, 0);
		inPanicMode = false;
	}

	public boolean isInPanicMode() {
		return inPanicMode;
	}
}
