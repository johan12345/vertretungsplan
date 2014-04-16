package com.johan.vertretungsplan_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import com.johan.vertretungsplan.classes.Schule;
import com.johan.vertretungsplan.utils.Utils;
import com.johan.vertretungsplan_2.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectSchoolActivity extends Activity {
	
	ListView lstSchools;
	List<Schule> schools;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_school);
		
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		lstSchools = (ListView) findViewById(R.id.listSchools);
		try {
			schools = Utils.getSchools(this);
			lstSchools.setAdapter(new SchoolsAdapter(this, schools));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lstSchools.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Schule selectedSchool = schools.get(position);
				settings.edit().putString("selected_school", selectedSchool.getId()).commit();
				
				((VertretungsplanApplication) getApplication()).notifySchoolChanged();
				
				Intent intent = new Intent(SelectSchoolActivity.this, StartActivity.class);
				startActivity(intent);
			}
			
		});
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
}
