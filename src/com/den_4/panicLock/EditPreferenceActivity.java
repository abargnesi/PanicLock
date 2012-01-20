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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * Activity for editing preferences of the application.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class EditPreferenceActivity extends PreferenceActivity {
	private ListPreference qpAlarmPref;
	private ListPreference dmsAlarmPref;
	private ListPreference pldAlarmPref;
	private CheckBoxPreference eaPref;
	private EditTextPreference aonPref;
	private EditTextPreference aoffPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		OnPreferenceChangeListener preferenceChangeListener = new SummaryUpdater();
		qpAlarmPref = (ListPreference) findPreference(getString(R.string.quickPanicAlarm));
		qpAlarmPref.setOnPreferenceChangeListener(preferenceChangeListener);
		
		dmsAlarmPref = (ListPreference) findPreference(getString(R.string.deadMansSwitchAlarm));
		dmsAlarmPref.setOnPreferenceChangeListener(preferenceChangeListener);
		
		pldAlarmPref = (ListPreference) findPreference(getString(R.string.panicLockDisturbanceAlarm));
		pldAlarmPref.setOnPreferenceChangeListener(preferenceChangeListener);
		
		eaPref = (CheckBoxPreference) findPreference(getString(R.string.externalAlarm));
		aonPref = (EditTextPreference) findPreference(getString(R.string.alarmOnUrl));
		aoffPref = (EditTextPreference) findPreference(getString(R.string.alarmOffUrl));
	}
	
    /**
     * Upon being resumed we can retrieve the current state.  This allows us
     * to update the state if it was changed at any time while paused.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE); 

        String quickPanicAlarm = prefs.getString(getString(R.string.quickPanicAlarm), getString(R.string.quickPanicAlarmDefaultValue));
        qpAlarmPref.setValue(quickPanicAlarm);
        qpAlarmPref.setSummary(quickPanicAlarm);
        
        String deadMansSwitchAlarm = prefs.getString(getString(R.string.deadMansSwitchAlarm), getString(R.string.deadMansSwitchAlarmDefaultValue));
        dmsAlarmPref.setValue(deadMansSwitchAlarm);
        dmsAlarmPref.setSummary(deadMansSwitchAlarm);
        
        String panicLockDisturbanceAlarm = prefs.getString(getString(R.string.panicLockDisturbanceAlarm), getString(R.string.panicLockDisturbanceAlarmDefaultValue));
        pldAlarmPref.setValue(panicLockDisturbanceAlarm);
        pldAlarmPref.setSummary(panicLockDisturbanceAlarm);
        
        boolean externalAlarm = prefs.getBoolean(getString(R.string.externalAlarm), false);
        eaPref.setChecked(externalAlarm);
        
        String alarmOnUrl = prefs.getString(getString(R.string.alarmOnUrl), "http://");
        aonPref.setText(alarmOnUrl);
        
        String alarmOffUrl = prefs.getString(getString(R.string.alarmOffUrl), "http://");
        aoffPref.setText(alarmOffUrl);
    }

    /**
     * Any time we are paused we need to save away the current state, so it
     * will be restored correctly when we are resumed.
     */
    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

	@Override
	protected void onStop() {
		super.onStop();
		savePreferences();
	}
	
	private void savePreferences() {
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		
		editor.putString(getString(R.string.quickPanicAlarm), qpAlarmPref.getValue());
		editor.putString(getString(R.string.deadMansSwitchAlarm), dmsAlarmPref.getValue());
		editor.putString(getString(R.string.panicLockDisturbanceAlarm), pldAlarmPref.getValue());
		
        editor.putBoolean(getString(R.string.externalAlarm), eaPref.isChecked());
        editor.putString(getString(R.string.alarmOnUrl), aonPref.getText());
        editor.putString(getString(R.string.alarmOffUrl), aoffPref.getText());
        
        editor.commit();
	}
	
	/**
	 * Used to update the preference summary text with the latest value.
	 * 
	 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
	 */
	private class SummaryUpdater implements OnPreferenceChangeListener {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			preference.setSummary(newValue.toString());
			
			return true;
		}
	}
}
