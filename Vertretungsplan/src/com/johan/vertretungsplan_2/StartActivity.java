/*  LS Vertretungsplan - Android-App f�r den Vertretungsplan der Lornsenschule Schleswig
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

import org.acra.ACRA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;
import com.inscription.ChangeLogDialog;
import com.inscription.WhatsNewDialog;
import com.johan.vertretungsplan.background.AutostartService;
import com.johan.vertretungsplan.background.VertretungsplanService;
import com.johan.vertretungsplan.background.VertretungsplanWidgetProvider;
import com.johan.vertretungsplan.classes.Vertretungsplan;
import com.johan.vertretungsplan.ui.LinkAlertDialog;
import com.johan.vertretungsplan.ui.TabSwipeActivity;
import com.johan.vertretungsplan_2.R;
import com.johan.vertretungsplan_2.VertretungsplanFragment.OnFragmentInteractionListener;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class StartActivity extends TabSwipeActivity implements OnFragmentInteractionListener {
	public static Context appContext;
	public static final String PREFS_NAME = "VertretungsplanLS";
	private VertretungFragment fragment;
	StartActivity thisActivity = this;
	private NachrichtenFragment fragment2;
	Vertretungsplan vertretungsplan;
    Boolean asyncRunning = false;
    SharedPreferences settings;
    PagerSlidingTabStrip tabs;
    
    Boolean aFragmentLoaded = false;
    Boolean bFragmentLoaded = false;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.d("Vertretungsplan", "onCreate");
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        
        settings  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        if(!settings.contains("selected_school")) {
        	Intent intent = new Intent(this, SelectSchoolActivity.class);
			startActivity(intent);
			finish();
        }
        
        addTab( "Vertretungsplan", VertretungFragment.class, VertretungFragment.createBundle( "Vertretungsplan") );
        addTab( "Nachrichten", NachrichtenFragment.class, NachrichtenFragment.createBundle( "Nachrichten") ); 
        
        fragment = (VertretungFragment) adapter.getItem(0);
        fragment2 = (NachrichtenFragment) adapter.getItem(1);
        
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (metrics.widthPixels / (metrics.densityDpi / 160f) >= 800 && getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
        	tabs.setVisibility(View.GONE);
        }
        tabs.setOnPageChangeListener( adapter );   
        tabs.setIndicatorColor(Color.rgb(51, 139, 255));
        
        //Launch what's new dialog (will only be shown once)      
        showDialogs();
        
        if (savedInstanceState == null) {
        	verbindungPruefenUndLaden();   
        } else {
        	vertretungsplan = (Vertretungsplan) savedInstanceState.getParcelable("Vertretungsplan");
        }
        
        Log.d("Vertretungsplan", "/onCreate");

    }
    
    @Override
    public void onStart() {
    	Log.d("Vertretungsplan", "onStart");
    	super.onStart();
      	//Google Analytics
    	EasyTracker.getInstance().activityStart(this);
	    Tracker tracker = EasyTracker.getTracker();
	    
	    String klasse = settings.getString("klasse", "unbekannt");
	    Boolean sync = settings.getBoolean("sync", true);
	    String syncPeriod;
	    String benachrichtigung;
	    if (sync == true) {
	    	syncPeriod = settings.getString("syncPeriod", "unbekannt");
	    	benachrichtigung = Boolean.valueOf(settings.getBoolean("notification", true)).toString();
	    } else {
	    	syncPeriod = "Sync ausgeschaltet";
	    	benachrichtigung = "Sync ausgeschaltet";
	    }
	    //Analytics
	    tracker.setCustomDimension(1, klasse);
	    tracker.setCustomDimension(2, syncPeriod);
	    tracker.setCustomDimension(3, benachrichtigung);
	    
      	//Aktualisieren
//	    anzeigenAktualisieren();
	    Log.d("Vertretungsplan", "/onStart");
    }
    
    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance().activityStop(this);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		
		switch (item.getItemId()) {
	    	case R.id.menu_refresh:
	    		if (asyncRunning == false) {	
	    			verbindungPruefenUndLaden();
	    		}
			    	break;
			    	
	    	case R.id.menu_info:
	    		PackageInfo pInfo = null;
	    		try {
	    			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	    		} catch (NameNotFoundException e) {
	    			e.printStackTrace();
	    		}
	    		String version = pInfo.versionName;
	    		
	    		String msg = "Version " + version + "<br />" + getResources().getString(R.string.info_dialog);
	    	    
	    		final Context context = this;
	    		AlertDialog dialog = LinkAlertDialog.create(this, "Info", msg)
	    			.setNegativeButton("Changelog", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	 //Launch change log dialog
				    		ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(context); 
				    		_ChangelogDialog.show();  
			           }
			       })
			       .setPositiveButton("OK", null)
			       .create();		
	    		dialog.show();	
	    		break;
	    		
	    	case R.id.menu_link:
	    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vertretung.lornsenschule.de/schueler/subst_001.htm"));
	    		startActivity(browserIntent);
	    		break;
	    		
	    	case R.id.menu_settings:
	    		Log.d("Vertretungsplan", "settings aufgerufen");
	    		Intent settingsIntent = new Intent(this, SettingsActivity.class);
	    		startActivity(settingsIntent);
	    		break;
	    		
	    	case R.id.menu_mail:
	    		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

	    		emailIntent.setType("plain/text");
	    		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"johan.forstner+app@gmail.com"});
	    		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vertretungsplan App");

	    		startActivity(Intent.createChooser(emailIntent, "E-Mail senden..."));
	    		break;
	    }
		return true;
}
	
	public void verbindungPruefenUndLaden() {
	    if (asyncRunning == false) {
	    	asyncRunning = true;
			setProgress(true);
    		Intent intent = new Intent(this, VertretungsplanService.class);
    	    // Create a new Messenger for the communication back
    	    Messenger messenger = new Messenger(handler);
    	    intent.putExtra("MESSENGER", messenger);
    	    startService(intent);
	    }
    }
	
	  private Handler handler = new Handler() {
		    public void handleMessage(Message message) {
		      if (message.arg1 == RESULT_OK) {
		    	  Log.d("Vertretungsplan", "RESULT OK");
		    	  Gson gson = new Gson();
		     	  setVertretungsplan((Vertretungsplan) message.getData().getSerializable("Vertretungsplan"));
		    	  Log.d("Vertretungsplan", gson.toJson(vertretungsplan));
		  		  asyncRunning = false;
		      } else if (message.arg1 == Activity.RESULT_CANCELED) {
		    	  	asyncRunning = false;
	    	  		Gson gson = new Gson();
	    	  		vertretungsplan = gson.fromJson(settings.getString("Vertretungsplan", ""), Vertretungsplan.class);
	  	        	if (vertretungsplan != null) {	
	  	        		Crouton crouton = Crouton.makeText(thisActivity, "Offline", Style.INFO);
			    		crouton.show();
	  	        		anzeigenAktualisieren();
		  	        } else {
		  	        	Crouton crouton = Crouton.makeText(thisActivity, "keine Internetverbindung", Style.ALERT);
			    		crouton.show();
		  	        }
		      }

		    };
		  };
		  
		    
   public void anzeigenAktualisieren() {
  	   Log.d("Vertretungsplan", "anzeigenAktualisieren"); 
  	   setVertretungsplan(vertretungsplan);
   }
	
	public void loadParsed(Vertretungsplan v) {
		setupVertretungsplanFragment();
		setupNachrichtenFragment();
		if (v != null) {
			vertretungsplan = v;
        	fragment.aktualisieren(v);
        	fragment2.aktualisieren(v);
        } else {
        	Crouton crouton = Crouton.makeText(thisActivity, "Konnte nicht auf den Vertretungsplan zugreifen", Style.ALERT);
		crouton.show();
        }
		setProgress(false);
	}
	
	public void reloadParsed() {
		loadParsed(vertretungsplan);
	}
	
	public void setupVertretungsplanFragment() {
		
		fragment.bgAktualisieren();
		
		fragment.klassen.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            	// We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("klasse", fragment.klassen.getSelectedItem().toString());

                // Commit the edits!
                editor.commit();
            	
                if(vertretungsplan != null) {
	                fragment.aktualisieren(vertretungsplan);
	                widgetAktualisieren();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
	}
	public void setupNachrichtenFragment() {        
		fragment2.bgAktualisieren();
	}
	
	public void setProgress(boolean show) {
		Log.d("Vertretungsplan", "progress: " + show);
		fragment.progress(show);
		fragment2.progress(show);
	}
	
    @Override
	public void onResume(){
        super.onResume();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean("isInForeground",true);
        prefEditor.commit();      
        setAlarms();
   }
    @Override
    public void onPause(){
    		super.onPause();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor prefEditor = prefs.edit();
            prefEditor.putBoolean("isInForeground", false);
            prefEditor.commit();
   }
    
    @Override
    public void onSaveInstanceState(Bundle out) {
    	super.onSaveInstanceState(out);
//    	out.putParcelable("Vertretungsplan", vertretungsplan);
    }
    
    public void setAlarms(){
    	Intent autostartIntent = new Intent(appContext, AutostartService.class);
		appContext.startService(autostartIntent);
    }
    
    public void widgetAktualisieren() {    
  	    //Widget(s) aktualisieren
  	    Intent i = new Intent(this, VertretungsplanWidgetProvider.class); 
          i.setAction(VertretungsplanWidgetProvider.UPDATE_ACTION); 
          sendBroadcast(i); 
      }

	@Override
	public void onFragmentInteraction(String type, Fragment sender) {
		Log.d("Vertretungsplan", "Message");
		if (type.equals("ViewCreated"))	{
			Log.d("Vertretungsplan", "Message: ViewCreated");
			if (sender instanceof VertretungFragment) {
				Log.d("Vertretungsplan", "Message: fragment 1 loaded");
				fragment = (VertretungFragment) sender;
				aFragmentLoaded = true;
			} else if (sender instanceof NachrichtenFragment) {
				Log.d("Vertretungsplan", "Message: fragment 2 loaded");
				fragment2 = (NachrichtenFragment) sender;
				bFragmentLoaded = true;
			}
			if (aFragmentLoaded && bFragmentLoaded && vertretungsplan != null) {
				reloadParsed();
				setProgress(false);
			}
		}
	}

	public void setVertretungsplan(Vertretungsplan v) {
		vertretungsplan = v;
		if (aFragmentLoaded && bFragmentLoaded) {
			reloadParsed();
		}
	}
	
	public void showDialogs() {
		boolean firstRun = settings.getBoolean("firstRun", true);
        final WhatsNewDialog whatsNewDialog = new WhatsNewDialog(this);
        if(firstRun) {
        	String license = getResources().getString(R.string.license_dialog);
        	AlertDialog dialog = LinkAlertDialog.create(this, "Lizenzbedingungen", license).setPositiveButton("Akzeptieren", null).create();
        	dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					settings.edit().putBoolean("firstRun", false).commit();
					whatsNewDialog.show();
				}       		
        	});
        	dialog.show();
        } else {
        	whatsNewDialog.show();
        }
	}
}