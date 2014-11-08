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

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.johan.vertretungsplan.objects.AdditionalInfo;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;

public class NachrichtenFragment extends VertretungsplanFragment {

	public interface Callback {
		public Vertretungsplan getVertretungsplan();
	}

	private ListView list;
	boolean ready = false;
	private Callback mCallback;

	private Activity activity;
	private NachrichtenAdapter listadapter = null;

	private Vertretungsplan v;

	public static final String EXTRA_TITLE = "Vertretungsplan";
	public static final String PREFS_NAME = "VertretungsplanLS";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nachrichten, container,
				false);
		list = (ListView) view.findViewById(R.id.listView2);

		listadapter = new NachrichtenAdapter(activity);
		list.setAdapter(listadapter);

		// Inflate the layout for this fragment
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ready = true;
		if (mCallback.getVertretungsplan() != null)
			setVertretungsplan(mCallback.getVertretungsplan());

		super.onViewCreated(view, savedInstanceState);
	}

	public static Bundle createBundle(String title) {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_TITLE, title);
		return bundle;
	}

	public void setVertretungsplan(Vertretungsplan v) {
		this.v = v;
		refresh();
	}

	public void refresh() {
		if (ready && v != null && getView() != null) {
			listadapter.clear();

			for (VertretungsplanTag tag : v.getTage()) {
				listadapter.addSeparatorItem(tag.getDatum());
				for (String message : tag.getNachrichten()) {
					listadapter.addItem(Html.fromHtml(message));
				}
				if (tag.getNachrichten().size() == 0)
					listadapter.addItem(getResources().getString(
							R.string.no_info));
			}
			for (AdditionalInfo info : v.getAdditionalInfos()) {
				listadapter.addSeparatorItem(info.getTitle());
				listadapter.addItem(info.getText());
			}
		}
	}

	public class NachrichtenAdapter extends BaseAdapter {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

		private ArrayList<CharSequence> mData = new ArrayList<CharSequence>();
		private LayoutInflater mInflater;

		private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

		public NachrichtenAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR
					: TYPE_ITEM;
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
					convertView = mInflater.inflate(R.layout.text, parent,
							false);
					holder.text = (TextView) convertView
							.findViewById(R.id.text);
					holder.text.setText((CharSequence) mData.get(position));
					break;
				case TYPE_SEPARATOR:
					convertView = mInflater.inflate(R.layout.separator_bold,
							parent, false);
					holder.textView = (TextView) convertView
							.findViewById(R.id.textSeparator);
					holder.textView.setText((CharSequence) mData.get(position));
					break;
				}
			} else {
				holder = (ViewHolder) convertView.getTag();

				switch (type) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.text, parent,
							false);
					holder.text = (TextView) convertView
							.findViewById(R.id.text);
					holder.text.setText((CharSequence) mData.get(position));
					break;
				case TYPE_SEPARATOR:
					convertView = mInflater.inflate(R.layout.separator_bold, parent,
							false);
					holder.textView = (TextView) convertView
							.findViewById(R.id.textSeparator);
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		mCallback = (Callback) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

}
