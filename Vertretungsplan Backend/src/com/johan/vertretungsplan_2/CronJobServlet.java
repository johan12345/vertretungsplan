package com.johan.vertretungsplan_2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.johan.vertretungsplan.additionalinfo.BaseAdditionalInfoParser;
import com.johan.vertretungsplan.objects.AdditionalInfo;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;
import com.johan.vertretungsplan.parser.BaseParser;

@SuppressWarnings("serial")
public class CronJobServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException, IOException {
		try {
			
			
			String schoolId = "Schleswig_Lornsenschule";
			File file = new File("WEB-INF/schulen/" + schoolId + ".json");
			String jsonString = getFileContent(new FileInputStream(file), "UTF-8");
			JSONObject json;
			json = new JSONObject(jsonString);
			Schule schule = Schule.fromJSON(schoolId, json);
			
			Vertretungsplan v = BaseParser.getInstance(schule).getVertretungsplan();
			
			List<BaseAdditionalInfoParser> additionalInfoParsers = new ArrayList<BaseAdditionalInfoParser>();
			for(String type:schule.getAdditionalInfos()) {
				additionalInfoParsers.add(BaseAdditionalInfoParser.getInstance(type));
			}
			
			for(BaseAdditionalInfoParser additionalInfoParser:additionalInfoParsers) {
				v.getAdditionalInfos().add(additionalInfoParser.getAdditionalInfo());
			}
			
			VertretungsplanSerialized vs = new VertretungsplanSerialized(v);
			vs.setSchoolId(schoolId);
			
			Vertretungsplan vAlt = new VertretungsplanSerializedEndpoint().getVertretungsplanSerialized(schoolId).get();
			
			VertretungsplanSerializedEndpoint vse = new VertretungsplanSerializedEndpoint();
			if(vAlt != null)
				vse.removeVertretungsplanSerialized(schoolId);
			
			vse.insertVertretungsplanSerialized(vs);
			
			if(vAlt != null)
				new MessageEndpoint().sendVertretungsplanMessage(schoolId, 
					changedClasses(vAlt, v, BaseParser.getInstance(schule).getAllClasses()));
			
		} catch (JSONException e) {
			e.printStackTrace();
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
	
	private List<String> changedClasses(Vertretungsplan vAlt, Vertretungsplan v, List<String> klassen) {
		List<String> changedClasses = new ArrayList<String>();
		for(String klasse:klassen) {
			if(somethingChanged(vAlt, v, klasse))
				changedClasses.add(klasse);
		}
		return changedClasses;
	}
	
	private boolean somethingChanged(Vertretungsplan vAlt, Vertretungsplan v,
			String klasse) {
		
		for(AdditionalInfo info:v.getAdditionalInfos()) {
			if(info.hasInformation()) {
				//passende alte Info finden
				AdditionalInfo oldInfo = null;
				for(AdditionalInfo infoAlt:vAlt.getAdditionalInfos()) {
					if(infoAlt.getText().equals(info.getText())) {
						oldInfo = infoAlt;
						break;
					}
				}
				if(oldInfo == null) {
					//es wurde keine passende alte Info gefunden
					return true;
				}
			}
		}
		
		for(VertretungsplanTag tag:v.getTage()) {
			//passenden alten Tag finden
			VertretungsplanTag oldTag = null;
			for(VertretungsplanTag tagAlt:vAlt.getTage()) {
				if(tagAlt.getDatum().equals(tag.getDatum())) {
					oldTag = tagAlt;
					break;
				}
			}
			
			if(tag.getKlassen().get(klasse) != null
					&& tag.getKlassen().get(klasse).getVertretung().size() > 0) {
				//Auf dem neuen Plan gibt es Vertretungen, die die gewählte Klasse betreffen
				if(oldTag == null) {
					//dieser Tag wurde neu hinzugefügt -> Vertretungen waren vorher nicht bekannt
					return true;
				} else {
					//dieser Tag war vorher schon auf dem Vertretungsplan
					//Stand prüfen					
					if(!oldTag.getStand().equals(tag.getStand())) {	
						//Stand hat sich verändert
						if(oldTag.getKlassen().get(klasse) != null
								&& oldTag.getKlassen().get(klasse).getVertretung().size() > 0) {
							//auch vorher waren schon Vertretungen für die Klasse bekannt
							//-> vergleiche alte mit neuen Vertretungen
							if(!oldTag.getKlassen().get(klasse).getVertretung().equals(
									tag.getKlassen().get(klasse).getVertretung())) {
								//Die Vertretungen sind nicht gleich
								return true;
							} else {
								//keine Veränderung
							}
						} else {
							//vorher waren keine Vertretungen für die gewählte Klasse bekannt -> es wurde etwas verändert
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
