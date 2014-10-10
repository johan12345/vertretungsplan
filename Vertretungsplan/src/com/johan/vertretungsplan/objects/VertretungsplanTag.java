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

package com.johan.vertretungsplan.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Ein Tag eines Vertretungsplans. Er enthält eine Liste
 * der Klassen mit den entsprechenden Vertretungen und
 * das dazugehörige Datum und Aktualisierungsdatum (Stand).
 */
public class VertretungsplanTag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1883217951533073077L;
	private String datum;
	private String stand;
	private LinkedHashMap<String, KlassenVertretungsplan> klassen = new LinkedHashMap<String, KlassenVertretungsplan>();
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
	public LinkedHashMap<String, KlassenVertretungsplan> getKlassen() {
		return klassen;
	}
	/**
	 * @param klassen the klassen to set
	 */
	public void setKlassen(LinkedHashMap<String, KlassenVertretungsplan> klassen) {
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
