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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.johan.vertretungsplan.comparators.AlphabeticalSchoolComparator;
import com.johan.vertretungsplan.comparators.DistanceSchoolComparator;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.parser.BackendConnectParser;
import com.johan.vertretungsplan.utils.Utils;

public class SelectSchoolActivity extends Activity {
	
	private ListView lstSchools;
	private List<Schule> schools;
	private ProgressBar progress;
	private FrameLayout suggestSchool;
	private LinearLayout locate;
	private TextView tvLocateString;
	private ImageView ivLocationIcon;
	private boolean loaded;
	private boolean visible;
	private Status status = Status.LIST;
	
	private enum Status {
		GEO, LOADING, LIST
	}
	
	@Override
	protected void onPause() {
		visible = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		visible = true;
		super.onResume();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_school);
		
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		lstSchools = (ListView) findViewById(R.id.listSchools);
		progress = (ProgressBar) findViewById(R.id.progress);
		suggestSchool = (FrameLayout) findViewById(R.id.suggestSchool);
		locate = (LinearLayout) findViewById(R.id.llLocate);
		tvLocateString = (TextView) findViewById(R.id.tvLocateString);
		ivLocationIcon = (ImageView) findViewById(R.id.ivLocationIcon);
		
		new LoadSchoolsTask().execute();

		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Schule selectedSchool = schools.get(position);
				settings.edit().putString("selected_school", selectedSchool.getId()).commit();
				
				((VertretungsplanApplication) getApplication()).notifySchoolChanged();
				
				Intent intent = new Intent(SelectSchoolActivity.this, StartActivity.class);
				startActivity(intent);
			}
			
		};
		lstSchools.setOnItemClickListener(listener);
		
		suggestSchool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Utils.email(SelectSchoolActivity.this,
						"johan.forstner+vertretungsplan@gmail.com",
						"Vertretungsplan App",
						"Schule: \nOrt: \nAdresse des Online-Vertretungsplans:");
			}
			
		});
		
		locate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(loaded) {
					if (status == Status.GEO) {
						status = Status.LIST;
						tvLocateString.setText(R.string.geolocate);
						ivLocationIcon.setImageResource(R.drawable.ic_action_location_found);
						showList();
					} else if (status == Status.LIST) {
						status = Status.LOADING;
						tvLocateString.setText(R.string.geolocate_progress);
						ivLocationIcon.setImageResource(R.drawable.ic_action_location_found);
						showListGeo();
					}
				} else {
					Toast.makeText(SelectSchoolActivity.this, "Bitte warten, bis die Liste geladen ist...", Toast.LENGTH_LONG).show();
				}
			}
			
		});
	}
	
	private void showList() {
		Collections.sort(schools, new AlphabeticalSchoolComparator());
		lstSchools.setAdapter(new SchoolsAdapter(SelectSchoolActivity.this, schools, false));
	}
	
	private void showListGeo() {
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // no GPS
		final String provider = locationManager.getBestProvider(criteria, true);

		if (provider == null) {
			Log.d("vertretungsplan", "provider==null");
			return;
		}
		locationManager.requestLocationUpdates(provider, 0, 0,
				new LocationListener() {
					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}

					@Override
					public void onProviderEnabled(String provider) {
					}

					@Override
					public void onProviderDisabled(String provider) {
					}

					@Override
					public void onLocationChanged(Location location) {
						if(!visible) return;
						if (location != null) {
							double lat = location.getLatitude();
							double lon = location.getLongitude();
							for(Schule school:schools) {
								float[] result = new float[1];
								Location.distanceBetween(lat, lon, school.getGeo()[0], school.getGeo()[1], result);
								school.setDistance(result[0]);
								Log.d("vertretungsplan", school.getName() + ": " + school.getDistance());
							}
							Collections.sort(schools, new DistanceSchoolComparator());
							lstSchools.setAdapter(new SchoolsAdapter(SelectSchoolActivity.this, schools, true));
						}
						tvLocateString.setText(R.string.alphabetic_list);
						ivLocationIcon.setImageResource(R.drawable.ic_action_view_as_list);
						status = Status.GEO;
					}
			});
	}
	
	private class SchoolsAdapter extends ArrayAdapter<Schule> {
		
		private Context context;
		private boolean distance;

		public SchoolsAdapter(Context context, List<Schule> objects, boolean distance) {
			super(context, R.layout.listitem_school, objects);
			this.context = context;
			this.distance = distance;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if(convertView != null)
				view = convertView;
			else {
				LayoutInflater inflater = (LayoutInflater) context
				        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.listitem_school, parent, false);
			}
			
			Schule school = getItem(position);
			
			TextView schoolName = (TextView) view.findViewById(R.id.school_name);
			TextView schoolCity = (TextView) view.findViewById(R.id.school_city);
			TextView schoolDistance = (TextView) view.findViewById(R.id.school_distance);
			
			schoolName.setText(school.getName());
			schoolCity.setText(school.getCity());
			if(distance) {
				DecimalFormat format = new DecimalFormat("0.0");
				schoolDistance.setVisibility(View.VISIBLE);
				schoolDistance.setText(format.format(school.getDistance() / 1000) + " km");
			} else {
				schoolDistance.setVisibility(View.GONE);
			}
			
			return view;
		}
		
	}
	
	private class LoadSchoolsTask extends AsyncTask <Void, Void, List<Schule>> {
		
		@Override
		protected void onPreExecute() {
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<Schule> doInBackground(Void... arg0) {
			try {
				return BackendConnectParser.getAvailableSchools();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<Schule> schools) {			
			progress.setVisibility(View.GONE);
			if(schools != null) {
				Collections.sort(schools, new AlphabeticalSchoolComparator());
				SelectSchoolActivity.this.schools = schools;
				lstSchools.setAdapter(new SchoolsAdapter(SelectSchoolActivity.this, schools, false));
				loaded = true;
			} else {
				Toast.makeText(SelectSchoolActivity.this, "Zum wählen einer Schule wird eine Internetverbindung benötigt!", Toast.LENGTH_LONG).show();
			}
		}
		
	}
}
