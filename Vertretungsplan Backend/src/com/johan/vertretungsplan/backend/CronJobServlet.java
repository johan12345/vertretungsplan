package com.johan.vertretungsplan.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.*;

import org.json.JSONObject;

import com.googlecode.objectify.ObjectifyService;
import com.johan.vertretungsplan.additionalinfo.BaseAdditionalInfoParser;
import com.johan.vertretungsplan.objects.SavedVertretungsplan;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.parser.BaseParser;

import static com.googlecode.objectify.ObjectifyService.ofy;

@SuppressWarnings("serial")
public class CronJobServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException, IOException {
		String schoolId = "Schleswig_Lornsenschule";
		File file = new File("WEB-INF/schulen/" + schoolId + ".json");
		String jsonString = getFileContent(new FileInputStream(file), "UTF-8");
		JSONObject json = new JSONObject(jsonString);
		Schule schule = Schule.fromJSON(schoolId, json);
		
		Vertretungsplan v = BaseParser.getInstance(schule).getVertretungsplan();
		
		List<BaseAdditionalInfoParser> additionalInfoParsers = new ArrayList<BaseAdditionalInfoParser>();
		for(String type:schule.getAdditionalInfos()) {
			additionalInfoParsers.add(BaseAdditionalInfoParser.getInstance(type));
		}
		
		for(BaseAdditionalInfoParser additionalInfoParser:additionalInfoParsers) {
			v.getAdditionalInfos().add(additionalInfoParser.getAdditionalInfo());
		}
		
		SavedVertretungsplan sv = new SavedVertretungsplan();
		sv.school_id = schoolId;
		sv.date = new Date();
		sv.vertretungsplan = v;
		
		ObjectifyService.register(SavedVertretungsplan.class);

		ofy().delete().type(SavedVertretungsplan.class).id(schoolId).now();
		
		ofy().save().entity(sv);
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
