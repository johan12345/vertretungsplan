package com.johan.vertretungsplan.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.classes.Schule;
import com.johan.vertretungsplan.classes.Vertretungsplan;

public abstract class BaseParser {
	public abstract Vertretungsplan parseVertretungsplan(Schule schule) throws IOException, JSONException;
	
	protected String httpGet(String url, String encoding) throws IOException {
		Response response = new Request(url).getResource(encoding);
		return response.getBody();
	}
}
