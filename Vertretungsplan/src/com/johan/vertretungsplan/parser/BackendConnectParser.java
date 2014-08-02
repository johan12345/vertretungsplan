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
import com.johan.vertretungsplan_2.VertretungsplanApplication;

public class BackendConnectParser extends BaseParser {
	
	private String schoolId;
	private static final String BASE_URL = "https://vertretungsplan-johan98.rhcloud.com/";
	private static final String VERSION = "v=" + VertretungsplanApplication.getVersion();
	
	public BackendConnectParser(Schule schule) {
		super(schule);
		this.schoolId = schule.getId();
	}
	
	public BackendConnectParser(String schoolId) {
		super(null);
		this.schoolId = schoolId;
	}

	@Override
	public Vertretungsplan getVertretungsplan() throws IOException,
			JSONException, VersionException {
		String url = BASE_URL + "vertretungsplan?school=" + schoolId + "&" + VERSION;
		Response response = new Request(url).getResource("UTF-8");
		if(response.getResponseCode() == 400)
			throw new VersionException();
		Vertretungsplan v = new Gson().fromJson(response.getBody(), Vertretungsplan.class);
		return v;
	}

	@Override
	public List<String> getAllClasses() throws IOException, JSONException {
		JSONArray classesJson;		
		String url = BASE_URL + "classes?school=" + schoolId + "&" + VERSION;
		classesJson = new JSONArray(httpGet(url, "UTF-8"));
		List<String> classes = new ArrayList<String>();
		for(int i = 0; i < classesJson.length(); i++) {
			classes.add(classesJson.getString(i));
		}
		return classes;
	}
	
	public static List<Schule> getAvailableSchools() throws IOException,
			JSONException {
		String url = BASE_URL + "schools?" + VERSION;
		Response response = new Request(url).getResource("UTF-8");
		JSONArray array = new JSONArray(response.getBody());
		List<Schule> schools = new ArrayList<Schule>();
		for(int i = 0; i < array.length(); i++) {
			schools.add(Schule.fromServerJSON(array.getJSONObject(i)));
		}
		return schools;
	}

}
