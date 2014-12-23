package com.johan.vertretungsplan.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.AdditionalInfo;
import com.johan.vertretungsplan.objects.KlassenVertretungsplan;
import com.johan.vertretungsplan.objects.Vertretung;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan.objects.VertretungsplanTag;
import com.johan.vertretungsplan_2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VertretungsplanWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new VertretungsplanRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class VertretungsplanRemoteViewsFactory implements
            RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private int appWidgetId;
        private Vertretungsplan v;
        private SharedPreferences prefs;
        private Gson gson;
        private String klasse;
        private List<Object> items;

        public VertretungsplanRemoteViewsFactory(Context context, Intent intent) {
            gson = new Gson();
            this.context = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        public void load() {
            v = gson.fromJson(
                    prefs.getString("Vertretungsplan", ""), Vertretungsplan.class);
            klasse = prefs.getString("klasse", null);

            items = new ArrayList<Object>();
            if (v != null) {
                for (VertretungsplanTag tag : v.getTage()) {
                    items.add(new Separator(tag.getDatum()));
                    if ("Alle".equals(klasse)) {
                        for (Entry<String, KlassenVertretungsplan> entry : tag.getKlassen().entrySet()) {
                            items.add(new Separator(entry.getKey()));
                            for (Vertretung item : entry.getValue().getVertretung()) {
                                items.add(item);
                            }
                        }
                    } else {
                        if (tag.getKlassen().get(klasse) != null) {
                            if (tag.getKlassen().get(klasse).getVertretung().size() > 0) {
                                for (Vertretung item : tag.getKlassen().get(klasse).getVertretung()) {
                                    items.add(item);
                                }
                            } else {
                                items.add(getResources().getString(R.string.no_info));
                            }
                        } else {
                            items.add(getResources().getString(R.string.no_info));
                        }
                    }
                }
                if (prefs.getBoolean("news_widget", true)) {
                    for (VertretungsplanTag tag : v.getTage()) {
                        if (tag.getNachrichten().size() > 0) {
                            items.add(new Separator("Nachrichten " + tag.getDatum()));
                            for (String nachricht : tag.getNachrichten()) {
                                items.add(nachricht);
                            }
                        }
                    }
                    if (v.getAdditionalInfos().size() > 0) {
                        items.add(new Separator("Nachrichten"));
                        for (AdditionalInfo info : v.getAdditionalInfos()) {
                            items.add(info);
                        }
                    }
                }
            } else {
                items.add("noch keine Daten gespeichert");
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = null;
            Object item = items.get(position);
            if (item instanceof Vertretung) {
                Vertretung vtr = (Vertretung) item;
                // Construct a remote views item based on the app widget item XML file,
                // and set the listitem_text based on the position.
                rv = new RemoteViews(context.getPackageName(), R.layout.listitem_vertretung_widget);
                rv.setTextViewText(R.id.stunde, vtr.getLesson());
                rv.setTextViewText(R.id.art, vtr.getType());
                rv.setTextViewText(R.id.text, vtr.toString());
            } else if (item instanceof String) {
                String text = (String) item;
                rv = new RemoteViews(context.getPackageName(), R.layout.listitem_text_widget);
                rv.setTextViewText(R.id.text, text);
            } else if (item instanceof AdditionalInfo) {
                AdditionalInfo info = (AdditionalInfo) item;
                rv = new RemoteViews(context.getPackageName(), R.layout.listitem_additionalinfo_widget);
                rv.setTextViewText(R.id.title, info.getTitle());
                rv.setTextViewText(R.id.text, info.getText());
            } else if (item instanceof Separator) {
                String text = ((Separator) item).text;
                rv = new RemoteViews(context.getPackageName(), R.layout.listitem_separator_widget);
                rv.setTextViewText(R.id.textSeparator, text);
            }

            // Return the remote views object.
            return rv;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public void onCreate() {
            load();
        }

        @Override
        public void onDataSetChanged() {
            load();
        }

        @Override
        public void onDestroy() {

        }

        private class Separator {
            public String text;

            public Separator(String text) {
                this.text = text;
            }
        }

    }

}