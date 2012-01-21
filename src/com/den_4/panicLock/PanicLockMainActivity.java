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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.den_4.panicLock.alarm.AlarmStrategyFactory;
import com.den_4.panicLock.alarm.service.PanicLockService;
import com.den_4.panicLock.model.PanicThresholds;
import com.den_4.panicLock.util.PreferencesUtil;

/**
 * Main application activity, showing the PANIC button, that allows the user to trigger the three types of alarms based on duration thresholds defined in {@link PanicThresholds}.
 *
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class PanicLockMainActivity extends Activity implements OnTouchListener {
	private final static int MENU_PREFERENCES = 1;
	private final static int MENU_EXIT = 2;
	private Chronometer chronometer;

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean panicLock = prefs.getBoolean(getString(R.string.panicLock), false);

		if(panicLock) {
			((ImageView) findViewById(R.id.panicButtonView)).setImageResource(R.drawable.panic_depressed);
		}

        ((ImageView) findViewById(R.id.panicButtonView)).setOnTouchListener(this);
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem preferencesMenuItem = menu.add(0, MENU_PREFERENCES, 0, R.string.menu_preferences);
		preferencesMenuItem.setIcon(R.drawable.preferences_icon);

		MenuItem exitMenuItem = menu.add(0, MENU_EXIT, 1, R.string.menu_exit);
		exitMenuItem.setIcon(R.drawable.exit_icon);

		return true;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PREFERENCES:
			startActivity(new Intent(this, EditPreferenceActivity.class));
			return true;
		case MENU_EXIT:
			finish();
			return true;
		}

		return (super.onOptionsItemSelected(item));
	}

	/**
     * {@inheritDoc}
     */
	@Override
    public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			((ImageView) v).setImageResource(R.drawable.panic_depressed);
			chronometer = new Chronometer(this);
			chronometer.start();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP) {
			chronometer.stop();
			AlarmStrategyFactory.processAlarmAction(SystemClock.elapsedRealtime() - chronometer.getBase(), this);
		}

		return true;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        cleanupMainActivity();
    }

    /**
     * Clean up other objects originally spawned from this activity.
     */
    protected void cleanupMainActivity() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), PanicLockService.class));

        //Disable panic lock preference and reset panic button resource.
        PreferencesUtil.disabledPanicLock(getApplicationContext());
        ((ImageView) findViewById(R.id.panicButtonView)).setImageResource(R.drawable.panic_default);
    }
}
