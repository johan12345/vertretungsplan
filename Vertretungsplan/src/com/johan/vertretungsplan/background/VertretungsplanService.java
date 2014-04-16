/*  LS Vertretungsplan - Android-App f�r den Vertretungsplan der Lornsenschule Schleswig
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

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.google.gson.Gson;
import com.johan.vertretungsplan.classes.Schule;
import com.johan.vertretungsplan.classes.Vertretungsplan;
import com.johan.vertretungsplan.classes.VertretungsplanTag;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.utils.Utils;
import com.johan.vertretungsplan_2.R;
import com.johan.vertretungsplan_2.StartActivity;
import com.johan.vertretungsplan_2.VertretungsplanApplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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

public class VertretungsplanService extends IntentService {
	public static String UPDATE_ACTION = "UPDATE";
	static SharedPreferences settings;
	static Bundle extras;
	static Context context;

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
			
				//Vertretungsplan-Objekt erzeugen
				Vertretungsplan v = parser.getVertretungsplan();
				settings.edit().putString("Vertretungsplan", gson.toJson(v)).commit();
	
				// Sucessful finished
				int result = Activity.RESULT_OK;	
	
				if(extras != null) {
					if(extras.get("MESSENGER") != null) {
						nachrichtAnApp(extras, result, v);
					}
				}
				
				if (settings.getBoolean("notification", true) && !settings.getBoolean("isInForeground", false)) {
					//Benachrichtigung anzeigen
					String klasse = settings.getString("klasse", null);
					String vAltJson = settings.getString("Vertretungsplan", null);
					if (klasse != null && vAltJson != null) {
						Vertretungsplan vAlt = gson.fromJson(vAltJson, Vertretungsplan.class);
						if(somethingChanged(vAlt, v, klasse)) {
							
						}
					}
				}
				
				
	
	//			String standWinter = null;
	//			Vertretungsplan vAlt = null;
	//
	//			if (settings.getBoolean("notification", true) && !settings.getBoolean("isInForeground", false)) {
	//				//Benachrichtigung anzeigen
	//
	//				if (settings.getString("Vertretungsplan", null) != null) {
	//					vAlt = gson.fromJson(settings.getString("Vertretungsplan", ""), Vertretungsplan.class);		        						        
	//					try {
	//						//Alten Winterausfall-Stand lesen
	//						standWinter = vAlt.getWinterAusfall().getStand();
	//					} catch (NullPointerException e) {}
	//				}
	//				//Wenn sich etwas ge�ndert hat, Benachrichtigungen geben
	//				String standWinterNeu = docWinter.select("pubDate").first().text();
	//
	//				if (standWinter != null && standWinterNeu != null && !(standWinterNeu.equals(standWinter))) {
	//					//Es gibt �nderungen beim Winter-Schulausfall
	//					String text = docWinter.select("item description").first().text(); 
	//					if (!text.contains("Aktuell gibt es keine Hinweise auf witterungsbedingten Unterrichtsausfall.")) {
	//						benachrichtigung(-1); //-1 steht f�r Winter-Schulausfall
	//					}
	//
	//				} else {
	//					String klasse = settings.getString("klasse", null);
	//					if(klasse != null) {
	//						//Pr�fen, ob �nderungen schon gestern bekannt waren und es keine �nderungen f�r Morgen gibt
	//						boolean schonGesternBekannt = false;
	//						boolean garNichtsVeraendert = false;
	//						if (vAlt != null && v !=  null) {
	//
	//							if (vAlt.get(klasse) != null) {
	//								if (v.get(klasse) != null) {
	//									//Bei alt und neu sind �nderungen
	//									schonGesternBekannt = v.get(klasse).getVertretungHeute().equals(vAlt.get(klasse).getVertretungMorgen()) && v.get(klasse).getVertretungMorgen().size() == 0;
	//									garNichtsVeraendert = v.get(klasse).equals(vAlt.get(klasse));
	//								} else {
	//									//�nderungen nur bei alt
	//								}
	//							} else {
	//								if (v.get(klasse) != null) {
	//									//�nderungen nur bei neu							    					
	//								} else {
	//									//Keine �nderungen bei beiden
	//									garNichtsVeraendert = true; //Keine �nderungen vorher und nachher
	//								}
	//							}
	//
	//						}
	//						Log.d("Vertretungsplan", "garnichts: " + garNichtsVeraendert);
	//						Log.d("Vertretungsplan", "schonGesternBekannt: " + schonGesternBekannt);
	//						if (!garNichtsVeraendert && !schonGesternBekannt) {
	//							int anzahlAenderungen = 0;
	//							if(v.get(klasse) != null) {
	//								if (v.get(klasse).getVertretungHeute() != null)
	//									anzahlAenderungen += v.get(klasse).getVertretungHeute().size();
	//								if (v.get(klasse).getVertretungMorgen() != null)
	//									anzahlAenderungen += v.get(klasse).getVertretungMorgen().size();
	//							}
	//							if (anzahlAenderungen != 0) {
	//								benachrichtigung(anzahlAenderungen);
	//							}
	//						}
	//					}
	//				}		
	//			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean somethingChanged(Vertretungsplan vAlt, Vertretungsplan v,
			String klasse) {
		String standAlt = vAlt.getTage().get(0).getStand();
		String standNeu = v.getTage().get(0).getStand();
		if(!standAlt.equals(standNeu)) {
			for(VertretungsplanTag tag:v.getTage()) {
				//passenden alten Tag finden
				VertretungsplanTag matchingTag = null;
				for(VertretungsplanTag tagAlt:vAlt.getTage()) {
					if(tagAlt.getDatum().equals(tag.getDatum())) {
						matchingTag = tagAlt;
						break;
					}
				}
				
				if(matchingTag == null) {
					//dieser Tag wurde neu hinzugefügt
					if(tag.getKlassen().get(klasse) != null
							&& tag.getKlassen().get(klasse).getVertretung().size() > 0)
						return true;
				} else {
					//TODO:
				}
			}
		}
		return false;
	}

	private void nachrichtAnApp(Bundle extras, int result, Vertretungsplan v) {
		//Nachricht senden an App
		Log.d("Vertretungsplan", "heruntergeladen, sende Nachricht an App");
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
			bundle.putSerializable("Vertretungsplan", v);		  	      
			msg.setData(bundle);
			try {
				messenger.send(msg);
			} catch (Throwable e1) {
				Log.w(getClass().getName(), "Exception sending message", e1);
			}

		}
	}

	private static void benachrichtigung(Integer anzahlAenderungen) {
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
				if (anzahlAenderungen == null) {
				} else {
					mBuilder.setNumber(anzahlAenderungen.intValue());
				}
				if (anzahlAenderungen == -1) {
					//Winter-Schulausfall
					mBuilder.setContentText("Es gibt �nderungen beim Winter-Schulausfall!");
				} else {
					mBuilder.setContentText("Es gibt �nderungen auf dem Vertretungsplan!");
				}
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
