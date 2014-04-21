/*  LS Vertretungsplan - Android-App f�r den Vertretungsplan der Lornsenschule Schleswig
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
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;
import com.johan.vertretungsplan_2.StartActivity;

import android.util.Log;

public class UntisMonitorParser extends UntisCommonParser {
	
	public UntisMonitorParser(Schule schule) {
		super(schule);
	}

	StartActivity mActivity;
	
	public Vertretungsplan getVertretungsplan() throws IOException, JSONException {	
		JSONArray urls = schule.getData().getJSONArray("urls");
		String encoding = schule.getData().getString("encoding");
		List<Document> docs = new ArrayList<Document>();
		
		for(int i = 0; i < urls.length(); i++) {
			JSONObject url = urls.getJSONObject(i);
			loadUrl(url.getString("url"), encoding, url.getBoolean("following"), docs);
		}
		
		HashMap<String, VertretungsplanTag> tage = new HashMap<String, VertretungsplanTag>();
		for(Document doc:docs) {
			if (doc.title().contains("Untis")) {
				VertretungsplanTag tag = parseVertretungsplanTag(doc, schule.getData());
				if(!tage.containsKey(tag.getDatum())) {
					tage.put(tag.getDatum(), tag);
				} else {
					VertretungsplanTag tagToMerge = tage.get(tag.getDatum());
					tagToMerge.merge(tag);
					tage.put(tag.getDatum(), tagToMerge);
				}
			} else {
				//Fehler
			}
		}
		Vertretungsplan v = new Vertretungsplan();
		v.setTage(new ArrayList<VertretungsplanTag>(tage.values()));
		
		return v;
	}
	
	private void loadUrl(String url, String encoding, boolean following, List<Document> docs, String startUrl) throws IOException {
		String html = httpGet(url, encoding);
		Document doc = Jsoup.parse(html);
		docs.add(doc);
		if(following
				&& doc.select("meta[http-equiv=refresh]").size() > 0) {
			Element meta = doc.select("meta[http-equiv=refresh]").first();
			String attr = meta.attr("content").toLowerCase();
			String redirectUrl = url.substring(0, url.lastIndexOf("/") + 1) + attr.substring(attr.indexOf("url=") + 4);
			Log.d("Vertretungsplan", redirectUrl);
			if (!redirectUrl.equals(startUrl))
				loadUrl(redirectUrl, encoding, true, docs, startUrl);
		}
	}
	
	private void loadUrl(String url, String encoding, boolean following, List<Document> docs) throws IOException {
		loadUrl(url, encoding, following, docs, url);
	}
	
//	protected WinterAusfall parseWinter(Document doc) {
//		Element stand = doc.select("pubDate").first();
//    	String text = doc.select("item description").first().text(); 
//    	if (text.contains("Aktuell gibt es keine Hinweise auf witterungsbedingten Unterrichtsausfall.")) {
//    		text = "keine Informationen";
//    	}
//		return new WinterAusfall(stand.text(), text);
//		
//	}
	
	protected VertretungsplanTag parseVertretungsplanTag(Document doc, JSONObject data) throws JSONException {
 		VertretungsplanTag tag = new VertretungsplanTag();
		tag.setDatum(doc.select(".mon_title").first().text().replaceAll(" \\(Seite \\d / \\d\\)", ""));	
		if(data.getBoolean("stand_links")) {
			tag.setStand(doc.select("body").html().substring(0, doc.select("body").html().indexOf("<p>")-1));
		} else {
			Element stand = doc.select("table.mon_head td[align=right] p").first();
			String info = stand.text();
			tag.setStand(info.substring(info.indexOf("Stand:")));
		}
 		
 		//NACHRICHTEN
 		parseNachrichten(doc.select("table.info").first(), data, tag);
 		
 		//VERTRETUNGSPLAN
 		parseVertretungsplanTable(doc, data, tag);	 		
 		
 		return tag;
	}
	
	public List<String> getAllClasses() throws JSONException {
		JSONArray classesJson = schule.getData().getJSONArray("classes");
		List<String> classes = new ArrayList<String>();
		for(int i = 0; i < classesJson.length(); i++) {
			classes.add(classesJson.getString(i));
		}
		return classes;
	}
}
