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

package com.johan.vertretungsplan.parser;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;

/**
 * Ein Parser für einen Vertretungsplan.
 * Er erhält Informationen aus der JSON-Datei für eine Schule und liefert
 * den abgerufenen und geparsten Vertretungsplan zurück.
 */
public abstract class BaseParser {
	/**
	 * Die Schule, deren Vertretungsplan geparst werden soll
	 */
	protected Schule schule;

	public BaseParser(Schule schule) {
		this.schule = schule;
	}
	
	/**
	 * Ruft den Vertretungsplan ab und parst ihn.
	 * Wird immer asynchron ausgeführt.
	 * @return Der geparste {@link Vertretungsplan}
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract Vertretungsplan getVertretungsplan() throws IOException, JSONException;
	
	/**
	 * Gibt eine Liste aller verfügbaren Klassen zurück.
	 * Wird immer asynchron ausgeführt.
	 * @return Eine Liste aller verfügbaren Klassen für diese Schule
	 *  (auch die, die nicht aktuell vom Vertretungsplan betroffen sind)
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract List<String> getAllClasses() throws IOException, JSONException;
	
	protected String httpGet(String url, String encoding) throws IOException {
		Response response = new Request(url).getResource(encoding);
		return response.getBody();
	}

	/**
	 * Erstelle einen neuen Parser für eine Schule.
	 * Liefert automatisch eine passende Unterklasse.
	 * @param schule die Schule, für die ein Parser erstellt werden soll
	 * @return Eine Unterklasse von {@link BaseParser}, die zur übergebenen Schule passt
	 */
	public static BaseParser getInstance(Schule schule) {
		BaseParser parser = null;
		if(schule != null) {
			if (schule.getApi().equals("untis-monitor")) {
				parser = new UntisMonitorParser(schule);
			} else if (schule.getApi().equals("untis-info")) {
				parser = new UntisInfoParser(schule);
			} else if (schule.getApi().equals("backend-connect")) {
				parser = new BackendConnectParser(schule);
			} //else if ... (andere Parser)
		}
		return parser;
	}
}
