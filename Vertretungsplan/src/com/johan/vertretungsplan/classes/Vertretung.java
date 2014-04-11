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

public class Vertretung implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8029479872726949613L;
	private String lesson;
	private String type;
	private String subject;
	private String previousSubject;
	private String teacher;
	private String previousTeacher;
	private String room;
	private String previousRoom;
	private String desc;
	
	public String getColor() {
		String farbe;
		if (type.equals("Entfall")) {
			farbe = "#40FF4444"; //Rot
		} else if (type.equals("EVA")) {
			farbe = "#40FF4444"; //Rot
		} else if (type.equals("Vertretung")) {
			farbe = "#4033B5E5"; //Blau
		} else if (type.equals("Sondereins.")) {
			farbe = "#4033B5E5"; //Blau
		} else if (type.equals("Verlegung")) {
			farbe = "#40FFBB33"; //Gelb
		} else if (type.equals("Tausch")) {
			farbe = "#40FFBB33"; //Gelb
		} else if (type.equals("Statt-Vertretung")) {
			farbe = "#4033B5E5"; //Blau
		} else if (type.equals("Zusammenlegung")) {
			farbe = "#40AA66CC"; //Lila
		} else  {
			farbe = "#40AA66CC"; //Lila
		}
		return farbe;
	}
	
	public String toString() {
		String string;
		if(type.equals("anderer Raum")) {
			string = subject + " in " + room;
		} else if (type.equals("Entfall")) {
			string = subject;
		} else if (type.equals("EVA")) {
			string = subject;
		} else if (type.equals("Vertretung")) {
			string = subject + " statt " + previousSubject + " in " + room;
		} else if (type.equals("Sondereins.")) {
			string = subject + " in " + room;
		} else if (type.equals("Verlegung")) {
			string = subject + " statt " + previousSubject + " in " + room;	
		} else if (type.equals("Tausch")) {
			string = subject + " statt " + previousSubject + " in " + room;
		} else if (type.equals("Statt-Vertretung")) {
			string = subject + " statt " + previousSubject + " in " + room;
		} else if (type.equals("Zusammenlegung")) {
			string = subject + " in " + room;
		} else  {
			string = subject + " statt " + previousSubject + " in " + room;
		}
		
		if (!desc.equals("\u00a0")) {
			string = string + " - " + desc;
		}
		return string;
	}

	/**
	 * @return the lesson
	 */
	public String getLesson() {
		return lesson;
	}

	/**
	 * @param lesson the lesson to set
	 */
	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the previousSubject
	 */
	public String getPreviousSubject() {
		return previousSubject;
	}

	/**
	 * @param previousSubject the previousSubject to set
	 */
	public void setPreviousSubject(String previousSubject) {
		this.previousSubject = previousSubject;
	}

	/**
	 * @return the teacher
	 */
	public String getTeacher() {
		return teacher;
	}

	/**
	 * @param teacher the teacher to set
	 */
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	/**
	 * @return the previousTeacher
	 */
	public String getPreviousTeacher() {
		return previousTeacher;
	}

	/**
	 * @param previousTeacher the previousTeacher to set
	 */
	public void setPreviousTeacher(String previousTeacher) {
		this.previousTeacher = previousTeacher;
	}

	/**
	 * @return the room
	 */
	public String getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(String room) {
		this.room = room;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the previousRoom
	 */
	public String getPreviousRoom() {
		return previousRoom;
	}

	/**
	 * @param previousRoom the previousRoom to set
	 */
	public void setPreviousRoom(String previousRoom) {
		this.previousRoom = previousRoom;
	}
		
}