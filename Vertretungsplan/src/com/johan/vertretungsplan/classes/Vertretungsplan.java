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
import android.text.Html;
import android.text.Spanned;

public class Vertretungsplan implements Parcelable {
	private ArrayList<KlassenVertretungsplan> klassen;
	private ArrayList<String> nachrichtenHeute;
	private ArrayList<String> nachrichtenMorgen;
	private String stand;
	private String dateHeute;
	private String dateMorgen;
	private WinterAusfall winterAusfall;
	
	public String getStand() {
		return stand;
	}
	public void setStand(String stand) {
		this.stand = stand;
	}
	public ArrayList<KlassenVertretungsplan> getKlassen() {
		return klassen;
	}
	public void setKlassen(ArrayList<KlassenVertretungsplan> klassen) {
		this.klassen = klassen;
	}
	
	public void add(KlassenVertretungsplan kv) {
		klassen.add(kv);
	}
	
	public KlassenVertretungsplan get(String klasse) {
		KlassenVertretungsplan found = null;
		for (KlassenVertretungsplan kv : klassen) {
			if (kv.getKlasse().equals(klasse)) {
				found = kv;
				break;
			}
		}
		return found;
	}
	

    protected Vertretungsplan(Parcel in) {
        if (in.readByte() == 0x01) {
            klassen = new ArrayList<KlassenVertretungsplan>();
            in.readList(klassen, KlassenVertretungsplan.class.getClassLoader());
        } else {
            klassen = null;
        }
        if (in.readByte() == 0x01) {
            nachrichtenHeute = new ArrayList<String>();
            in.readList(nachrichtenHeute, Spanned.class.getClassLoader());
        } else {
            nachrichtenHeute = null;
        }
        if (in.readByte() == 0x01) {
            nachrichtenMorgen = new ArrayList<String>();
            in.readList(nachrichtenMorgen, Spanned.class.getClassLoader());
        } else {
            nachrichtenMorgen = null;
        }
        winterAusfall = in.readParcelable(WinterAusfall.class.getClassLoader());
        stand = in.readString();
        dateHeute = in.readString();
        dateMorgen = in.readString();
    }

    public Vertretungsplan(ArrayList<KlassenVertretungsplan> klassen, ArrayList<Spanned> nachrichtenHeute, ArrayList<Spanned> nachrichtenMorgen, String stand, String dateHeute, String dateMorgen, WinterAusfall winterAusfall) {
		this.klassen = klassen;
		this.stand = stand;
		this.dateHeute = dateHeute;
		this.dateMorgen = dateMorgen;
		this.nachrichtenHeute = new ArrayList<String>();
		for (Spanned s:nachrichtenHeute) {
			this.nachrichtenHeute.add(Html.toHtml(s));
		}
		this.nachrichtenMorgen = new ArrayList<String>();
		for (Spanned s:nachrichtenMorgen) {
			this.nachrichtenMorgen.add(Html.toHtml(s));
		}
		this.winterAusfall = winterAusfall;
	}
	@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (klassen == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(klassen);
        }
        if (nachrichtenHeute == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(nachrichtenHeute);
        }
        if (nachrichtenMorgen == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(nachrichtenMorgen);
        }
        dest.writeParcelable(winterAusfall, 0);
        dest.writeString(stand);
        dest.writeString(dateHeute);
        dest.writeString(dateMorgen);
    }

	public String getDateHeute() {
		return dateHeute;
	}
	public void setDateHeute(String dateHeute) {
		this.dateHeute = dateHeute;
	}

	public String getDateMorgen() {
		return dateMorgen;
	}
	public void setDateMorgen(String dateMorgen) {
		this.dateMorgen = dateMorgen;
	}

	public ArrayList<Spanned> getNachrichtenHeute() {
		ArrayList<Spanned> nachrichtenHeuteSpanned = new ArrayList<Spanned>();
		for(String s:nachrichtenHeute) {
			nachrichtenHeuteSpanned.add(noTrailingwhiteLines(Html.fromHtml(s)));
		}
		return nachrichtenHeuteSpanned;
	}
	public void setNachrichtenHeute(ArrayList<Spanned> nachrichtenHeute) {
		this.nachrichtenHeute = new ArrayList<String>();
		for (Spanned s:nachrichtenHeute) {
			this.nachrichtenHeute.add(Html.toHtml(s));
		}
	}

	public ArrayList<Spanned> getNachrichtenMorgen() {
		ArrayList<Spanned> nachrichtenMorgenSpanned = new ArrayList<Spanned>();
		for(String s:nachrichtenMorgen) {
			nachrichtenMorgenSpanned.add(noTrailingwhiteLines(Html.fromHtml(s)));
		}
		return nachrichtenMorgenSpanned;
	}
	public void setNachrichtenMorgen(ArrayList<Spanned> nachrichtenMorgen) {
		this.nachrichtenMorgen = new ArrayList<String>();
		for (Spanned s:nachrichtenMorgen) {
			this.nachrichtenMorgen.add(Html.toHtml(s));
		}
	}

	public WinterAusfall getWinterAusfall() {
		return winterAusfall;
	}
	public void setWinterAusfall(WinterAusfall winterAusfall) {
		this.winterAusfall = winterAusfall;
	}

	@SuppressWarnings("unused")
    public static final Parcelable.Creator<Vertretungsplan> CREATOR = new Parcelable.Creator<Vertretungsplan>() {
        @Override
        public Vertretungsplan createFromParcel(Parcel in) {
            return new Vertretungsplan(in);
        }

        @Override
        public Vertretungsplan[] newArray(int size) {
            return new Vertretungsplan[size];
        }
    };
    
    private Spanned noTrailingwhiteLines(Spanned text) {

        while (text.charAt(text.length() - 1) == '\n') {
            text = (Spanned) text.subSequence(0, text.length() - 1);
        }
        return text;
    }
}