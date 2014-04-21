package com.johan.vertretungsplan.additionalinfo;

import java.io.IOException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.johan.vertretungsplan.objects.AdditionalInfo;

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
