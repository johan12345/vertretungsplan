package com.johan.vertretungsplan.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.johan.vertretungsplan.additionalinfo.BaseAdditionalInfoParser;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.parser.BaseParser;

@SuppressWarnings("serial")
public class VertretungsplanJSONServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String schoolId = req.getParameter("school");
			if(schoolId == null) schoolId = "Schleswig_Lornsenschule";
			File file = new File("WEB-INF/schulen/" + schoolId + ".json");
			String jsonString = getFileContent(new FileInputStream(file), "UTF-8");
			JSONObject json = new JSONObject(jsonString);
			Schule schule = Schule.fromJSON("", json);
			
			Vertretungsplan v = BaseParser.getInstance(schule).getVertretungsplan();
			
			List<BaseAdditionalInfoParser> additionalInfoParsers = new ArrayList<BaseAdditionalInfoParser>();
			for(String type:schule.getAdditionalInfos()) {
				additionalInfoParsers.add(BaseAdditionalInfoParser.getInstance(type));
			}
			
			for(BaseAdditionalInfoParser additionalInfoParser:additionalInfoParsers) {
				v.getAdditionalInfos().add(additionalInfoParser.getAdditionalInfo());
			}
			
			Gson gson = new Gson();
			String output = gson.toJson(v);
			resp.setContentType("application/json");
			resp.getOutputStream().print(output);
		} catch (JSONException | IOException e) {
			resp.setStatus(404);
		}
	}
	
	public static String getFileContent(FileInputStream fis, String encoding ) throws IOException {
		  BufferedReader br =
		           new BufferedReader( new InputStreamReader(fis, encoding ));
	      StringBuilder sb = new StringBuilder();
	      String line;
	      while(( line = br.readLine()) != null ) {
	         sb.append( line );
	         sb.append( '\n' );
	      }
	      return sb.toString();	
	}
}
