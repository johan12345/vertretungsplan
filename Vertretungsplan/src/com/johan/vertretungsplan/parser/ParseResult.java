/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
    Copyright (C) 2013  Johan v. Forstner

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

package com.johan.vertretungsplan.parser;

import java.util.ArrayList;

import android.text.Spanned;

import com.johan.vertretungsplan.classes.KlassenVertretungsplan;

public class ParseResult {
	private ArrayList<KlassenVertretungsplan> vertretung;
	private ArrayList<Spanned> nachrichten;
	private String stand;
	private String date;
	
	public ParseResult(ArrayList<KlassenVertretungsplan> vertretung, ArrayList<Spanned> nachrichten, String stand, String date) {
		this.stand = stand;
		this.date = date;
		this.vertretung = vertretung;
		this.nachrichten = nachrichten;
	}
	
	public ArrayList<KlassenVertretungsplan> getVertretung() {
		return vertretung;
	}
	public void setVertretung(ArrayList<KlassenVertretungsplan> vertretung) {
		this.vertretung = vertretung;
	}
	public String getStand() {
		return stand;
	}
	public void setStand(String stand) {
		this.stand = stand;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<Spanned> getNachrichten() {
		return nachrichten;
	}

	public void setNachrichten(ArrayList<Spanned> nachrichten) {
		this.nachrichten = nachrichten;
	}
}
