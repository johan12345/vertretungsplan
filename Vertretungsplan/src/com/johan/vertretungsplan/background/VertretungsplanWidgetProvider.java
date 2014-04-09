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

package com.johan.vertretungsplan.background;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.preference.PreferenceManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;
import com.johan.vertretungsplan.R;
import com.johan.vertretungsplan.StartActivity;
import com.johan.vertretungsplan.R.id;
import com.johan.vertretungsplan.R.layout;
import com.johan.vertretungsplan.classes.Vertretung;
import com.johan.vertretungsplan.classes.Vertretungsplan;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

public class VertretungsplanWidgetProvider extends AppWidgetProvider {
	private static final String ACTION_CLICK = "ACTION_CLICK";
	public static String ANZEIGE_HEUTE = "ANZEIGE_HEUTE";
	public static String ANZEIGE_MORGEN = "ANZEIGE_MORGEN";
	public static String ANZEIGE_KEINE = "ANZEIGE_KEINE";
	public static String UPDATE_ACTION = "UPDATE";
	public static final String PREFS_NAME = "VertretungsplanLS";
	Vertretungsplan v;
	Context mContext;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		this.mContext = context;

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, VertretungsplanWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), VertretungsplanWidgetProvider.class.getName());

			//Vertretungsplan holen
			anzeigeÄndern(ANZEIGE_KEINE, remoteViews, appWidgetManager, thisAppWidget, allWidgetIds);

			remoteViews.setViewVisibility(R.id.spinner, View.VISIBLE);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

			if (settings.getString("Vertretungsplan", null) != null) {
				Gson gson = new Gson();
				v = gson.fromJson(settings.getString("Vertretungsplan", ""), Vertretungsplan.class);
				texteEinfügen(remoteViews, widgetId);
			}

			// Register an onClickListener	      
			Intent intent = new Intent(context, VertretungsplanService.class);
			intent.putExtra("AutoSync", false);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.refreshbutton, pendingIntent);

			Intent intent2 = new Intent(context, StartActivity.class);
			PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
			remoteViews.setOnClickPendingIntent(R.id.update1, pendingIntent2);
			remoteViews.setOnClickPendingIntent(R.id.update2, pendingIntent2);
			remoteViews.setOnClickPendingIntent(R.id.lsLogo, pendingIntent2);

			Intent intent3 = new Intent(context, VertretungsplanWidgetProvider.class);
			intent3.setAction(ANZEIGE_HEUTE);
			PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, intent3, 0);
			remoteViews.setOnClickPendingIntent(R.id.buttonHeute, pendingIntent3);

			Intent intent4 = new Intent(context, VertretungsplanWidgetProvider.class);
			intent4.setAction(ANZEIGE_MORGEN);
			PendingIntent pendingIntent4 = PendingIntent.getBroadcast(context, 0, intent4, 0);
			remoteViews.setOnClickPendingIntent(R.id.buttonMorgen, pendingIntent4);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String action = intent.getAction(); 
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

		if (action != null && action.equals(UPDATE_ACTION)) { 
			onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, VertretungsplanWidgetProvider.class)) ); 
		} 

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), VertretungsplanWidgetProvider.class.getName());
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

		anzeigeÄndern(intent.getAction(), remoteViews, appWidgetManager, thisAppWidget, allWidgetIds);
	}

	public void anzeigeÄndern(String anzeige, RemoteViews remoteViews, AppWidgetManager appWidgetManager, ComponentName thisAppWidget, int[] allWidgetIds) {

		if (anzeige != null) {
			if (anzeige.equals(ANZEIGE_HEUTE)) {   
				remoteViews.setViewVisibility(R.id.update1, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.update2, View.INVISIBLE);

				remoteViews.setViewVisibility(R.id.strichMorgenAktiv, View.INVISIBLE);
				remoteViews.setViewVisibility(R.id.strichMorgen, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.strichHeuteAktiv, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.strichHeute, View.INVISIBLE);
			}
			if (anzeige.equals(ANZEIGE_MORGEN)) {  
				remoteViews.setViewVisibility(R.id.update2, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.update1, View.INVISIBLE);

				remoteViews.setViewVisibility(R.id.strichHeuteAktiv, View.INVISIBLE);
				remoteViews.setViewVisibility(R.id.strichHeute, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.strichMorgenAktiv, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.strichMorgen, View.INVISIBLE);
			}
			if (anzeige.equals(ANZEIGE_KEINE)) {
				remoteViews.setViewVisibility(R.id.update1, View.INVISIBLE);
				remoteViews.setViewVisibility(R.id.update2, View.INVISIBLE);

				remoteViews.setViewVisibility(R.id.strichMorgenAktiv, View.INVISIBLE);
				remoteViews.setViewVisibility(R.id.strichMorgen, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.strichHeuteAktiv, View.INVISIBLE);
				remoteViews.setViewVisibility(R.id.strichHeute, View.VISIBLE);
			}
			for (int widgetId : allWidgetIds) {
				appWidgetManager.updateAppWidget(widgetId, remoteViews);
			}
		}
	}

	public void texteEinfügen(RemoteViews remoteViews, int widgetId) {

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);

		String textHeute = textErstellen(v, true);
		String textMorgen = textErstellen(v, false);
		String dateHeute = "";
		String dateMorgen = "";
		try {
			dateHeute = v.getDateHeute().substring(v.getDateHeute().indexOf(" ") + 1);
		} catch (NullPointerException e) {}
		try {
			dateMorgen = v.getDateMorgen().substring(v.getDateMorgen().indexOf(" ") + 1);
		} catch (NullPointerException e) {}
		
		remoteViews.setTextViewText(R.id.update1, textHeute);
		remoteViews.setTextViewText(R.id.update2, textMorgen);

		remoteViews.setTextViewText(R.id.buttonHeute, dateHeute);
		remoteViews.setTextViewText(R.id.buttonMorgen, dateMorgen);

		remoteViews.setViewVisibility(R.id.strichMorgenAktiv, View.INVISIBLE);
		remoteViews.setViewVisibility(R.id.strichMorgen, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.strichHeuteAktiv, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.strichHeute, View.INVISIBLE);

		remoteViews.setViewVisibility(R.id.update1, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.spinner, View.INVISIBLE);

		appWidgetManager.updateAppWidget(widgetId, remoteViews);

	}

	public String textErstellen(Vertretungsplan v, boolean heute) {
		String text = "";

		// Restore preferences
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		String klasse = settings.getString("klasse", "5a");

		try {		 		
			if (v.get(klasse) != null) {

				ArrayList<Vertretung> list;
				if(heute) {
					list = v.get(klasse).getVertretungHeute();
				} else {
					list = v.get(klasse).getVertretungMorgen();
				}

				if (list.size() > 0) {
					for ( Vertretung vert:list ) {
						text = text + vert.getLesson().toString() + ".: " + vert.getType() + ": " + vert.toString() + "\n";
					}
				} else {
					text = text + "keine Informationen" + "\n";
				}
			} else {
				text = text + "keine Informationen" + "\n";
			}
			return text;
		} catch (NullPointerException e) {
			return "Fehler";
		}	 		

	}
}
