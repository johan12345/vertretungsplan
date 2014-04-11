package com.johan.vertretungsplan_2;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.app.Application;

@ReportsCrashes(formKey = "", mailTo = "johan.forstner+app@gmail.com", 
				mode = org.acra.ReportingInteractionMode.DIALOG,
				resDialogIcon = R.drawable.ic_launcher,
		       resDialogTitle = R.string.crash_dialog_title,
				resDialogText = R.string.crash_dialog_text,
				resDialogCommentPrompt = R.string.crash_dialog_comment_prompt)
public class VertretungsplanApplication extends Application {
	 @Override
     public void onCreate() {
         super.onCreate();

         // The following line triggers the initialization of ACRA
         ACRA.init(this);
     }
}
