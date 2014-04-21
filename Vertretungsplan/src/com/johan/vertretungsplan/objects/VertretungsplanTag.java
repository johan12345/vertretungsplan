package com.johan.vertretungsplan.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class VertretungsplanTag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1883217951533073077L;
	private String datum;
	private String stand;
	private HashMap<String, KlassenVertretungsplan> klassen = new HashMap<String, KlassenVertretungsplan>();
	private List<String> nachrichten = new ArrayList<String>();
	
	/**
	 * @return the datum
	 */
	public String getDatum() {
		return datum;
	}
	/**
	 * @param datum the datum to set
	 */
	public void setDatum(String datum) {
		this.datum = datum;
	}
	/**
	 * @return the stand
	 */
	public String getStand() {
		return stand;
	}
	/**
	 * @param stand the stand to set
	 */
	public void setStand(String stand) {
		this.stand = stand;
	}
	/**
	 * @return the klassen
	 */
	public HashMap<String, KlassenVertretungsplan> getKlassen() {
		return klassen;
	}
	/**
	 * @param klassen the klassen to set
	 */
	public void setKlassen(HashMap<String, KlassenVertretungsplan> klassen) {
		this.klassen = klassen;
	}
	/**
	 * @return the nachrichten
	 */
	public List<String> getNachrichten() {
		return nachrichten;
	}
	/**
	 * @param nachrichten the nachrichten to set
	 */
	public void setNachrichten(List<String> nachrichten) {
		this.nachrichten = nachrichten;
	}
	public void merge(VertretungsplanTag tag) {
		for(Entry<String, KlassenVertretungsplan> entry:tag.getKlassen().entrySet()) {
			if(!klassen.containsKey(entry.getKey())) {
				klassen.put(entry.getKey(), entry.getValue());
			} else {
				for(Vertretung v:entry.getValue().getVertretung()) {
					klassen.get(entry.getKey()).add(v);
				}
			}
		}
	}

}
