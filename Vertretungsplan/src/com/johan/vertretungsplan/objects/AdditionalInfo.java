package com.johan.vertretungsplan.objects;

import java.io.Serializable;

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
