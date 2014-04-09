/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
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

package com.johan.vertretungsplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.johan.vertretungsplan.background.AutostartService;

import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
	
	 @Override
	    public void onStart() {
	      super.onStart();	      
	      EasyTracker.getInstance().activityStart(this);
	    }
	    
	    @Override
	    public void onStop() {
	      super.onStop();
	      EasyTracker.getInstance().activityStop(this);
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
        super.onResume();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean("isInForeground",true);
        prefEditor.commit();      
   }
    @Override
    public void onPause(){
    		super.onPause();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor prefEditor = prefs.edit();
            prefEditor.putBoolean("isInForeground", false);
            prefEditor.commit();
            setAlarms();
   }
    
    public void setAlarms(){
    	Intent autostartIntent = new Intent(getApplicationContext(), AutostartService.class);
    	getApplicationContext().startService(autostartIntent);
    }

}
