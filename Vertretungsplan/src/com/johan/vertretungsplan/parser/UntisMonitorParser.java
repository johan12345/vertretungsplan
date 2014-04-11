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
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.joejernst.http.Response;
import com.johan.vertretungsplan.classes.KlassenVertretungsplan;
import com.johan.vertretungsplan.classes.Schule;
import com.johan.vertretungsplan.classes.Vertretung;
import com.johan.vertretungsplan.classes.Vertretungsplan;
import com.johan.vertretungsplan.classes.VertretungsplanTag;
import com.johan.vertretungsplan.utils.Utils;
import com.johan.vertretungsplan_2.StartActivity;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public class UntisMonitorParser extends BaseParser {
	StartActivity mActivity;
	
	public Vertretungsplan parseVertretungsplan(Schule schule) throws IOException, JSONException {	
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
				VertretungsplanTag tag = parseVertretungsplan(doc, schule.getData());
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
		
		JSONArray classesJson = schule.getData().getJSONArray("classes");
		List<String> classes = new ArrayList<String>();
		for(int i = 0; i < classesJson.length(); i++) {
			classes.add(classesJson.getString(i));
		}
		v.setAllClasses(classes);
		
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
	
	protected VertretungsplanTag parseVertretungsplan(Document doc, JSONObject data) throws JSONException {
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
 		Elements zeilen = doc.select("table.info tr:not(:contains(Nachrichten zum Tag))");
 		for ( Element i:zeilen ) {	
 			Elements spalten = i.select("td");
 			String info = "";
 			for (Element b:spalten) {
 				info += "\n" + TextNode.createFromEncoded(b.html(), doc.baseUri()).getWholeText();			 		 				
 			}
 			info = info.substring(1); //remove first \n
 			tag.getNachrichten().add(info); 			
 		}
 		
 		//VERTRETUNGSPLAN
 		if(data.getBoolean("class_in_extra_line")) { 		
	 		for (Element element:doc.select("td.inline_header")) {
	 			
	 			KlassenVertretungsplan kv = new KlassenVertretungsplan(element.text());
		 			
		 		Element zeile = null;
		 		try {
		 			zeile = element.parent().nextElementSibling();
		 			if (zeile.select("td") == null) { zeile=zeile.nextElementSibling();}
		 			while (zeile != null && !zeile.select("td").attr("class").equals("list inline_header")) {
		 				Vertretung v = new Vertretung();
		 				
		 				int i = 0;
		 				for(Element spalte : zeile.select("td")) {
		 					String type = data.getJSONArray("columns").getString(i);
		 					if(type.equals("lesson"))
		 						v.setLesson(spalte.text());
		 					else if(type.equals("subject"))
		 						v.setSubject(spalte.text());
		 					else if(type.equals("previousSubject"))
		 						v.setPreviousSubject(spalte.text());
		 					else if(type.equals("type"))
		 						v.setType(spalte.text());
		 					else if(type.equals("type-entfall")) {
		 						if(spalte.text().equals("x"))
		 							v.setType("Entfall");
		 						else
		 							v.setType("Vertretung");
		 					}
		 					else if(type.equals("room"))
		 						v.setRoom(spalte.text());
		 					else if(type.equals("desc"))
		 						v.setDesc(spalte.text());
		 					i++;
		 				}
		 	 			
		 				if(!v.getLesson().equals("")) {
			 	 			kv.add(v);
			 	 		}
		 	 			
		 	 			zeile = zeile.nextElementSibling();
		 	 			
		 			}
			 		tag.getKlassen().put(element.text(), kv);
		 		} catch (Throwable e) {
	
		 			e.printStackTrace();
		 		}
	 		}
 		} else {
 			for (Element zeile:doc.select("tr.list.odd, tr.list.even")) {
 				Vertretung v = new Vertretung();
 				String klassen = "";
 				int i = 0;
 				for(Element spalte : zeile.select("td")) {
 					String type = data.getJSONArray("columns").getString(i);
 					if(type.equals("lesson"))
 						v.setLesson(spalte.text());
 					else if(type.equals("subject"))
 						v.setSubject(spalte.text());
 					else if(type.equals("previousSubject"))
 						v.setPreviousSubject(spalte.text());
 					else if(type.equals("type"))
 						v.setType(spalte.text());
 					else if(type.equals("type-entfall")) {
 						if(spalte.text().equals("x"))
 							v.setType("Entfall");
 						else
 							v.setType("Vertretung");
 					}
 					else if(type.equals("room"))
 						v.setRoom(spalte.text());
 					else if(type.equals("previousRoom"))
 						v.setPreviousRoom(spalte.text());
 					else if(type.equals("desc"))
 						v.setDesc(spalte.text());
 					else if(type.equals("class"))
 						klassen = spalte.text();
 					i++;
 				}
 				
 				for(String klasse:klassen.split(", ")) {
					KlassenVertretungsplan kv = tag.getKlassen().get(klasse);
	 				if (kv == null)
	 					kv = new KlassenVertretungsplan(klasse);	
	 				kv.add(v);
					tag.getKlassen().put(klasse, kv);
 				}
 				
 			}
 		}
	 		
 		
 		return tag;
	}
}
