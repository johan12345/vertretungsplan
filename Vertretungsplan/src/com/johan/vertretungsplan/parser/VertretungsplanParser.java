/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
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

import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.johan.vertretungsplan.StartActivity;
import com.johan.vertretungsplan.classes.KlassenVertretungsplan;
import com.johan.vertretungsplan.classes.Vertretung;
import com.johan.vertretungsplan.classes.Vertretungsplan;
import com.johan.vertretungsplan.classes.WinterAusfall;
import com.johan.vertretungsplan.utils.Utils;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public class VertretungsplanParser {
	StartActivity mActivity;
	
	public Vertretungsplan parseVertretungsplan(String[] klassenListe, Document docHeute, Document docMorgen, Document docWinter) {
		Log.d("Vertretungsplan", "Parser asynctask läuft");
		
		if (docHeute != null && docMorgen != null && docWinter != null) {
			if (docHeute.title().contains("Untis")) {
				Log.d("Vertretungsplan", "Untis gefunden");
				ParseResult heute = parseVertretungsplan(docHeute, klassenListe, true);
				ParseResult morgen = parseVertretungsplan(docMorgen, klassenListe, false);
				WinterAusfall winter = parseWinter(docWinter);
				Log.d("Vertretungsplan", "returning...");
				return createVertretungsplan(heute, morgen, winter);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	protected WinterAusfall parseWinter(Document doc) {
		Element stand = doc.select("pubDate").first();
    	String text = doc.select("item description").first().text(); 
    	if (text.contains("Aktuell gibt es keine Hinweise auf witterungsbedingten Unterrichtsausfall.")) {
    		text = "keine Informationen";
    	}
		return new WinterAusfall(stand.text(), text);
		
	}
	
	protected ParseResult parseVertretungsplan(Document doc, String[] klassenListe, boolean heute) {
		String datum = doc.select(".mon_title").first().text();	
    	String stand = doc.select("body").html().substring(0, doc.select("body").html().indexOf("<p>")-1);
 		ArrayList<KlassenVertretungsplan> vertretung = new ArrayList<KlassenVertretungsplan>();
 		
 		//NACHRICHTEN
 		ArrayList<Spanned> nachrichten = new ArrayList<Spanned>();
 		Elements zeilen = doc.select("table.info tr:not(:contains(Nachrichten zum Tag))");
 		for ( Element i:zeilen ) {	
 			Elements spalten = i.select("td");
 			String info = "";
 			for (Element b:spalten) {
 				info += "\n" + TextNode.createFromEncoded(b.html(), doc.baseUri()).getWholeText();			 		 				
 			}
 			info = info.substring(1); //remove first \n
 			nachrichten.add(Html.fromHtml(info));	 			
 		}
 		
 		//VERTRETUNGSPLAN
 		for (String klasse : klassenListe) {
 			
 			KlassenVertretungsplan kv = new KlassenVertretungsplan(klasse);
 			
	 		if (doc.select("td.inline_header:contains(" + klasse + ")").size() != 0) {
	 			
		 		Element zeile = null;
		 		try {
		 			zeile = doc.select("td.inline_header:contains(" + klasse + ")").first().parent().nextElementSibling();
		 			if (zeile.select("td") == null) { zeile=zeile.nextElementSibling();}
		 			while (zeile != null && !zeile.select("td").attr("class").equals("list inline_header")) {
		 				Vertretung v = new Vertretung();
		 				
		 				v.setLesson(zeile.children().get(0).text());
		 	 			v.setSubject(zeile.children().get(2).text());
		 	 			v.setChangedSubject(zeile.children().get(3).text());
		 	 			v.setType(zeile.children().get(1).text());
		 	 			v.setRoom(zeile.children().get(4).text());
		 	 			v.setDesc(zeile.children().get(5).text());
		 	 			
		 	 			if(heute) {
		 	 				kv.addHeute(v);
		 	 			} else {
		 	 				kv.addMorgen(v);
		 	 			}
		 	 			
		 	 			zeile = zeile.nextElementSibling();
		 	 			
		 			}
			 		vertretung.add(kv);
		 		} catch (Throwable e) {
		 			Log.d("Vertretungsplan", "Fehler bei Klasse " + klasse);
		 			e.printStackTrace();
		 		}
	 		}
	 		
 		}
 		
 		return new ParseResult(vertretung, nachrichten, stand, datum);
	}
 		
 	protected Vertretungsplan createVertretungsplan(ParseResult heute, ParseResult morgen, WinterAusfall winter) {
 		ArrayList<KlassenVertretungsplan> v = heute.getVertretung();
 		for (KlassenVertretungsplan kv : morgen.getVertretung()) {
 			KlassenVertretungsplan found = find(v, kv.getKlasse());
 			if (found != null) {
 				found.setVertretungMorgen(kv.getVertretungMorgen());
 			} else {
 				v.add(kv);
 			}
 		}
 		return new Vertretungsplan(v, heute.getNachrichten(), morgen.getNachrichten(), heute.getStand(), heute.getDate(), morgen.getDate(), winter);
 	}
 	
 	protected KlassenVertretungsplan find(ArrayList<KlassenVertretungsplan> v, String klasse) {
 		KlassenVertretungsplan found = null;
 		for (KlassenVertretungsplan kv : v) {
 			if (kv.getKlasse().equals(klasse)) {
 				found = kv;
 			}
 		}
 		return found;
 		
 	}
}
