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

import java.util.ArrayList;
import java.util.Calendar;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AutostartService extends Service {
	public AutostartService() {
	}

	@Override
	public void onStart(Intent intent, int startid) {		
		//Toast.makeText(this, "AutostartService started", Toast.LENGTH_LONG).show();
		setAlarms();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
	return null;
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
	   	
	   	if(settings.getBoolean("sync", true) == true) {
        	Calendar cal = Calendar.getInstance();
        	
        	int g;
        	AlarmManager[] alarmCancel=new AlarmManager[24];
        	for(g=0;g<12;g++){
     		   Intent intentSchlau = new Intent(AutostartService.this, VertretungsplanService.class);
     		   PendingIntent pi=PendingIntent.getBroadcast(AutostartService.this, g,intentSchlau, 0);

     		   alarmCancel[g] = (AlarmManager) getSystemService(ALARM_SERVICE);
     		   alarmCancel[g].cancel(pi);

     		}
        
        	if(settings.getString("syncPeriod", "30").equals("SCHLAU")) {
        		Log.d("Vertretungsplan", "schlauer Alarm");
        		AlarmManager[] alarmManager=new AlarmManager[24];
        		ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        		
        		ArrayList<Long> zeiten = new ArrayList<Long>();
        		
        		// 7:20
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 7);
        		cal.set(Calendar.MINUTE, 20);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis()); 
        		
        		// 7:25
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 7);
        		cal.set(Calendar.MINUTE, 25);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis()); 
        		
        		// 7:30
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 7);
        		cal.set(Calendar.MINUTE, 30);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 7:35
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 7);
        		cal.set(Calendar.MINUTE, 35);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis()); 
        		
        		// 7:40
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 7);
        		cal.set(Calendar.MINUTE, 40);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 7:45
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 7);
        		cal.set(Calendar.MINUTE, 45);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 9:35
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 9);
        		cal.set(Calendar.MINUTE, 35);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 11:25
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 11);
        		cal.set(Calendar.MINUTE, 25);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 13:05
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 12);
        		cal.set(Calendar.MINUTE, 22);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());   
        		
        		// 14:30
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 14);
        		cal.set(Calendar.MINUTE, 30);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 17:00
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
        		cal.set(Calendar.HOUR_OF_DAY, 17);
        		cal.set(Calendar.MINUTE, 00);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		// 19:00
        		cal=Calendar.getInstance();
        		cal.setTimeInMillis(System.currentTimeMillis());
                cal.clear();
        		cal.set(Calendar.HOUR_OF_DAY, 19);
        		cal.set(Calendar.MINUTE, 00);
        		cal.set(Calendar.SECOND, 0);        		
        		zeiten.add(cal.getTimeInMillis());  
        		
        		
        		int f;
        		for(f=0;f<zeiten.size();f++){
        		   Intent intentSchlau = new Intent(AutostartService.this, VertretungsplanService.class);
        		   PendingIntent pi=PendingIntent.getBroadcast(AutostartService.this, f,intentSchlau, 0);

        		   alarmManager[f] = (AlarmManager) getSystemService(ALARM_SERVICE);
        		   alarmManager[f].setRepeating(AlarmManager.RTC_WAKEUP,zeiten.get(f), AlarmManager.INTERVAL_DAY, pi);

        		   intentArray.add(pi);

        		}
        		
        	} else if (settings.getString("syncPeriod", "30").equals("30s")) {
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
