package com.johan.vertretungsplan.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Schule {
	private String id;
	private String name;
	private String city;
	private String api;
	private JSONObject data;
	
	
	public static Schule fromJSON(String id, JSONObject json) throws JSONException {
		Schule schule = new Schule();
		schule.setId(id);
		schule.setCity(json.getString("city"));
		schule.setName(json.getString("name"));
		schule.setApi(json.getString("api"));
		schule.setData(json.getJSONObject("data"));
		return schule;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the api
	 */
	public String getApi() {
		return api;
	}
	/**
	 * @param api the api to set
	 */
	public void setApi(String api) {
		this.api = api;
	}
	/**
	 * @return the data
	 */
	public JSONObject getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(JSONObject data) {
		this.data = data;
	}
}
