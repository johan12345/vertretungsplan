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

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.johan.vertretungsplan.parser.BackendConnectParser;

@ReportsCrashes(formKey = "", mailTo = "johan.forstner+app@gmail.com", 
mode = org.acra.ReportingInteractionMode.DIALOG,
resDialogIcon = R.drawable.ic_launcher,
resDialogTitle = R.string.crash_dialog_title,
resDialogText = R.string.crash_dialog_text,
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt)
public class VertretungsplanApplication extends Application {

	private BackendConnectParser parser;
	private Tracker mTracker;
	private SharedPreferences settings;
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		context = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public BackendConnectParser getParser() {
		if(parser != null)
			return parser;
		else {
			notifySchoolChanged();
			if(parser == null) {					
				startSelectSchoolActivity();
				parser = null;
				return parser;
			} else {
				return parser;
			}
		}
	}

	public void notifySchoolChanged() {
		String schoolId = settings.getString("selected_school", null);
		if(schoolId != null) {
			parser = new BackendConnectParser(schoolId);
		}
	}
	
	private void startSelectSchoolActivity() {
		Intent intent = new Intent(this, SelectSchoolActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	synchronized Tracker getTracker() {
	    if (mTracker == null) {

	      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	      mTracker = analytics.newTracker(R.xml.analytics);

	    }
	    return mTracker;
	}
	public static int getVersion() {
		try {
			return context.getPackageManager()
				    .getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
