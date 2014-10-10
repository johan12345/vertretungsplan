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
import java.util.List;

/**
 * Ein Vertretungsplan einer Schule für mehrere Tage
 */
public class Vertretungsplan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1753326921591151152L;
	private List<VertretungsplanTag> tage;
	private List<AdditionalInfo> additionalInfos;
	private String schoolName;
	private String city;
	
	public Vertretungsplan() {
		additionalInfos = new ArrayList<AdditionalInfo>();
	}

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

	/**
	 * @return the additionalInfos
	 */
	public List<AdditionalInfo> getAdditionalInfos() {
		return additionalInfos;
	}

	/**
	 * @param additionalInfos the additionalInfos to set
	 */
	public void setAdditionalInfos(List<AdditionalInfo> additionalInfos) {
		this.additionalInfos = additionalInfos;
	}

	/**
	 * @return the schoolName
	 */
	public String getSchoolName() {
		return schoolName;
	}

	/**
	 * @param schoolName the schoolName to set
	 */
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
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

}