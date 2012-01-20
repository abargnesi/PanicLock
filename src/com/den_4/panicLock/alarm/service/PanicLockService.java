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
package com.den_4.panicLock.alarm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import com.den_4.panicLock.R;
import com.den_4.panicLock.alarm.PanicAlarmNotifier;
import com.den_4.panicLock.util.PreferencesUtil;

/**
 * {@link Service} that checks for physical phone disturbance using the accelerometer.  If the phone becomes disturbed
 * then the panic lock alarm is sounded using {@link PanicAlarmNotifier}.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class PanicLockService extends Service {
    private boolean started = false;
    private boolean screenShutoffSingleEvent = false;
    private DisturbanceSensorThread sensorThread;
    
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        Toast.makeText(this, R.string.panicLockStarted, Toast.LENGTH_SHORT).show();
        sensorThread = new DisturbanceSensorThread(getApplicationContext());
        sensorThread.start();
    }

    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        sensorThread.setCancelled(true);
        Toast.makeText(this, R.string.panicLockEnded, Toast.LENGTH_SHORT).show();
        unregisterReceiver(screenon);
        unregisterReceiver(screenoff);
    }

    /* (non-Javadoc)
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);

            if (!started) {
                IntentFilter onfilter = new IntentFilter (Intent.ACTION_SCREEN_ON);
                IntentFilter offfilter = new IntentFilter (Intent.ACTION_SCREEN_OFF);
                registerReceiver(screenon, onfilter);
                registerReceiver(screenoff, offfilter);
                started = true;
            }
    }

    BroadcastReceiver screenon = new BroadcastReceiver() {
        /* (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            screenShutoffSingleEvent = false;
        }
    };

    BroadcastReceiver screenoff = new BroadcastReceiver() {
        /* (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            screenShutoffSingleEvent = true;
            
            //The following is a work-around for Issues 3708: OnSensorChanged() is no longer called in standby mode since last Firmware upgrade.
            //URL: http://code.google.com/p/android/issues/detail?id=3708
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(sensorThread);
            sensorManager.registerListener(sensorThread, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        }
    };
    
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    private class DisturbanceSensorThread extends Thread implements SensorEventListener {
        private static final int TIME_TO_WAIT_BEFORE_FIRST_READ = 3000;
        private int timeWaited = 0;
    	private Context context;
    	private Map<Sensor, List<Float>> initialSensorValues;
    	private boolean phoneUndisturbed;
        private boolean cancelled = false;
    	
    	public DisturbanceSensorThread(Context context) {
    		this.context = context;
    		this.initialSensorValues = new HashMap<Sensor, List<Float>>();

    		attachToAccelerometer(context);
    		this.phoneUndisturbed = true;
    	}
    	
    	public synchronized void setCancelled(boolean cancelled) {
    	    this.cancelled = cancelled;
    	}

		protected void attachToAccelerometer(Context context) {
			SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		protected void detachFromAccelerometer() {
			SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    		sensorManager.unregisterListener(this);
		}
    	
    	/* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	public void run() {
    		while(phoneUndisturbed && !cancelled) {
    			try {
					Thread.sleep(100);
					
					if(initialSensorValues.isEmpty()) {
					    timeWaited += 100;
					}
				} catch (InterruptedException e) {
				}
    		}

    		if(!cancelled) {
    		    Integer alarmResource = PreferencesUtil.getCurrentlySelectedPanicLockDisturbanceAlarm(context);
                
                if(alarmResource != null) {
                    PanicAlarmNotifier.getInstance(context).startPanicAlarm(alarmResource);
                    
                    Vibrator theVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    theVibrator.vibrate(2000);
                }
    		}
    		
    		detachFromAccelerometer();
    		stopSelf();
    	}

		/* (non-Javadoc)
		 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
		 */
		public void onAccuracyChanged(Sensor sensor, int value) {
		}

		/* (non-Javadoc)
		 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
		 */
		public void onSensorChanged(SensorEvent event) {
		    if(timeWaited >= TIME_TO_WAIT_BEFORE_FIRST_READ) {
		        List<Float> startingSensorValues = new ArrayList<Float>();
                for(int i = 0; i < event.values.length; i++) {
                    startingSensorValues.add(event.values[i]);
                }
                initialSensorValues.put(event.sensor, startingSensorValues);
                timeWaited = 0;
		    }
		    else if(initialSensorValues.containsKey(event.sensor)) {
		        List<Float> startingSensorValues = initialSensorValues.get(event.sensor);
                
                //iterate over DATA_X, DATA_Y, and DATA_Z and determine if there is a major movement.
                for(int i = 0; i < 3; i++) {
                    float currentVal = ((float) startingSensorValues.get(i));
                    
                    if(screenShutoffSingleEvent) {
                        screenShutoffSingleEvent = false;
                        return;
                    }
                    
                    if(Math.abs(event.values[i] - currentVal) > 3.0) {
                        phoneUndisturbed = false;
                    }
                }
		    }
		}
    }
}
