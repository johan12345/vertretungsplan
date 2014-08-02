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
import java.util.Collections;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.johan.vertretungsplan.comparators.AlphabeticalSchoolComparator;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.parser.BackendConnectParser;

public class SelectSchoolActivity extends Activity {
	
	ListView lstSchools;
	List<Schule> schools;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_school);
		
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		lstSchools = (ListView) findViewById(R.id.listSchools);
		
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
	}
	
	private class SchoolsAdapter extends ArrayAdapter<Schule> {
		
		Context context;

		public SchoolsAdapter(Context context, List<Schule> objects) {
			super(context, R.layout.listitem_school, objects);
			this.context = context;
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
			
			schoolName.setText(school.getName());
			schoolCity.setText(school.getCity());
			
			return view;
		}
		
	}
	
	private class LoadSchoolsTask extends AsyncTask <Void, Void, List<Schule>> {

		@Override
		protected List<Schule> doInBackground(Void... arg0) {
			try {
				return BackendConnectParser.getAvailableSchools();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(List<Schule> schools) {					
			Collections.sort(schools, new AlphabeticalSchoolComparator());
			SelectSchoolActivity.this.schools = schools;
			lstSchools.setAdapter(new SchoolsAdapter(SelectSchoolActivity.this, schools));
		}
		
	}
}
