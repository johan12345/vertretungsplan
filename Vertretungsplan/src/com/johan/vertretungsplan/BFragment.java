package com.johan.vertretungsplan;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class BFragment extends SherlockFragment {
	
	TextView text;
	ListView list;
	Spinner klassen;
	ImageView image;
	
	List<String> valueList = new ArrayList<String>();
    public static Context appContext;
    ListAdapter listadapter;
	
	public static final String EXTRA_TITLE = "Vertretungsplan";
	public static final String PREFS_NAME = "VertretungsplanLS";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {       
		View view = inflater.inflate(R.layout.bfragment, container, false);

		appContext = getActivity().getApplicationContext();
        image = (ImageView) view.findViewById(R.id.imageView2);
        
		final StartActivity start = (StartActivity) getActivity();
		
		// Inflate the layout for this fragment
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	bgAktualisieren();
        super.onViewCreated(view, savedInstanceState);
    }
    
    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }
    
    public void aktualisieren(Document docHeute, Document docMorgen) {
    	valueList.clear();	        
    	listeFuellen(docHeute);
		listeFuellen(docMorgen);
    }
    
    public void bgAktualisieren() {
        // Restore preferences
        SharedPreferences settings = appContext.getSharedPreferences(PREFS_NAME, 0);
        Boolean bg = settings.getBoolean("bg", true);
        if (bg == true) {
        	image.setVisibility(View.VISIBLE);
        } else {
        	image.setVisibility(View.INVISIBLE);
        }
    }
    
    public void listeFuellen(Document doc) {
    	
    	Element datum = doc.select(".mon_title").first();
 		valueList.add(datum.text());
 			
 		Elements zeilen = null;
 		try {
 			zeilen = doc.select("table.info tr");
 		} catch (Throwable e) {
 			
 		}
 		
 		for ( int i = 1; i < zeilen.size(); i ++ ) {
 			
 			String info = zeilen.get(i).text();
 		
 			String message = info;
 		
 			valueList.add(message);
 		}
     }
    
}


   

