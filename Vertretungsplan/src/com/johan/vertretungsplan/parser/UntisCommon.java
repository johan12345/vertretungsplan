package com.johan.vertretungsplan.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.johan.vertretungsplan.classes.KlassenVertretungsplan;
import com.johan.vertretungsplan.classes.Vertretung;
import com.johan.vertretungsplan.classes.VertretungsplanTag;

public class UntisCommon {
	public static void parseVertretungsplanTable(Element table, JSONObject data, VertretungsplanTag tag) throws JSONException {
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
 				
 				for(String klasse:klassen.split(", ")) {
					KlassenVertretungsplan kv = tag.getKlassen().get(klasse);
	 				if (kv == null)
	 					kv = new KlassenVertretungsplan(klasse);	
	 				kv.add(v);
					tag.getKlassen().put(klasse, kv);
 				}
 			}
 		}
	}
	
	public static void parseNachrichten(Element table, JSONObject data, VertretungsplanTag tag) {
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