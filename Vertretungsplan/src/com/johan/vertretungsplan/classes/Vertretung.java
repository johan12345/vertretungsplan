/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
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

import android.os.Parcel;
import android.os.Parcelable;

public class Vertretung implements Parcelable {
	private String lesson;
	private String type;
	private String subject;
	private String changedSubject;
	private String room;
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
			string = subject + " statt " + changedSubject + " in " + room;
		} else if (type.equals("Sondereins.")) {
			string = subject + " in " + room;
		} else if (type.equals("Verlegung")) {
			string = subject + " statt " + changedSubject + " in " + room;	
		} else if (type.equals("Tausch")) {
			string = subject + " statt " + changedSubject + " in " + room;
		} else if (type.equals("Statt-Vertretung")) {
			string = subject + " statt " + changedSubject + " in " + room;
		} else if (type.equals("Zusammenlegung")) {
			string = subject + " in " + room;
		} else  {
			string = subject + " statt " + changedSubject + " in " + room;
		}
		
		if (!desc.equals("\u00a0")) {
			string = string + " - " + desc;
		}
		return string;
	}
	
	public String getLesson() {
		return lesson;
	}
	public void setLesson(String lesson) {
		this.lesson = lesson;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getChangedSubject() {
		return changedSubject;
	}
	public void setChangedSubject(String changedSubject) {
		this.changedSubject = changedSubject;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

    protected Vertretung(Parcel in) {
        lesson = in.readString();
        type = in.readString();
        subject = in.readString();
        changedSubject = in.readString();
        room = in.readString();
        desc = in.readString();
    }

    public Vertretung() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lesson);
        dest.writeString(type);
        dest.writeString(subject);
        dest.writeString(changedSubject);
        dest.writeString(room);
        dest.writeString(desc);
    }

    public static final Parcelable.Creator<Vertretung> CREATOR = new Parcelable.Creator<Vertretung>() {
        @Override
        public Vertretung createFromParcel(Parcel in) {
            return new Vertretung(in);
        }

        @Override
        public Vertretung[] newArray(int size) {
            return new Vertretung[size];
        }
    };

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((changedSubject == null) ? 0 : changedSubject.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((lesson == null) ? 0 : lesson.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertretung other = (Vertretung) obj;
		if (changedSubject == null) {
			if (other.changedSubject != null)
				return false;
		} else if (!changedSubject.equals(other.changedSubject))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (lesson == null) {
			if (other.lesson != null)
				return false;
		} else if (!lesson.equals(other.lesson))
			return false;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}