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

package com.johan.vertretungsplan.background;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.utils.Utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutostartService extends IntentService {

	public AutostartService() {
		super("AutostartService");
	}

	@Override
	public void onHandleIntent(Intent intent) {		
		//Toast.makeText(this, "AutostartService started", Toast.LENGTH_LONG).show();
		setAlarms();
	}
	
	public void setAlarms(){
		
		Log.d("Vertretungsplan", "Alarme werden gesetzt");
	   	SharedPreferences settings  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	   	 
	     
	   	Intent syncIntent = new Intent(this, VertretungsplanService.class);
	 	
	   	syncIntent.putExtra("AutoSync", true);
	   	
	   	PendingIntent pintent = PendingIntent.getService(this, 0, syncIntent, 0);
		
	   	AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			
	   	//Alte Alarme abbrechen
	   	alarm.cancel(pintent);	
	   	
	   	Schule schule = null;
		try {
			schule = Utils.getSelectedSchool(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	   	
	   	if(settings.getBoolean("sync", true) == true && schule != null && !schule.usesPush()) {
        	Calendar cal = Calendar.getInstance();
        	
        	int g;
        	AlarmManager[] alarmCancel=new AlarmManager[24];
        	for(g=0;g<12;g++){
     		   Intent intentSchlau = new Intent(AutostartService.this, VertretungsplanService.class);
     		   PendingIntent pi=PendingIntent.getBroadcast(AutostartService.this, g,intentSchlau, 0);

     		   alarmCancel[g] = (AlarmManager) getSystemService(ALARM_SERVICE);
     		   alarmCancel[g].cancel(pi);

     		}
        
        	if (settings.getString("syncPeriod", "30").equals("30s")) {
        		Log.d("Vertretungsplan", "30s Alarm");
        		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 30*1000, 30*1000, pintent); 
        	} else if (settings.getString("syncPeriod", "30").equals("15")) {
        		Log.d("Vertretungsplan", "15min Alarm");
        		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 15*60*1000, 15*60*1000, pintent); 
        	} else if (settings.getString("syncPeriod", "30").equals("30")) {
        		Log.d("Vertretungsplan", "30min Alarm");
        		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 30*60*1000, 30*60*1000, pintent); 
        	} else if (settings.getString("syncPeriod", "30").equals("60")) {
        		Log.d("Vertretungsplan", "1h Alarm");
        		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 60*60*1000, 60*60*1000, pintent); 
        	} else if (settings.getString("syncPeriod", "30").equals("120")) {
        		Log.d("Vertretungsplan", "2h Alarm");
        		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 120*60*1000, 120*60*1000, pintent); 
        	} else {
        		Log.d("Vertretungsplan", "Unbekannt: " + settings.getString("syncPeriod", "30"));
        	}
        }
   }
}
