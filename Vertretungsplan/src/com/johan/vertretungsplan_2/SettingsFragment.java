/*  Vertretungsplan - Android-App für Vertretungspläne von Schulen
    Copyright (C) 2014  Johan v. Forstner

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see [http://www.gnu.org/licenses/]. */

package com.johan.vertretungsplan_2;

import java.io.IOException;
import java.util.List;

import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.Preference.OnPreferenceClickListener;
import org.holoeverywhere.preference.PreferenceCategory;
import org.holoeverywhere.preference.PreferenceFragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.PreferenceScreen;
import org.holoeverywhere.preference.SharedPreferences;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.utils.Utils;
import com.johan.vertretungsplan_2.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference changeSchoolPref = findPreference(R.id.change_school);
        changeSchoolPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity(), SelectSchoolActivity.class);
				getActivity().startActivity(intent);
				return true;
			}
        	
        });
        
        CheckBoxPreference analyticsPref = (CheckBoxPreference) findPreference(R.id.analytics);
        analyticsPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Log.d("Vertretungsplan", "Analytics Opt-out");
	            GoogleAnalytics.getInstance(getActivity().getApplicationContext()).setAppOptOut(!(boolean) newValue);
				return true;
			}
        	
        });
        
        try {
			Schule schule = Utils.getSelectedSchool(getActivity());
			PreferenceCategory syncCategory = (PreferenceCategory) findPreference(R.id.sync_category);
			Preference syncPref = findPreference(R.id.sync);
			Preference syncPeriodPref = findPreference(R.id.syncPeriod);
			Preference notificationPref = findPreference(R.id.notification);
			Preference ringtonePref = findPreference(R.id.ringtone);
			
			if(schule.usesPush()) {
				syncCategory.removePreference(syncPref);
				syncCategory.removePreference(syncPeriodPref);
				notificationPref.setDependency(null);
				ringtonePref.setDependency(null);
			}
			
			PreferenceScreen screen = getPreferenceScreen();
			List<Schule> schulen = Utils.getSchools(getActivity());
			if(schulen.size() <= 1) {
				screen.removePreference(changeSchoolPref);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    }
}