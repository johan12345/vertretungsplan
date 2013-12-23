package com.johan.vertretungsplan.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class WinterAusfall implements Parcelable {
	private String stand;
	private String message;
	
	public WinterAusfall(String stand, String message) {
		this.stand = stand;
		this.message = message;
	}
	
	public String getStand() {
		return stand;
	}
	public void setStand(String stand) {
		this.stand = stand;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

    protected WinterAusfall(Parcel in) {
        stand = in.readString();
        message = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stand);
        dest.writeString(message);
    }

    public static final Parcelable.Creator<WinterAusfall> CREATOR = new Parcelable.Creator<WinterAusfall>() {
        @Override
        public WinterAusfall createFromParcel(Parcel in) {
            return new WinterAusfall(in);
        }

        @Override
        public WinterAusfall[] newArray(int size) {
            return new WinterAusfall[size];
        }
    };
}