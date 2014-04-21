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

package com.johan.vertretungsplan.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;

/**
 * Parser für Untis-Vertretungspläne mit dem Info-Stundenplan-Layout
 * Beispiel: AKG Bensheim http://www.akg-bensheim.de/akgweb2011/content/Vertretung/default.htm
 * Wurde bisher noch nicht mit anderen Schulen getestet.
 *
 */
public class UntisInfoParser extends UntisCommonParser {
	
	private String baseUrl;
	private JSONObject data;

	public UntisInfoParser(Schule schule) {
		super(schule);
		try {
			data = schule.getData();
			baseUrl = data.getString("baseurl");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Vertretungsplan getVertretungsplan()
			throws IOException, JSONException {
		String navbarUrl = baseUrl + "/frames/navbar.htm";
		Document navbarDoc = Jsoup.parse(httpGet(navbarUrl, schule.getData().getString("encoding")));
		Element select = navbarDoc.select("select[name=week]").first();
		
		Vertretungsplan v = new Vertretungsplan();
		List<VertretungsplanTag> tage = new ArrayList<VertretungsplanTag>();
		
		String info = navbarDoc.select(".description").text();
		String stand = info.substring(info.indexOf("Stand:"));
		
		for (Element option:select.children()) {
			String week = option.attr("value");
			String url = baseUrl + "/w/" + week + "/w00000.htm";
			Document doc = Jsoup.parse(httpGet(url, schule.getData().getString("encoding")));
			Elements days = doc.select("#vertretung > p > b, #vertretung > b");
			for(Element day:days) {
				VertretungsplanTag tag = new VertretungsplanTag();
				tag.setStand(stand);
				tag.setDatum(day.text());
				Element next = null;		
				if (day.parent().tagName().equals("p")) {
					next = day.parent().nextElementSibling().nextElementSibling();
				} else
					next = day.parent().select("p").first().nextElementSibling();
				if (next.className().equals("subst")) {
					//Vertretungstabelle
					if(next.text().contains("Vertretungen sind nicht freigegeben"))
						continue;
					parseVertretungsplanTable(next, data, tag);
				} else {
					//Nachrichten
					parseNachrichten(next, data, tag);
					next = next.nextElementSibling().nextElementSibling();
					parseVertretungsplanTable(next, data, tag);
				}
				tage.add(tag);
			}
		}
		v.setTage(tage);
		return v;
	}

	@Override
	public List<String> getAllClasses() throws JSONException, IOException {
		String url = baseUrl + "/frames/navbar.htm";
		String js = httpGet(url, schule.getData().getString("encoding"));
		Pattern pattern = Pattern.compile("var classes = (\\[[^\\]]*\\]);");
		Matcher matcher = pattern.matcher(js);		
		if(matcher.find()) {
			JSONArray classesJson = new JSONArray(matcher.group(1));
			List<String> classes = new ArrayList<String>();
			for(int i = 0; i < classesJson.length(); i++) {
				classes.add(classesJson.getString(i));
			}
			return classes;
		} else {
			throw new IOException();
		}
	}

}
