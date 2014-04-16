/*  LS Vertretungsplan - Android-App f�r den Vertretungsplan der Lornsenschule Schleswig
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

package com.johan.vertretungsplan.classes;

import java.io.Serializable;
import java.util.List;


public class Vertretungsplan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1753326921591151152L;
	private List<VertretungsplanTag> tage;

	/**
	 * @return the tage
	 */
	public List<VertretungsplanTag> getTage() {
		return tage;
	}

	/**
	 * @param tage the tage to set
	 */
	public void setTage(List<VertretungsplanTag> tage) {
		this.tage = tage;
	}

}