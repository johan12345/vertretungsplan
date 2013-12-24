/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
    Copyright (C) 2013  Johan v. Forstner

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