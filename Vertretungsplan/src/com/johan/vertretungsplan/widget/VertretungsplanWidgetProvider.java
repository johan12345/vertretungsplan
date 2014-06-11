package com.johan.vertretungsplan.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.johan.vertretungsplan_2.R;
import com.johan.vertretungsplan_2.StartActivity;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VertretungsplanWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
        final int N = appWidgetIds.length;
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
            // Set up the intent that starts the StackViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, VertretungsplanWidgetService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));      
            
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            // Create an Intent to launch Activity
            Intent startActivityIntent = new Intent(context, StartActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 5000, startActivityIntent, 0);            

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            views.setOnClickPendingIntent(R.id.home, pendingIntent);
            views.setRemoteAdapter(R.id.listView, intent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
        }
    }
}
