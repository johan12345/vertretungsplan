package com.johan.vertretungsplan.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan_2.CloudEndpointUtils;
import com.johan.vertretungsplan_2.GCMIntentService;
import com.johan.vertretungsplan_2.vertretungsplanserializedendpoint.Vertretungsplanserializedendpoint;
import com.johan.vertretungsplan_2.vertretungsplanserializedendpoint.model.VertretungsplanSerialized;

public class BackendConnectParser extends BaseParser {
	
	private Schule schule;
	
	public BackendConnectParser(Schule schule) {
		super(schule);
		this.schule = schule;
	}

	@Override
	public Vertretungsplan getVertretungsplan() throws IOException,
			JSONException {
		Vertretungsplanserializedendpoint.Builder endpointBuilder = new Vertretungsplanserializedendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) { }
				});
		Vertretungsplanserializedendpoint endpoint = CloudEndpointUtils.updateBuilder(
				endpointBuilder).build();
		try {
			VertretungsplanSerialized vs = endpoint.getVertretungsplanSerialized(schule.getId()).execute();
			Vertretungsplan v = new Gson().fromJson(vs.getJson(), Vertretungsplan.class);
			return v;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getAllClasses() throws IOException, JSONException {
		JSONArray classesJson = schule.getData().getJSONArray("classes");
		List<String> classes = new ArrayList<String>();
		for(int i = 0; i < classesJson.length(); i++) {
			classes.add(classesJson.getString(i));
		}
		return classes;
	}

}
