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

import javax.persistence.Embeddable;

/**
 * Eine zusätzliche Information zum Vertretungsplan,
 * z.B. witterungsbedingter Schulausfall im Winter.
 * Wird von einem {@link BaseAdditionalInfoParser} abgerufen.
 */
public class AdditionalInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5761074578946441352L;
	private String title;
	private String text;
	private boolean hasInformation = true;
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the hasInformation
	 */
	public boolean hasInformation() {
		return hasInformation;
	}
	/**
	 * @param hasInformation the hasInformation to set
	 */
	public void setHasInformation(boolean hasInformation) {
		this.hasInformation = hasInformation;
	}
}
