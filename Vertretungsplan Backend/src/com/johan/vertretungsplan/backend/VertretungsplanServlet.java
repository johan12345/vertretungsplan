package com.johan.vertretungsplan.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;

@SuppressWarnings("serial")
public class VertretungsplanServlet extends HttpServlet {
	
	private static final String GEOCODE_URL = "http://dev.virtualearth.net/REST/v1/Locations";
	private static final Logger log = Logger.getLogger(VertretungsplanServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {     
//		JSONObject schule = new JSONObject(req.getParameter("schule"));
//		int i = 0;
//		while (schule == null && i < schulen.length()) {
//			JSONObject current = schulen.getJSONObject(i);
//			if(current.getString("name").equals(name)
//					&& current.getString("city").equals(city))
//				schule = current;
//			i++;
//		}
//		
//        try {
//			Vertretungsplan v = getVertretungsplan(schule);
//			Gson gson = new Gson();
//			resp.setContentType("text/json; charset=utf-8");
//			resp.getWriter().print(gson.toJson(v));
//		} catch (VertretungsplanErrorException e) {
//			resp.setContentType("text/plain");
//			resp.getWriter().println(e.getMessage());
//		}
	}
	
	
	public static JSONObject createMonitorPlan(String url) throws MalformedURLException, IOException, JSONException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.connect();
		int status = conn.getResponseCode();
		if(status == 200) {
			String html = readStream(conn.getInputStream(), "ISO-8859-1");
			Document doc = Jsoup.parse(html);
			if(doc.getElementsByTag("frameset").size() > 0) {
				System.out.println("Schule benutzt frames");
			} else {
				return createMonitorJSON(doc, url);
			}
		}
		return null;
	}

	private static JSONObject createMonitorJSON(Document doc, String url) throws JSONException, IOException {
		JSONObject json = new JSONObject();
		
		json.put("api", "untis-monitor");
		
		//Information about the school
		Element info = doc.select(".mon_head td p").first();
		String infoText = info.text();
		
		String plz;
		
		//Möglichkeit 1: Stadt steht hinter der PLZ
		Pattern regex = Pattern.compile("(.*)([A-Z]-\\d*) (.*), .* Stand:");
		Matcher matcher = regex.matcher(infoText);
		if(matcher.find()) {
			json.put("name", matcher.group(1).trim());
			plz = matcher.group(2).trim();
			json.put("city", matcher.group(3).trim());		
		} else {
			//Möglichkeit 2: Stadt steht hinter dem Schulnamen
			regex = Pattern.compile("(.*) (.*)   ([A-Z]-\\d*), .* Stand:");
			matcher = regex.matcher(infoText);
			if(matcher.find()) {
				json.put("name", matcher.group(1).trim());
				json.put("city", matcher.group(2).trim());
				plz = matcher.group(3).trim();				
			} else {			
				throw new IOException("Fehler beim Regex: Info-Text");
			}
		}
		
		//Position nach PLZ herausfinden
		JSONObject geocodeResult = geocode(plz);
		JSONArray results = geocodeResult.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources");
		if(results.length() > 0
				&& results.getJSONObject(0).getJSONObject("address").getString("locality").equals(json.get("city"))) {				
			JSONObject result = results.getJSONObject(0);						
			JSONArray geo = result.getJSONObject("point").getJSONArray("coordinates");
			json.put("geo", geo);
		} else {
			
		}
			
		
		//Parser-Daten
		JSONObject data = new JSONObject();
		//Encoding
		data.put("encoding", "ISO-8859-1");
		//Spalten
		Element table = doc.select(".mon_list").first();
		Element header = table.select("tr.list:not(.odd,.even)").last();		
		JSONArray columns = new JSONArray();
		
		int i = 0;
		for(Element head:header.children()) {
			switch(head.text()) {
			case "Klasse(n)":
			case "(Klasse(n))":
			case "Klasse":
				columns.put("class");
				break;
			case "Stunde":
				columns.put("lesson");
				break;
			case "Art":
				columns.put("type");
				break;
			case "Entfall":
				columns.put("type-entfall");
				break;
			case "Fach":
				columns.put("subject");
				break;
			case "(Fach)":
				columns.put("previousSubject");
				break;
			case "Raum":
				columns.put("room");
				break;
			case "(Raum)":
				columns.put("previousRoom");
				break;
			case "Vertreter":
			case "Lehrer":
			case "Vertretungslehrer":
				columns.put("teacher");
				break;
			case "(Lehrer)":
				columns.put("previousTeacher");
				break;
			case "Vertretungs-Text":
			case "Vertr-Text-2":
			case "Text":
				columns.put("desc");
				break;
			case "verlegt von":
			case "verlegt nach":
			case "Neu":
			case "Vertr. von":
			case "Datum":
			case "Tag":
			case "Beschr.":
				columns.put("ignore");
				break;
			default:
				columns.put("unknown");
			}
			i++;
		}
		if(columns.toString().contains("class"))
			data.put("class_in_extra_line", false);
		else
			data.put("class_in_extra_line", true);
		data.put("columns", columns);
		
		//URLs
		JSONArray urls = new JSONArray();
		JSONObject urlobj = new JSONObject();
		urlobj.put("url", url);
		urls.put(urlobj);
		data.put("urls", urls);
		
		json.put("data", data);
		
		return json;
	}
	
	public static String getTableHeaders(JSONObject json) throws JSONException, IOException {
		String url = json.getJSONObject("data").getJSONArray("urls").getJSONObject(0).getString("url");
		Document doc = Jsoup.connect(url).get();
		Element table = doc.select(".mon_list").first();
		
		int i = 0;
		for(Element e:table.select("tr")) {
			if(e.classNames().contains("odd") || e.classNames().contains("even")) {
				if(i > 1)
					e.remove();
				i++;
			}
		}
		
		return table.parent().html();
	}
	
	private static JSONObject geocode(String plz) {
		String params = "";
		try {
			params = "postalCode=" + plz.substring(2)
					+ "&countryRegion=DE&key=" + Settings.API_KEY;
			URL url = new URL(GEOCODE_URL + "?" + params);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			int status = conn.getResponseCode();

	        if(status == 200) {         
	        	String string = readStream(conn.getInputStream(), "UTF-8");
	        	log.info(string);
	            return new JSONObject(string);
	        }
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String readStream(InputStream stream, String encoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, encoding));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();
	}
	
	/**
	 * A general exception containing a human-readable error message
	 */
	public class VertretungsplanErrorException extends Exception {

		public VertretungsplanErrorException(String message) {
			super(message);
		}

	}
}
