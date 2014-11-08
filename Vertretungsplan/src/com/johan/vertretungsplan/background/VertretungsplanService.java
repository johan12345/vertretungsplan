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

package com.johan.vertretungsplan.background;

import java.io.IOException;

import org.json.JSONException;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.parser.BaseParser.UnauthorizedException;
import com.johan.vertretungsplan.parser.BaseParser.VersionException;
import com.johan.vertretungsplan.widget.VertretungsplanWidgetProvider;
import com.johan.vertretungsplan_2.VertretungsplanApplication;

public class VertretungsplanService extends IntentService {
	public static String UPDATE_ACTION = "UPDATE";
	static SharedPreferences settings;
	static Bundle extras;
	static Context context;
	
	public static final int RESULT_OK = -1;
	public static final int RESULT_ERROR = 0;
	public static final int RESULT_VERSION_ERROR = 1;
	public static final int RESULT_UNAUTHORIZED_ERROR = 2;
	
	public static final String KEY_NOTIFICATION = "notification";

	public VertretungsplanService() {
		super("VertretungsplanService");
	}

	// Will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent) {
		context = this;
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		extras = intent.getExtras();
		Gson gson = new Gson();

		boolean autoSync;
		try {
			autoSync = extras.getBoolean("AutoSync");	
		} catch (NullPointerException e) {
			autoSync = false;
		}

		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		//wifi
		State wifi = conMan.getNetworkInfo(1).getState();

		if (wifi == NetworkInfo.State.CONNECTED || autoSync == false || settings.getBoolean("syncWifi", false) == false) {

			Log.d("Vertretungsplan", "WiFi state: " + wifi);
			Log.d("Vertretungsplan", "autoSync: " + autoSync);
			Log.d("Vertretungsplan", "syncWifi: " + Boolean.valueOf(settings.getBoolean("syncWifi", false)));

			Log.d("Vertretungsplan", "Vertretungsplan wird abgerufen");
			
			try {
				BaseParser parser = ((VertretungsplanApplication) getApplication()).getParser();
				
				if (parser == null) return;

				Vertretungsplan v = parser.getVertretungsplan();
				settings.edit().putString("Vertretungsplan", gson.toJson(v)).commit();
				
				AppWidgetManager mgr=AppWidgetManager.getInstance(this);
				int[] ids = mgr.getAppWidgetIds(new ComponentName(this, VertretungsplanWidgetProvider.class));
				new VertretungsplanWidgetProvider().onUpdate(this, mgr, ids);
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			} catch (VersionException e) {
			} catch (UnauthorizedException e) {
			}
		}
	}

}
