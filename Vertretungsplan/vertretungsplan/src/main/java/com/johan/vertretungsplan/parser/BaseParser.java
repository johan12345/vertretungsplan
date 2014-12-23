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

import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Ein Parser für einen Vertretungsplan. Er erhält Informationen aus der
 * JSON-Datei für eine Schule und liefert den abgerufenen und geparsten
 * Vertretungsplan zurück.
 */
public abstract class BaseParser {
    /**
     * Die Schule, deren Vertretungsplan geparst werden soll
     */
    protected Schule schule;

    public BaseParser(Schule schule) {
        this.schule = schule;
    }

    protected static String httpGet(String url, String encoding) throws IOException {
        Response response = new Request(url).getResource(encoding);
        return response.getBody();
    }

    /**
     * Ruft den Vertretungsplan ab und parst ihn. Wird immer asynchron
     * ausgeführt.
     *
     * @return Der geparste {@link Vertretungsplan}
     * @throws IOException
     * @throws JSONException
     * @throws VersionException
     */
    public abstract Vertretungsplan getVertretungsplan() throws IOException,
            JSONException, VersionException, UnauthorizedException;

    /**
     * Gibt eine Liste aller verfügbaren Klassen zurück. Wird immer asynchron
     * ausgeführt.
     *
     * @return Eine Liste aller verfügbaren Klassen für diese Schule (auch die,
     * die nicht aktuell vom Vertretungsplan betroffen sind)
     * @throws IOException
     * @throws JSONException
     */
    public abstract List<String> getAllClasses() throws IOException,
            JSONException;

    @SuppressWarnings("serial")
    public class VersionException extends Exception {

    }

    @SuppressWarnings("serial")
    public class UnauthorizedException extends Exception {

    }
}
