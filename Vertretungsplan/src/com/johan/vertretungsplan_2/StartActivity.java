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

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.holoeverywhere.preference.PreferenceManager;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.inscription.ChangeLogDialog;
import com.inscription.WhatsNewDialog;
import com.johan.vertretungsplan.background.AutostartService;
import com.johan.vertretungsplan.background.VertretungsplanService;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.ui.LinkAlertDialog;
import com.johan.vertretungsplan.ui.TabSwipeActivity;
import com.johan.vertretungsplan_2.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class StartActivity extends TabSwipeActivity implements VertretungFragment.Callback, NachrichtenFragment.Callback {
	public static Context appContext;
	public static final String PREFS_NAME = "VertretungsplanLS";
	private VertretungFragment vertretungFragment;
	private NachrichtenFragment nachrichtenFragment;
	
	private Vertretungsplan vertretungsplan;
	private Boolean asyncRunning = false;
	private SharedPreferences settings;
	private PagerSlidingTabStrip tabs;
	
	private boolean vertretungFragmentLoaded;
	private boolean nachrichtenFragmentLoaded;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d("Vertretungsplan", "onCreate");
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();

		settings  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if(!settings.contains("selected_school")) {
			Intent intent = new Intent(this, SelectSchoolActivity.class);
			startActivity(intent);
			finish();
		}
		
		vertretungFragment = new VertretungFragment();
		nachrichtenFragment = new NachrichtenFragment();

		addTab( "Vertretungsplan", vertretungFragment );
		addTab( "Nachrichten", nachrichtenFragment ); 

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		getResources().getConfiguration();
		if (metrics.widthPixels / (metrics.densityDpi / 160f) >= 800 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			tabs.setVisibility(View.GONE);
		}
		tabs.setOnPageChangeListener( adapter );   
		tabs.setIndicatorColor(Color.rgb(51, 139, 255));

		//Launch what's new dialog (will only be shown once)      
		showDialogs();

		if (savedInstanceState == null) {
			verbindungPruefenUndLaden();   
		} else {
			vertretungsplan = (Vertretungsplan) savedInstanceState.getParcelable("Vertretungsplan");
		}

		Log.d("Vertretungsplan", "/onCreate");

	}

	@Override
	public void onStart() {
		super.onStart();
		registerGCM();
	}
	
	private void registerGCM() {
		GCMIntentService.register(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			if (asyncRunning == false) {	
				verbindungPruefenUndLaden();
			}
			break;

		case R.id.menu_info:
			PackageInfo pInfo = null;
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			String version = pInfo.versionName;

			String msg = "Version " + version + "<br />" + getResources().getString(R.string.info_dialog);

			final Context context = this;
			AlertDialog dialog = LinkAlertDialog.create(this, "Info", msg)
					.setNegativeButton("Changelog", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//Launch change log dialog
							ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(context); 
							_ChangelogDialog.show();  
						}
					})
					.setPositiveButton("OK", null)
					.create();		
			dialog.show();	
			break;

		case R.id.menu_settings:
			Log.d("Vertretungsplan", "settings aufgerufen");
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			break;

		case R.id.menu_mail:
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"johan.forstner+app@gmail.com"});
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vertretungsplan App");

			startActivity(Intent.createChooser(emailIntent, "E-Mail senden..."));
			break;
		}
		return true;
	}

	public void verbindungPruefenUndLaden() {
		if (asyncRunning == false) {
			asyncRunning = true;
			setProgress(true);
			Intent intent = new Intent(this, VertretungsplanService.class);
			// Create a new Messenger for the communication back
			Messenger messenger = new Messenger(new ResultHandler(this));
			intent.putExtra("MESSENGER", messenger);
			startService(intent);
		}
	}

	static class ResultHandler extends Handler {
		private StartActivity activity;
		
		public ResultHandler(StartActivity activity) {
			this.activity = activity;
		}
		
		public void handleMessage(Message message) {
			if (message.arg1 == RESULT_OK) {
				Log.d("Vertretungsplan", "RESULT OK");
				Gson gson = new Gson();
				activity.setVertretungsplan((Vertretungsplan) message.getData().getSerializable("Vertretungsplan"));
				Log.d("Vertretungsplan", gson.toJson(activity.vertretungsplan));
				activity.asyncRunning = false;
			} else if (message.arg1 == Activity.RESULT_CANCELED) {
				activity.asyncRunning = false;
				Gson gson = new Gson();
				activity.vertretungsplan = gson.fromJson(activity.settings.getString("Vertretungsplan", ""), Vertretungsplan.class);
				if (activity.vertretungsplan != null) {	
					Crouton crouton = Crouton.makeText(activity, "Offline", Style.INFO);
					crouton.show();
					activity.anzeigenAktualisieren();
				} else {
					Crouton crouton = Crouton.makeText(activity, "keine Internetverbindung", Style.ALERT);
					crouton.show();
				}
			}

		};
	};


	public void anzeigenAktualisieren() {
		Log.d("Vertretungsplan", "anzeigenAktualisieren"); 
		setVertretungsplan(vertretungsplan);
	}

	private void loadParsed(Vertretungsplan v) {
		if (v != null) {
			vertretungsplan = v;
			vertretungFragment.aktualisieren(v);
			nachrichtenFragment.aktualisieren(v);
		} else {
			Crouton crouton = Crouton.makeText(this, "Konnte nicht auf den Vertretungsplan zugreifen", Style.ALERT);
			crouton.show();
		}
		setProgress(false);
	}

	private void reloadParsed() {
		loadParsed(vertretungsplan);
	}

	public void setProgress(boolean show) {
		Log.d("Vertretungsplan", "progress: " + show);
		vertretungFragment.progress(show);
		nachrichtenFragment.progress(show);
	}

	@Override
	public void onResume(){
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefEditor = prefs.edit();
		prefEditor.putBoolean("isInForeground",true);
		prefEditor.commit();      
		setAlarms();
	}
	@Override
	public void onPause(){
		super.onPause();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefEditor = prefs.edit();
		prefEditor.putBoolean("isInForeground", false);
		prefEditor.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);
	}

	public void setAlarms(){
		Intent autostartIntent = new Intent(appContext, AutostartService.class);
		appContext.startService(autostartIntent);
	}
	
	@Override
	public void onFragmentLoaded(Fragment fragment) {
		if(fragment.equals(vertretungFragment)) {
			vertretungFragmentLoaded = true;
		} else if (fragment.equals(nachrichtenFragment)) {
			nachrichtenFragmentLoaded = true;
		}
		if (nachrichtenFragmentLoaded && vertretungFragmentLoaded && vertretungsplan != null) {
			reloadParsed();
			setProgress(false);
		}
	}

	public void setVertretungsplan(Vertretungsplan v) {
		vertretungsplan = v;
		if (nachrichtenFragmentLoaded && vertretungFragmentLoaded) {
			reloadParsed();
		}
	}

	public void showDialogs() {
		boolean firstRun = settings.getBoolean("firstRun", true);
		final WhatsNewDialog whatsNewDialog = new WhatsNewDialog(this);
		if(firstRun) {
			String license = getResources().getString(R.string.license_dialog);
			AlertDialog dialog = LinkAlertDialog.create(this, "Lizenzbedingungen", license).setPositiveButton("Akzeptieren", null).create();
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					settings.edit().putBoolean("firstRun", false).commit();
					whatsNewDialog.show();
				}       		
			});
			dialog.show();
		} else {
			whatsNewDialog.show();
		}
	}

	@Override
	public Vertretungsplan getVertretungsplan() {
		return vertretungsplan;
	}

	@Override
	public void onClassSelected() {
		registerGCM();
	}
}