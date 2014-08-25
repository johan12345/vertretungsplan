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

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.preference.PreferenceManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.parser.BaseParser.UnauthorizedException;
import com.johan.vertretungsplan.parser.BaseParser.VersionException;
import com.johan.vertretungsplan.ui.LinkAlertDialog;
import com.johan.vertretungsplan.ui.TabSwipeActivity;
import com.johan.vertretungsplan.widget.VertretungsplanWidgetProvider;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class StartActivity extends TabSwipeActivity implements
		VertretungFragment.Callback, NachrichtenFragment.Callback {
	public static Context appContext;
	public static final String PREFS_NAME = "VertretungsplanLS";
	private VertretungFragment vertretungFragment;
	private NachrichtenFragment nachrichtenFragment;

	private Vertretungsplan vertretungsplan;
	private Boolean asyncRunning = false;
	private SharedPreferences settings;
	private PagerSlidingTabStrip tabs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();

		settings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (!settings.contains("selected_school")) {
			Intent intent = new Intent(this, SelectSchoolActivity.class);
			startActivity(intent);
			finish();
		}

		vertretungFragment = new VertretungFragment();
		nachrichtenFragment = new NachrichtenFragment();

		addTab("Vertretungsplan", vertretungFragment);
		addTab("Nachrichten", nachrichtenFragment);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		getResources().getConfiguration();
		if (metrics.widthPixels / (metrics.densityDpi / 160f) >= 800
				&& getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
			tabs.setVisibility(View.GONE);
		}
		tabs.setOnPageChangeListener(adapter);
		tabs.setIndicatorColor(Color.rgb(51, 139, 255));

		// Launch license dialog
		showDialogs();

		if (savedInstanceState == null) {
			Log.d("vertretungsplan", "state == null");
			new GetVertretungsplanTask().execute();
		} else {
			Log.d("vertretungsplan", "load");
			vertretungsplan = new Gson().fromJson(
					savedInstanceState.getString("vertretungsplan"),
					Vertretungsplan.class);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		registerGCM();
		analyticsStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		analyticsStop();
	}

	private void analyticsStart() {
		Tracker t = ((VertretungsplanApplication) getApplication())
				.getTracker();
		t.send(new HitBuilders.AppViewBuilder()
				.setCustomDimension(1,
						settings.getString("selected_school", "unbekannt"))
				.setCustomDimension(2,
						settings.getString("klasse", "unbekannt")).build());
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	private void analyticsStop() {
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
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
				new GetVertretungsplanTask().execute();
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

			String msg = "Version " + version + "<br />"
					+ getResources().getString(R.string.info_dialog);

			AlertDialog dialog = LinkAlertDialog.create(this, "Info", msg)
					.setPositiveButton("OK", null).create();
			dialog.show();
			break;

		case R.id.menu_settings:
			Log.d("Vertretungsplan", "settings aufgerufen");
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			break;

		case R.id.menu_mail:
			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND);

			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "johan.forstner+app@gmail.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Vertretungsplan App");

			startActivity(Intent.createChooser(emailIntent, "E-Mail senden..."));
			break;
		}
		return true;
	}

	private void refreshFragments() {
		if (vertretungsplan != null) {
			vertretungFragment.setVertretungsplan(vertretungsplan);
			nachrichtenFragment.setVertretungsplan(vertretungsplan);
		} else {
			Crouton crouton = Crouton.makeText(this,
					"Konnte nicht auf den Vertretungsplan zugreifen",
					Style.ALERT);
			crouton.show();
		}
		setProgress(false);
	}

	public void setProgress(boolean show) {
		vertretungFragment.progress(show);
		nachrichtenFragment.progress(show);
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefEditor = prefs.edit();
		prefEditor.putBoolean("isInForeground", true);
		prefEditor.commit();
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefEditor = prefs.edit();
		prefEditor.putBoolean("isInForeground", false);
		prefEditor.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle out) {
		out.putString("vertretungsplan", new Gson().toJson(vertretungsplan));
		super.onSaveInstanceState(out);
	}

	private void setVertretungsplan(Vertretungsplan v) {
		vertretungsplan = v;
		refreshFragments();
	}

	public void showDialogs() {
		boolean firstRun = settings.getBoolean("firstRun", true);
		if (firstRun) {
			String license = getResources().getString(R.string.license_dialog);
			AlertDialog dialog = LinkAlertDialog
					.create(this, "Lizenzbedingungen", license)
					.setPositiveButton("Akzeptieren", null).create();
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					settings.edit().putBoolean("firstRun", false).commit();
				}
			});
			dialog.show();
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

	private class GetVertretungsplanTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected void onPreExecute() {
			asyncRunning = true;
			setProgress(true);
		}

		@Override
		protected Object doInBackground(Void... params) {
			BaseParser parser = ((VertretungsplanApplication) getApplication())
					.getParser();

			if (parser == null)
				return null;

			try {
				Vertretungsplan v = parser.getVertretungsplan();
				settings.edit()
						.putString("Vertretungsplan", new Gson().toJson(v))
						.commit();

				AppWidgetManager mgr = AppWidgetManager
						.getInstance(StartActivity.this);
				int[] ids = mgr
						.getAppWidgetIds(new ComponentName(StartActivity.this,
								VertretungsplanWidgetProvider.class));
				new VertretungsplanWidgetProvider().onUpdate(
						StartActivity.this, mgr, ids);
				return v;
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			asyncRunning = false;
			setProgress(false);
			if (result instanceof UnauthorizedException) {
				Crouton crouton = Crouton
						.makeText(
								StartActivity.this,
								"Benutzerdaten sind falsch. Bitte klicke hier, um dich erneut einzuloggen.",
								Style.ALERT);
				crouton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(StartActivity.this,
								SelectSchoolActivity.class);
						startActivity(intent);
						finish();
					}

				});
				crouton.setConfiguration(new Configuration.Builder()
						.setDuration(Configuration.DURATION_INFINITE).build());
				crouton.show();
			} else if (result instanceof VersionException) {
				Crouton crouton = Crouton
						.makeText(
								StartActivity.this,
								"Bitte klicke hier, um die neueste Version der App zu installieren",
								Style.ALERT);
				crouton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						final String appPackageName = StartActivity.this
								.getPackageName(); // getPackageName() from
													// Context or Activity
													// object
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse("market://details?id="
											+ appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://play.google.com/store/apps/details?id="
											+ appPackageName)));
						}
					}

				});
				crouton.setConfiguration(new Configuration.Builder()
						.setDuration(Configuration.DURATION_INFINITE).build());
				crouton.show();
			} else if (result instanceof Exception || result == null) {
				Gson gson = new Gson();
				StartActivity.this.vertretungsplan = gson.fromJson(
						StartActivity.this.settings.getString(
								"Vertretungsplan", ""), Vertretungsplan.class);
				if (StartActivity.this.vertretungsplan != null) {
					Crouton crouton = Crouton.makeText(StartActivity.this,
							"Offline", Style.INFO);
					crouton.show();
					StartActivity.this.refreshFragments();
				} else {
					Crouton crouton = Crouton.makeText(StartActivity.this,
							"keine Internetverbindung", Style.ALERT);
					crouton.show();
				}
			} else if (result instanceof Vertretungsplan) {
				setVertretungsplan((Vertretungsplan) result);
			}
		}

	}
}