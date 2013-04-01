package com.johan.vertretungsplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Vertretungsplan", "OnBootReceiver gestartet");
		Intent autostartIntent = new Intent(context, AutostartService.class);
		context.startService(autostartIntent);
    }

}
