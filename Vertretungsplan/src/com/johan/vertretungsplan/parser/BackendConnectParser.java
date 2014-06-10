package com.johan.vertretungsplan.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;

public class BackendConnectParser extends BaseParser {
	
	private Schule schule;
	private static final String BASE_URL = "https://vertretungsplan-johan98.rhcloud.com/";
	
	public BackendConnectParser(Schule schule) {
		super(schule);
		this.schule = schule;
	}

	@Override
	public Vertretungsplan getVertretungsplan() throws IOException,
			JSONException {
		String url = BASE_URL + "vertretungsplan?school=" + schule.getId();
		try {
			Response response = new Request(url).getResource("UTF-8");
			Vertretungsplan v = new Gson().fromJson(response.getBody(), Vertretungsplan.class);
			return v;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getAllClasses() throws IOException, JSONException {
		JSONArray classesJson;
		if(schule.getData().has("classes")) {
			classesJson = schule.getData().getJSONArray("classes");
		} else {
			String url = BASE_URL + "classes?school=" + schule.getId();
			classesJson = new JSONArray(httpGet(url, "UTF-8"));
		}
		List<String> classes = new ArrayList<String>();
		for(int i = 0; i < classesJson.length(); i++) {
			classes.add(classesJson.getString(i));
		}
		return classes;
	}

}
