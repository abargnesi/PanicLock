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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * 
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public class PanicWidgetAlarmPreference extends PreferenceActivity {
    private ListPreference qpAlarmPref;
    
    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.widget_prefs);
        
        OnPreferenceChangeListener preferenceChangeListener = new SummaryUpdater();
        qpAlarmPref = (ListPreference) findPreference(getString(R.string.quickPanicAlarm));
        qpAlarmPref.setOnPreferenceChangeListener(preferenceChangeListener);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE); 

        String quickPanicAlarm = prefs.getString(getString(R.string.quickPanicAlarm), getString(R.string.quickPanicAlarmDefaultValue));
        qpAlarmPref.setValue(quickPanicAlarm);
        qpAlarmPref.setSummary(quickPanicAlarm);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
    }
    
    private void savePreferences() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(getString(R.string.quickPanicAlarm), qpAlarmPref.getValue());
        editor.commit();
    }
    
    /**
     * Used to update the preference summary text with the latest value.
     * 
     * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
     */
    private class SummaryUpdater implements OnPreferenceChangeListener {

        /* (non-Javadoc)
         * @see android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference, java.lang.Object)
         */
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            preference.setSummary(newValue.toString());
            
            return true;
        }
    }
}
