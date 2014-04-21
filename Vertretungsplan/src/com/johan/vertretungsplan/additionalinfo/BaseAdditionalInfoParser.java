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
package com.johan.vertretungsplan.additionalinfo;

import java.io.IOException;

import org.json.JSONException;

import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.AdditionalInfo;

/**
 * Parser, der {@link AdditionalInfo}s erzeugt.
 */
public abstract class BaseAdditionalInfoParser {
	public BaseAdditionalInfoParser() {
	}
	
	public abstract AdditionalInfo getAdditionalInfo() throws IOException, JSONException;
	
	protected String httpGet(String url, String encoding) throws IOException {
		Response response = new Request(url).getResource(encoding);
		return response.getBody();
	}

	/**
	 * Erstelle einen neuen AdditionalInfoParser für einen bestimmten Typ.
	 * Liefert automatisch eine passende Unterklasse.
	 * @param type die Art der Zusatzinformation (ein Element von <code>Schule.getAdditionalInfos()</code>)
	 * @return Eine Unterklasse von {@link BaseAdditionalInfoParser}, die zum übergebenen Typ passt
	 */
	public static BaseAdditionalInfoParser getInstance(String type) {
		BaseAdditionalInfoParser parser = null;
		if (type.equals("winter-sh")) {
			parser = new WinterShParser();
		} //else if ... (andere Parser)
		return parser;
	}
}
