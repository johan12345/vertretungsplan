package com.johan.vertretungsplan.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.johan.vertretungsplan.objects.Schule;

public class Utils {
	public static boolean isEqual(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}

	private static String SCHULEN_DIR = "schulen";

	public static List<Schule> getSchools(Context context) throws IOException {
		AssetManager assets = context.getAssets();
		String[] files = assets.list(SCHULEN_DIR);
		int num = files.length;

		List<Schule> schools = new ArrayList<Schule>();

		StringBuilder builder = null;
		BufferedReader reader = null;
		InputStream fis = null;
		String line = null;
		String json = null;

		for (int i = 0; i < num; i++) {
			builder = new StringBuilder();
			fis = assets.open(SCHULEN_DIR + "/" + files[i]);

			reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			fis.close();
			json = builder.toString();
			try {
				Schule schule = Schule.fromJSON(files[i].replace(".json", ""),
						new JSONObject(json));
				schools.add(schule);
			} catch (JSONException e) {
				Log.w("JSON files", "Failed parsing "
						+ files[i]);
				e.printStackTrace();
			}
		}

		return schools;
	}


	public static Schule getSelectedSchool(Context context) throws IOException {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		List<Schule> schools = getSchools(context);
		String id = settings.getString("selected_school", "");
		Schule schule = null;
		int i = 0;
		while (i < schools.size() && schule == null) {
			if(schools.get(i).getId().equals(id))
				schule = schools.get(i);
			i++;
		}
		if(schule != null)
			return schule;
		else
			return null;
	}
}
