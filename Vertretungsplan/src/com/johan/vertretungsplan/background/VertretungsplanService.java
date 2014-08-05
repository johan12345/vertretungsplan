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
import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.AdditionalInfo;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.parser.BaseParser.VersionException;
import com.johan.vertretungsplan.widget.VertretungsplanWidgetProvider;
import com.johan.vertretungsplan_2.R;
import com.johan.vertretungsplan_2.StartActivity;
import com.johan.vertretungsplan_2.VertretungsplanApplication;

public class VertretungsplanService extends IntentService {
	public static String UPDATE_ACTION = "UPDATE";
	static SharedPreferences settings;
	static Bundle extras;
	static Context context;
	
	public static int RESULT_OK = -1;
	public static int RESULT_ERROR = 0;
	public static int RESULT_VERSION_ERROR = 1;
	
	public static String KEY_NOTIFICATION = "notification";

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
				
				//Vertretungsplan-Objekt erzeugen
				Vertretungsplan v = parser.getVertretungsplan();
	
				// Sucessful finished
				int result = RESULT_OK;	
	
				if(extras != null && extras.get("MESSENGER") != null) {
					nachrichtAnApp(extras, result, v);
				}
				
				if ((extras == null || extras.getBoolean(KEY_NOTIFICATION, true))
						&& settings.getBoolean("notification", true)
						&& !settings.getBoolean("isInForeground", false)) {
					//Benachrichtigung anzeigen
					String klasse = settings.getString("klasse", null);
					String vAltJson = settings.getString("Vertretungsplan", null);
					if (klasse != null && vAltJson != null) {
						Vertretungsplan vAlt = gson.fromJson(vAltJson, Vertretungsplan.class);
						if(somethingChanged(vAlt, v, klasse)) {
							benachrichtigung();
						}
					}
				}
				
				settings.edit().putString("Vertretungsplan", gson.toJson(v)).commit();
				
				AppWidgetManager mgr=AppWidgetManager.getInstance(this);
				int[] ids = mgr.getAppWidgetIds(new ComponentName(this, VertretungsplanWidgetProvider.class));
				new VertretungsplanWidgetProvider().onUpdate(this, mgr, ids);
			} catch (IOException | JSONException e) {
				e.printStackTrace();
				int result = RESULT_ERROR;
				if(extras != null && extras.get("MESSENGER") != null) {
					nachrichtAnApp(extras, result, null);
				}
			} catch (VersionException e) {
				int result = RESULT_VERSION_ERROR;
				if(extras != null && extras.get("MESSENGER") != null) {
					nachrichtAnApp(extras, result, null);
				}
			}
		}
	}

	private boolean somethingChanged(Vertretungsplan vAlt, Vertretungsplan v,
			String klasse) {
		
		for(AdditionalInfo info:v.getAdditionalInfos()) {
			if(info.hasInformation()) {
				//passende alte Info finden
				AdditionalInfo oldInfo = null;
				for(AdditionalInfo infoAlt:vAlt.getAdditionalInfos()) {
					if(infoAlt.getText().equals(info.getText())) {
						oldInfo = infoAlt;
						break;
					}
				}
				if(oldInfo == null) {
					//es wurde keine passende alte Info gefunden
					return true;
				}
			}
		}
		
		for(VertretungsplanTag tag:v.getTage()) {
			//passenden alten Tag finden
			VertretungsplanTag oldTag = null;
			for(VertretungsplanTag tagAlt:vAlt.getTage()) {
				if(tagAlt.getDatum().equals(tag.getDatum())) {
					oldTag = tagAlt;
					break;
				}
			}
			
			if(tag.getKlassen().get(klasse) != null
					&& tag.getKlassen().get(klasse).getVertretung().size() > 0) {
				//Auf dem neuen Plan gibt es Vertretungen, die die gewählte Klasse betreffen
				if(oldTag == null) {
					//dieser Tag wurde neu hinzugefügt -> Vertretungen waren vorher nicht bekannt
					return true;
				} else {
					//dieser Tag war vorher schon auf dem Vertretungsplan
					//Stand prüfen					
					if(!oldTag.getStand().equals(tag.getStand())) {	
						//Stand hat sich verändert
						if(oldTag.getKlassen().get(klasse) != null
								&& oldTag.getKlassen().get(klasse).getVertretung().size() > 0) {
							//auch vorher waren schon Vertretungen für die Klasse bekannt
							//-> vergleiche alte mit neuen Vertretungen
							if(!oldTag.getKlassen().get(klasse).getVertretung().equals(
									tag.getKlassen().get(klasse).getVertretung())) {
								//Die Vertretungen sind nicht gleich
								return true;
							} else {
								//keine Veränderung
							}
						} else {
							//vorher waren keine Vertretungen für die gewählte Klasse bekannt -> es wurde etwas verändert
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void nachrichtAnApp(Bundle extras, int result, Vertretungsplan v) {
		//Nachricht senden an App
		if (extras != null) {
			Messenger messenger = (Messenger) extras.get("MESSENGER");
			Message msg = Message.obtain();

			try {
				RemoteViews rv = (RemoteViews) extras.get("RV");
				int widgetId = ((Integer) extras.get("WID")).intValue();
				List<Object> list = new ArrayList<Object>();
				list.add(rv);
				list.add(widgetId);
				msg.obj = list;
			} catch(Throwable e) {

			}

			msg.arg1 = result;
			Bundle bundle = new Bundle();
			if(v != null)
				bundle.putSerializable("Vertretungsplan", v);		  	      
			msg.setData(bundle);
			try {
				messenger.send(msg);
			} catch (Throwable e1) {
				Log.w(getClass().getName(), "Exception sending message", e1);
			}

		}
	}

	private void benachrichtigung() {
		if (settings.getBoolean("notification", true) == true) {
			//Benachrichtigung anzeigen
			if(settings.getBoolean("isInForeground", false)){
				//App wird angezeigt
			} else {
				//App wird nicht angezeigt
				String sound = settings.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle("Vertretungsplan");
				mBuilder.setContentText(getResources().getString(R.string.notification_text));
				if (!sound.equals("")) {
					Uri soundUri = Uri.parse(sound);
					mBuilder.setSound(soundUri);
				}
				mBuilder.setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS);
				mBuilder.setOnlyAlertOnce(true);
				mBuilder.setAutoCancel(true);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(context, StartActivity.class);

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(StartActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
						stackBuilder.getPendingIntent(
								0,
								PendingIntent.FLAG_UPDATE_CURRENT
								);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				int mId = 1;
				mNotificationManager.notify(mId, mBuilder.build());
			}
		}
	}

}
