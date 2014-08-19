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
import java.util.TreeSet;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.johan.vertretungsplan.objects.Vertretung;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;
import com.johan.vertretungsplan.parser.BaseParser;
import com.johan.vertretungsplan.utils.Animations;

public class VertretungFragment extends VertretungsplanFragment {
	
	public interface Callback {
		public Vertretungsplan getVertretungsplan();
		public void onClassSelected();
	}
	
	private ListView list;
	private Spinner klassen;
	private TextView txtStand;
	private ProgressBar pBar = null;
	private boolean showProgress = true;
	boolean ready = false;
	private String klasse;
	private Callback mCallback;
	
	private SharedPreferences settings;
	
    public static Context appContext;
    public static StartActivity startActivity;
    private VertretungAdapter listadapter = null;
    
    private Vertretungsplan v;
	
	public static final String EXTRA_TITLE = "Vertretungsplan";
	public static final String PREFS_NAME = "VertretungsplanLS";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {       
		View view = inflater.inflate(R.layout.fragment_vertretung, container, false);

		appContext = getActivity().getApplicationContext();
		startActivity = (StartActivity) getActivity();
        
		
		// Restore preferences
        settings = PreferenceManager.getDefaultSharedPreferences(appContext);
        list = (ListView) view.findViewById(R.id.listView1);
        klassen = (Spinner) view.findViewById(R.id.spinner1);
        txtStand = (TextView) view.findViewById(R.id.txtStand);
        pBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        
        listadapter = new VertretungAdapter(startActivity);
		list.setAdapter(listadapter);
		
		new LoadClassesTask().execute();
		
		// Inflate the layout for this fragment
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progress(showProgress);
        
        klassen.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// We need an Editor object to make preference changes.
				// All objects are from android.context.Context
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("klasse", klassen.getSelectedItem().toString());

				// Commit the edits!
				editor.commit();
				refresh();
				mCallback.onClassSelected();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});
               
        ready = true;
        
        super.onViewCreated(view, savedInstanceState);
    }
    
    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }
    
    public void setVertretungsplan(Vertretungsplan v) {
    	this.v = v;
    	refresh();
    }
    
    public void refresh() { 
    	if(ready && v != null && getView() != null) {
	    	txtStand.setText(v.getTage().get(0).getStand());
	        listadapter.clear();
	        
		    klasse = (String) klassen.getSelectedItem();

	        for (VertretungsplanTag tag:v.getTage()) {
		    	listadapter.addSeparatorItem(tag.getDatum());
		        if (tag.getKlassen().get(klasse) != null) {
			        if (tag.getKlassen().get(klasse).getVertretung().size() > 0) {
				        for (Vertretung item:tag.getKlassen().get(klasse).getVertretung()) {
				        	listadapter.addItem(item);
				        }
			        } else {
			        	listadapter.addTextItem(getResources().getString(R.string.no_info));
			        }
		        } else {
		        	listadapter.addTextItem(getResources().getString(R.string.no_info));
		        }
	        }
    	}
    }
    
    public void progress(boolean show) {
    	showProgress = show;
    	if (pBar != null) {
	    	if (show == true) {
	    		Animations.crossfade(list, pBar);
	    	} else {
	    		Animations.crossfade(pBar, list);
	    		list.setVisibility(View.VISIBLE);
	    	}
    	}
    }
    
    public class VertretungAdapter extends BaseAdapter {
    	 
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_TEXT = 2;
        private static final int TYPE_MAX_COUNT = TYPE_TEXT + 1;
 
        private ArrayList<Object> mData = new ArrayList<Object>();
        private LayoutInflater mInflater;
 
        private TreeSet<Object> mSeparatorsSet = new TreeSet<Object>();
        private TreeSet<Object> mTextsSet = new TreeSet<Object>();
 
        public VertretungAdapter(Context context) {
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addItem(final Vertretung item) {
            mData.add(item);
            notifyDataSetChanged();
        }
 
        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }
        
        public void addTextItem(final String item) {
            mData.add(item);
            // save separator position
            mTextsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }
 
        @Override
        public int getItemViewType(int position) {
        	return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : (mTextsSet.contains(position) ? TYPE_TEXT : TYPE_ITEM);
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
        	// Restore preferences
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
            Boolean farben = settings.getBoolean("farben", true);
            
        	ViewHolder holder = null;
            int type = getItemViewType(position);         
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.vertretung, null);
                        holder.stunde = (TextView)convertView.findViewById(R.id.stunde);
                        holder.stunde.setText((CharSequence)((Vertretung) mData.get(position)).getLesson().toString());
                        
                        holder.art = (TextView)convertView.findViewById(R.id.art);
                        holder.art.setText((CharSequence)((Vertretung) mData.get(position)).getType());
                        
                        holder.text = (TextView)convertView.findViewById(R.id.text);
                        holder.text.setText((CharSequence)((Vertretung) mData.get(position)).toString());
                        
                        holder.stripe = (View) convertView.findViewById(R.id.stripe);
                        if (farben) {
                        	holder.stripe.setBackgroundColor(Color.parseColor((String)((Vertretung) mData.get(position)).getColor()));
                        } else {
                        	holder.stripe.setVisibility(View.GONE);
                		}
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.separator, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                        holder.textView.setText((CharSequence) mData.get(position));
                        break;
                    case TYPE_TEXT:
                    	convertView = mInflater.inflate(R.layout.text, null);
                        holder.text = (TextView)convertView.findViewById(R.id.text);
                        holder.text.setText((CharSequence) mData.get(position));
                        
//                        if (farben) {
//                        	holder.layout = (LinearLayout)convertView.findViewById(R.id.layout);
//                        	holder.layout.setBackgroundColor(Color.parseColor("#4099CC00"));
//                        }
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
                
                switch (type) {
                case TYPE_ITEM:
                    holder.stunde = (TextView)convertView.findViewById(R.id.stunde);
                    holder.stunde.setText((CharSequence)((Vertretung) mData.get(position)).getLesson().toString());
                    
                    holder.art = (TextView)convertView.findViewById(R.id.art);
                    holder.art.setText((CharSequence)((Vertretung) mData.get(position)).getType());
                    
                    holder.text = (TextView)convertView.findViewById(R.id.text);
                    holder.text.setText((CharSequence)((Vertretung) mData.get(position)).toString());
                	
                    holder.stripe = (View) convertView.findViewById(R.id.stripe);
                    
                    if (farben) {
                    	holder.stripe.setBackgroundColor(Color.parseColor((String)((Vertretung) mData.get(position)).getColor()));
                    } else {
                    	holder.stripe.setVisibility(View.GONE);
            		}
                    break;
                case TYPE_SEPARATOR:
                    holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                    holder.textView.setText((CharSequence) mData.get(position));
                    break;
                case TYPE_TEXT:
                    holder.text = (TextView)convertView.findViewById(R.id.text);
                    holder.text.setText((CharSequence) mData.get(position));
                    
//                    if (farben) {
//                    	holder.layout = (LinearLayout)convertView.findViewById(R.id.layout);
//                    	holder.layout.setBackgroundColor(Color.parseColor("#4099CC00"));
//                    }
                    break;
            }
            convertView.setTag(holder);
            }
            return convertView;
        }
        
        public void clear() {
        	mData.clear();
        	mSeparatorsSet.clear();
        	mTextsSet.clear();
        	notifyDataSetChanged();
        }
 
    }
    
    public static class ViewHolder {
        public TextView text;
		public TextView art;
		public TextView stunde;
		public TextView textView;
		public LinearLayout layout;
		public View stripe;
    }
    
    private class LoadClassesTask extends AsyncTask<Void, Void, List<String>> {
    	
		@Override
		protected List<String> doInBackground(Void... params) {
			BaseParser parser = ((VertretungsplanApplication) VertretungFragment.this.getActivity().getApplication()).getParser();
			try {
				List<String> klassen = parser.getAllClasses();
				return klassen;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<String> result) {			
			if (getActivity() == null)
				return;
			
			if(result != null)
				settings.edit().putString("klassen", new Gson().toJson(result)).commit();
			else
				result = new Gson().fromJson(settings.getString("klassen", null), new TypeToken<List<String>>(){}.getType());
			
			klasse = settings.getString("klasse", "");
	        klassen.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, result));
	        for(int i=0; i < klassen.getAdapter().getCount(); i++) {
	        	  if(klassen.getAdapter().getItem(i).toString().equals(klasse)){
	        	    klassen.setSelection(i);
	        	    break;
	        	  }
	        }
		}
    	
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	mCallback = (Callback) activity;
    }
    
    @Override
    public void onDetach() {
    	super.onDetach();
    	mCallback = null;
    }
    
}

