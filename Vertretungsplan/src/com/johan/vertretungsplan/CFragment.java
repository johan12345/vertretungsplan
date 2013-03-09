package com.johan.vertretungsplan;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;


public class CFragment extends SherlockFragment {
	
	ToggleButton bg;
	ImageView image;
	
    public static Context appContext;
	
	public static final String EXTRA_TITLE = "Vertretungsplan";
	public static final String PREFS_NAME = "VertretungsplanLS";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {       
		View view = inflater.inflate(R.layout.cfragment, container, false);

		appContext = getActivity().getApplicationContext();
        
		final StartActivity start = (StartActivity) getActivity();
		
		// Restore preferences
        SharedPreferences settings = getSherlockActivity().getSharedPreferences(PREFS_NAME, 0);
        Boolean hintergrund = settings.getBoolean("bg", true);
        bg = (ToggleButton) view.findViewById(R.id.toggleButton1);
        bg.setChecked(hintergrund);
		
		// Inflate the layout for this fragment
        return view;
    }
    
    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }
    
    
    
}


   

