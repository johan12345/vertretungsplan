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

public class KlassenVertretungsplan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5487489708857673784L;
	private ArrayList<Vertretung> vertretung;
	private String klasse;
	
	public KlassenVertretungsplan(String klasse) {
		this.vertretung = new ArrayList<Vertretung>();
		this.klasse = klasse;
	}
	
	public ArrayList<Vertretung> getVertretung() {
		return vertretung;
	}
	public void setVertretung(ArrayList<Vertretung> vertretung) {
		this.vertretung = vertretung;
	}
	
	public String getKlasse() {
		return klasse;
	}
	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}	
	public void add(Vertretung vertretung) {
		this.vertretung.add(vertretung);
	}
}