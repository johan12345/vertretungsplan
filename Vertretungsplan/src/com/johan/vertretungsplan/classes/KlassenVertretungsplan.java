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

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class KlassenVertretungsplan implements Parcelable {
	private ArrayList<Vertretung> vertretungHeute;
	private ArrayList<Vertretung> vertretungMorgen;
	private String klasse;
	
	public KlassenVertretungsplan(String klasse) {
		vertretungHeute = new ArrayList<Vertretung>();
		vertretungMorgen = new ArrayList<Vertretung>();
		this.klasse = klasse;
	}
	
	public ArrayList<Vertretung> getVertretungHeute() {
		return vertretungHeute;
	}
	public void setVertretungHeute(ArrayList<Vertretung> vertretungHeute) {
		this.vertretungHeute = vertretungHeute;
	}
	public ArrayList<Vertretung> getVertretungMorgen() {
		return vertretungMorgen;
	}
	public void setVertretungMorgen(ArrayList<Vertretung> vertretungMorgen) {
		this.vertretungMorgen = vertretungMorgen;
	}
	public String getKlasse() {
		return klasse;
	}
	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}
	
	public void addHeute(Vertretung vertretung) {
		vertretungHeute.add(vertretung);
	}
	
	public void addMorgen(Vertretung vertretung) {
		vertretungMorgen.add(vertretung);
	}

    protected KlassenVertretungsplan(Parcel in) {
        if (in.readByte() == 0x01) {
            vertretungHeute = new ArrayList<Vertretung>();
            in.readList(vertretungHeute, Vertretung.class.getClassLoader());
        } else {
            vertretungHeute = null;
        }
        if (in.readByte() == 0x01) {
            vertretungMorgen = new ArrayList<Vertretung>();
            in.readList(vertretungMorgen, Vertretung.class.getClassLoader());
        } else {
            vertretungMorgen = null;
        }
        klasse = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (vertretungHeute == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(vertretungHeute);
        }
        if (vertretungMorgen == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(vertretungMorgen);
        }
        dest.writeString(klasse);
    }

	@SuppressWarnings("unused")
    public static final Parcelable.Creator<KlassenVertretungsplan> CREATOR = new Parcelable.Creator<KlassenVertretungsplan>() {
        @Override
        public KlassenVertretungsplan createFromParcel(Parcel in) {
            return new KlassenVertretungsplan(in);
        }

        @Override
        public KlassenVertretungsplan[] newArray(int size) {
            return new KlassenVertretungsplan[size];
        }
    };

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((klasse == null) ? 0 : klasse.hashCode());
		result = prime * result
				+ ((vertretungHeute == null) ? 0 : vertretungHeute.hashCode());
		result = prime
				* result
				+ ((vertretungMorgen == null) ? 0 : vertretungMorgen.hashCode());
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
		KlassenVertretungsplan other = (KlassenVertretungsplan) obj;
		if (klasse == null) {
			if (other.getKlasse() != null)
				return false;
		} else if (!klasse.equals(other.klasse))
			return false;
		if (vertretungHeute == null) {
			if (other.getVertretungHeute() != null)
				return false;
		} else if (!vertretungHeute.equals(other.getVertretungHeute()))
			return false;
		if (vertretungMorgen == null) {
			if (other.getVertretungMorgen() != null)
				return false;
		} else if (!vertretungMorgen.equals(other.getVertretungMorgen()))
			return false;
		return true;
	}
}