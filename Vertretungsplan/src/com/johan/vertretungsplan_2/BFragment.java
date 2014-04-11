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

import java.util.ArrayList;
import java.util.TreeSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import org.holoeverywhere.LayoutInflater;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import com.johan.vertretungsplan.classes.Vertretungsplan;
import com.johan.vertretungsplan.classes.VertretungsplanTag;
import com.johan.vertretungsplan.utils.Animations;
import com.johan.vertretungsplan_2.R;
import com.johan.vertretungsplan_2.AFragment.MyCustomAdapter;


public class BFragment extends VertretungsplanFragment {
	
	TextView text;
	ListView list;
	Spinner klassen;
	ImageView image;
	ProgressBar pBar = null;
	Boolean showProgress = true;
	boolean ready = false;
	Vertretungsplan v = null;
	
    public static Context appContext;
    public static StartActivity startActivity;
    MyCustomAdapter listadapter = null;
	
	public static final String EXTRA_TITLE = "Vertretungsplan";
	public static final String PREFS_NAME = "VertretungsplanLS";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {       
		View view = inflater.inflate(R.layout.bfragment, container, false);

		appContext = getActivity().getApplicationContext();
		startActivity = (StartActivity) getActivity();
        image = (ImageView) view.findViewById(R.id.imageView2);
        pBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        list = (ListView) view.findViewById(R.id.listView2);
        progress(true);
        
        listadapter = new MyCustomAdapter(startActivity);
		list.setAdapter(listadapter);
        
        ready = true;
//        if(v != null) aktualisieren(v);
		
		// Inflate the layout for this fragment
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	bgAktualisieren();
        progress(showProgress);
        mListener.onFragmentInteraction("ViewCreated", this);
        super.onViewCreated(view, savedInstanceState);
    }
    
    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }
    
    public void aktualisieren(Vertretungsplan v) {
    	this.v = v;
    	if(ready) {
	    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
	    	
	    	listadapter.clear(); 
	    	
	    	for(VertretungsplanTag tag:v.getTage()) {	    	
		    	listadapter.addSeparatorItem(tag.getDatum());
		    	for (String message:tag.getNachrichten()) {
		    		listadapter.addItem(Html.fromHtml(message));
		    	}
	    	}
	    			
//			if(settings.getBoolean("winter", true)) {
//				String text = v.getWinterAusfall().getMessage();
//				String stand = v.getWinterAusfall().getStand();
//		    	listadapter.addSeparatorItem("Winter-Unterrichtsausfall (Stand: " + stand + ")");
//		        listadapter.addItem(text);
//			}
			progress(false);
    	}
}
    
    public void bgAktualisieren() {
        // Restore preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
        Boolean bg = settings.getBoolean("bg", true);
        if (bg == true) {
        	image.setVisibility(View.VISIBLE);
        } else {
        	image.setVisibility(View.INVISIBLE);
        }
    }
    
    public void progress(Boolean show) {
    	showProgress = show;
    	if (pBar != null) {
    		if (show == true) {
	    		Animations.crossfade(list, pBar);
	    	} else {
	    		Animations.crossfade(pBar, list);
	    	}
    	}
    }
    
    public class MyCustomAdapter extends BaseAdapter {
   	 
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
 
        private ArrayList<CharSequence> mData = new ArrayList<CharSequence>();
        private LayoutInflater mInflater;
 
        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
 
        public MyCustomAdapter(Context context) {
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addItem(final CharSequence item) {
            mData.add(item);
            notifyDataSetChanged();
        }
 
        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }
 
        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }
 
        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }
 
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public String getItem(int position) {
            return (String) mData.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:
                    	convertView = mInflater.inflate(R.layout.text, null);
                    	holder.text = (TextView)convertView.findViewById(R.id.text);
                        holder.text.setText((CharSequence) mData.get(position));
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.separator, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                        holder.textView.setText((CharSequence) mData.get(position));
                        break;
                }
            } else {
                holder = (ViewHolder)convertView.getTag();
                
                switch (type) {
                case TYPE_ITEM:
                	convertView = mInflater.inflate(R.layout.text, null);
                	holder.text = (TextView)convertView.findViewById(R.id.text);
                    holder.text.setText((CharSequence) mData.get(position));
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.separator, null);
                    holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                    holder.textView.setText((CharSequence) mData.get(position));
                    break;
            }
            }
            convertView.setTag(holder);
            return convertView;
        }
        
        public void clear() {
        	mData.clear();
        	mSeparatorsSet.clear();
        	notifyDataSetChanged();
        }
 
    }
    
    public static class ViewHolder {
        public TextView text;
		public TextView textView;
    }
    
}


   

