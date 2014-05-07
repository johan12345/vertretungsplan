package com.johan.vertretungsplan_2;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.Vertretungsplan;

@Entity
public class VertretungsplanSerialized {
	@Id private String schoolId;
	private Text json;
	
	public VertretungsplanSerialized(Vertretungsplan v) {
		json = new Text(new Gson().toJson(v));
	}
	
	public Vertretungsplan get() {
		return new Gson().fromJson(json.getValue(), Vertretungsplan.class);
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getJson() {
		return json.getValue();
	}

	public void setJson(String json) {
		this.json = new Text(json);
	}
}
