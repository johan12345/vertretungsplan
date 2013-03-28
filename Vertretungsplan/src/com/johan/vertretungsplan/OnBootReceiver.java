package com.johan.vertretungsplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        StartActivity sa = new StartActivity();
        sa.setAlarms();
    }

}
