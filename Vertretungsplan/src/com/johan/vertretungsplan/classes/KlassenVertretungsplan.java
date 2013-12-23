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
}