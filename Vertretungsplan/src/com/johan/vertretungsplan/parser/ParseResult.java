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
