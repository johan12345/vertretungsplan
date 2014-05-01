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
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.johan.vertretungsplan.objects.KlassenVertretungsplan;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretung;
import com.johan.vertretungsplan.objects.VertretungsplanTag;

/**
 * Enthält gemeinsam genutzte Funktionen für die Parser für
 * Untis-Vertretungspläne
 *
 */
public abstract class UntisCommonParser extends BaseParser {
	
	public UntisCommonParser(Schule schule) {
		super(schule);
	}

	/**
	 * Parst eine Vertretungstabelle eines Untis-Vertretungsplans
	 * @param table das <code>table</code>-Element des HTML-Dokuments, das geparst werden soll
	 * @param data Daten von der Schule (aus <code>Schule.getData()</code>)
	 * @param tag der {@link VertretungsplanTag} in dem die Vertretungen gespeichert werden sollen
	 * @throws JSONException
	 */
	protected void parseVertretungsplanTable(Element table, JSONObject data, VertretungsplanTag tag) throws JSONException {
		if(data.optBoolean("class_in_extra_line")) { 		
	 		for (Element element:table.select("td.inline_header")) {
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
 			for (Element zeile:table.select("tr.list.odd, tr.list.even")) {
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
 				List<String> affectedClasses;
 				if(data.optBoolean("classes_separated", true)) {
 					affectedClasses = Arrays.asList(klassen.split(", "));
 				} else {
 					affectedClasses = new ArrayList<String>();
 					try {
						for (String klasse:getAllClasses()) { //TODO: Gibt es eine bessere Möglichkeit?
							StringBuilder regex = new StringBuilder();
							for(char character:klasse.toCharArray()) {
								regex.append(character);
								regex.append(".*");
							}
							if(klassen.matches(regex.toString()))
								affectedClasses.add(klasse);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
 				}
 				for(String klasse:affectedClasses) {
					KlassenVertretungsplan kv = tag.getKlassen().get(klasse);
	 				if (kv == null)
	 					kv = new KlassenVertretungsplan(klasse);	
	 				kv.add(v);
					tag.getKlassen().put(klasse, kv);
 				}
 			}
 		}
	}
	
	/**
	 * Parst eine "Nachrichten zum Tag"-Tabelle aus Untis-Vertretungsplänen
	 * @param table das <code>table</code>-Element des HTML-Dokuments, das geparst werden soll
	 * @param data Daten von der Schule (aus <code>Schule.getData()</code>)
	 * @param tag der {@link VertretungsplanTag} in dem die Nachrichten gespeichert werden sollen
	 */
	protected void parseNachrichten(Element table, JSONObject data, VertretungsplanTag tag) {
		Elements zeilen = table.select("tr:not(:contains(Nachrichten zum Tag))");
 		for ( Element i:zeilen ) {	
 			Elements spalten = i.select("td");
 			String info = "";
 			for (Element b:spalten) {
 				info += "\n" + TextNode.createFromEncoded(b.html(), null).getWholeText();			 		 				
 			}
 			info = info.substring(1); //remove first \n
 			tag.getNachrichten().add(info); 			
 		}
	}
}