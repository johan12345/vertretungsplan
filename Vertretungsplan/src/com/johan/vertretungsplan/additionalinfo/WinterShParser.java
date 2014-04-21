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
package com.johan.vertretungsplan.additionalinfo;

import java.io.IOException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.johan.vertretungsplan.objects.AdditionalInfo;

/**
 * Parser für den witterungsbedingten Unterrichtsausfall (zumeist im Winter) in Schleswig-Holstein.
 * Die Informationen entsprechen denen von http://www.schleswig-holstein.de/Bildung/DE/Service/Lagemeldungen/lage_node.html
 * Kann für alle Schulen in Schleswig-Holstein verwendet werden.
 */
public class WinterShParser extends BaseAdditionalInfoParser {

	private static final String URL = "http://phpservice.schleswig-holstein.de/lage/feed.php";
	private static final String ENCODING = "ISO-8859-1";
	private static final String TITLE = "Witterungsbedingter Unterrichtsausfall";
	
	@Override
	public AdditionalInfo getAdditionalInfo() throws IOException,
			JSONException {
		AdditionalInfo info = new AdditionalInfo();
		String xml = httpGet(URL, ENCODING);
		Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
		String text = doc.select("item description").first().text(); 
    	if (text.contains("Aktuell gibt es keine Hinweise auf witterungsbedingten Unterrichtsausfall.")) {
    		text = "keine Informationen";
    		info.setHasInformation(false);
    	}
    	info.setText(text);
    	info.setTitle(TITLE + " (Stand: " + doc.select("pubDate").first().text() + ")");
		return info;
	}

}
