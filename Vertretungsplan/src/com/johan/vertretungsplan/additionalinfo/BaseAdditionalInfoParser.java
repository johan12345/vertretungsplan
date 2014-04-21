package com.johan.vertretungsplan.additionalinfo;

import java.io.IOException;

import org.json.JSONException;

import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.AdditionalInfo;

public abstract class BaseAdditionalInfoParser {
	public BaseAdditionalInfoParser() {
	}
	
	public abstract AdditionalInfo getAdditionalInfo() throws IOException, JSONException;
	
	protected String httpGet(String url, String encoding) throws IOException {
		Response response = new Request(url).getResource(encoding);
		return response.getBody();
	}

	public static BaseAdditionalInfoParser getInstance(String type) {
		BaseAdditionalInfoParser parser = null;
		if (type.equals("winter-sh")) {
			parser = new WinterShParser();
		} //else if ... (andere Parser)
		return parser;
	}
}
