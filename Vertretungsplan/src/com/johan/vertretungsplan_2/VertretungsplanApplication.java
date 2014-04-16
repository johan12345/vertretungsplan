package com.johan.vertretungsplan_2;

import java.io.IOException;
import java.util.List;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.app.Application;

import android.content.Intent;

import com.johan.vertretungsplan.classes.Schule;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.parser.UntisInfoParser;
import com.johan.vertretungsplan.utils.Utils;

@ReportsCrashes(formKey = "", mailTo = "johan.forstner+app@gmail.com", 
mode = org.acra.ReportingInteractionMode.DIALOG,
resDialogIcon = R.drawable.ic_launcher,
resDialogTitle = R.string.crash_dialog_title,
resDialogText = R.string.crash_dialog_text,
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt)
public class VertretungsplanApplication extends Application {

	private BaseParser parser;

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
				parser = BaseParser.getInstance(Utils.getSelectedSchool(this));
				if(parser == null) {
					Intent intent = new Intent(this, SelectSchoolActivity.class);
					startActivity(intent);
					return BaseParser.getInstance(Utils.getSchools(this).get(0));
				} else {
					return parser;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	public void notifySchoolChanged() {
		try {
			parser = BaseParser.getInstance(Utils.getSelectedSchool(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
