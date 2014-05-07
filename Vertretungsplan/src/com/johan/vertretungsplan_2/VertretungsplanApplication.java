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
package com.johan.vertretungsplan_2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.app.Application;

import android.content.Intent;

import com.johan.vertretungsplan.additionalinfo.BaseAdditionalInfoParser;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.utils.Utils;

@ReportsCrashes(formKey = "", mailTo = "johan.forstner+app@gmail.com", 
mode = org.acra.ReportingInteractionMode.DIALOG,
resDialogIcon = R.drawable.ic_launcher,
resDialogTitle = R.string.crash_dialog_title,
resDialogText = R.string.crash_dialog_text,
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt)
public class VertretungsplanApplication extends Application {

	private BaseParser parser;
	private List<BaseAdditionalInfoParser> additionalInfoParsers;

	@Override
	public void onCreate() {
		super.onCreate();

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
	}

	public BaseParser getParser() {
		if(parser != null)
			return parser;
		else {
			try {
				notifySchoolChanged();
				if(parser == null) {
					List<Schule> schools = Utils.getSchools(this);
					if(schools.size() > 1)
						startSelectSchoolActivity();
					parser = BaseParser.getInstance(schools.get(0));
					return parser;
				} else {
					return parser;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public List<BaseAdditionalInfoParser> getAdditionalInfoParsers() {
		if(additionalInfoParsers != null) {
			return additionalInfoParsers;
		} else {
			try {
				notifySchoolChanged();
				if(additionalInfoParsers == null) {
					List<Schule> schools = Utils.getSchools(this);
					if(schools.size() > 1)
						startSelectSchoolActivity();
					additionalInfoParsers = new ArrayList<BaseAdditionalInfoParser>();
					for (String infoType:schools.get(0).getAdditionalInfos()) {
						additionalInfoParsers.add(BaseAdditionalInfoParser.getInstance(infoType));
					}
					return additionalInfoParsers;
				} else {
					return additionalInfoParsers;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public void notifySchoolChanged() {
		try {
			Schule schule = Utils.getSelectedSchool(this);
			if(schule != null) {
				parser = BaseParser.getInstance(schule);
				additionalInfoParsers = new ArrayList<BaseAdditionalInfoParser>();
				for (String infoType:schule.getAdditionalInfos()) {
					additionalInfoParsers.add(BaseAdditionalInfoParser.getInstance(infoType));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startSelectSchoolActivity() {
		Intent intent = new Intent(this, SelectSchoolActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
