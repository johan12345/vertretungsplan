package com.johan.vertretungsplan.parser;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;

public abstract class BaseParser {
	protected Schule schule;
	
	public BaseParser(Schule schule) {
		this.schule = schule;
	}
	
	public abstract Vertretungsplan getVertretungsplan() throws IOException, JSONException;
	public abstract List<String> getAllClasses() throws IOException, JSONException;
	
	protected String httpGet(String url, String encoding) throws IOException {
		Response response = new Request(url).getResource(encoding);
		return response.getBody();
	}

	public static BaseParser getInstance(Schule schule) {
		BaseParser parser = null;
		if(schule != null) {
			if (schule.getApi().equals("untis-monitor")) {
				parser = new UntisMonitorParser(schule);
			} else if (schule.getApi().equals("untis-info")) {
				parser = new UntisInfoParser(schule);
			} //else if ... (andere Parser)
		}
		return parser;
	}
}
