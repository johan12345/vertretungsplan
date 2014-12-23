package com.johan.vertretungsplan_2;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.background.VertretungsplanService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * This class is started up as a service of the Android application. It listens
 * for Google Cloud Messaging (GCM) messages directed to this device.
 * <p/>
 * When the device is successfully registered for GCM, a message is sent to the
 * App Engine backend via Cloud Endpoints, indicating that it wants to receive
 * broadcast messages from the it.
 * <p/>
 * Before registering for GCM, you have to create a project in Google's Cloud
 * Console (https://code.google.com/apis/console). In this project, you'll have
 * to enable the "Google Cloud Messaging for Android" Service.
 * <p/>
 * Once you have set up a project and enabled GCM, you'll have to set the
 * PROJECT_NUMBER field to the project number mentioned in the "Overview" page.
 * <p/>
 * See the documentation at
 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class GCMIntentService extends IntentService {

    protected static final String PROJECT_NUMBER = "133323756868";
    private static final String BASE_URL = "https://hamilton.rami.io/";
    private static GoogleCloudMessaging gcm;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    /**
     * Register the device for GCM.
     *
     * @param mContext the activity's context.
     */
    public static void register(Context mContext) throws IOException {
        String msg = "";
        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
        }
        String regid = gcm.register(PROJECT_NUMBER);
        msg = "Device registered, registration ID=" + regid;
        onRegistered(mContext, regid);
    }

    /**
     * Called back when a registration token has been received from the Google
     * Cloud Messaging service.
     *
     * @param context the Context
     */
    public static void onRegistered(Context context, String registration) {
        boolean alreadyRegisteredWithEndpointServer = false;

        try {

            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(context);
            settings.edit().putString("regId", registration).apply();

            String deviceInfo = URLEncoder.encode(android.os.Build.MANUFACTURER
                    + " " + android.os.Build.PRODUCT, "UTF-8");
            String klasse = settings.getString("klasse", "");
            String schoolId = settings.getString("selected_school", "");
            String login = settings.getString("login", "");
            String password = settings.getString("password", "");

			/*
             * Using cloud endpoints, see if the device has already been
			 * registered with the backend
			 */
            DeviceInfo existingInfo = getDeviceInfo(registration);

            if (existingInfo != null && registration.equals(existingInfo.subId)
                    && klasse.equals(existingInfo.klasse)
                    && schoolId.equals(existingInfo.schoolId)) {
                alreadyRegisteredWithEndpointServer = true;
            }

            if (!alreadyRegisteredWithEndpointServer) {
                /*
                 * We are not registered as yet. Send an endpoint message
				 * containing the GCM registration id and some of the device's
				 * product information over to the backend. Then, we'll be
				 * registered.
				 */
                insertDeviceInfo(context, deviceInfo, klasse, schoolId, login, password,
                        registration);
            }
        } catch (IOException | JSONException e) {
            Log.e(GCMIntentService.class.getName(),
                    "Exception received when attempting to register with server",
                    e);

            Log.d("Vertretungsplan",
                    "1) Registration with Google Cloud Messaging...SUCCEEDED!\n\n"
                            + "2) Registration with Endpoints Server...FAILED!\n\n"
                            + "Unable to register your device with your Cloud Endpoints server. "
                            + "Either your Cloud Endpoints server is not deployed to App Engine, or "
                            + "your settings need to be changed to run against a local instance "
                            + "by setting LOCAL_ANDROID_RUN to 'true' in CloudEndpointUtils.java.");
            return;
        }

        Intent intent = new Intent();
        intent.setAction("com.johan.vertretungsplan.registered");
    }

    private static void insertDeviceInfo(Context context, String deviceInfo, String klasse,
                                         String schoolId, String login, String password, String registration)
            throws IOException {
        String url = BASE_URL + "register?subId="
                + URLEncoder.encode(registration, "UTF-8") + "&klasse="
                + URLEncoder.encode(klasse, "UTF-8") + "&school="
                + URLEncoder.encode(schoolId, "UTF-8") + "&deviceInfo="
                + URLEncoder.encode(deviceInfo, "UTF-8") + "&login="
                + URLEncoder.encode(login, "UTF-8") + "&password="
                + URLEncoder.encode(password, "UTF-8");
        Response response = new Request(url).getResource("UTF-8");
        if (response.getResponseCode() == 200) {
            Log.d("GCM", "inserted device info");
        }
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putString("regId", registration).commit();
    }

    private static DeviceInfo getDeviceInfo(String registration) throws IOException,
            JSONException {
        String url = BASE_URL + "getregistration?subId=" + registration;
        try {
            Response response = new Request(url).getResource("UTF-8");
            JSONObject json = new JSONObject(response.getBody());
            DeviceInfo info = new DeviceInfo();
            info.subId = json.getString("_id");
            info.deviceInfo = json.getString("deviceInfo");
            info.klasse = json.getString("klasse");
            info.schoolId = json.getString("schoolId");
            return info;
        } catch (FileNotFoundException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Called when a cloud message has been received.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Intent intent2 = new Intent(this, VertretungsplanService.class);
                intent2.putExtra(VertretungsplanService.KEY_NOTIFICATION, false);
                startService(intent2);

                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(this);
                if (settings.getBoolean("notification", true)
                        && !intent.getStringExtra("message").equals("NO_NOTIFICATION"))
                    sendNotificationIntent(this, intent.getStringExtra("message"),
                            true, false);
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private DeviceInfo removeDeviceInfo(String registration) throws IOException {
        String url = BASE_URL + "removeregistration?subId=" + registration;
        new Request(url).getResource("UTF-8");
        return null;
    }

    /**
     * Generate a notification intent and dispatch it to the RegisterActivity.
     * This is how we get information from this service (non-UI) back to the
     * activity.
     * <p/>
     * For this to work, the 'android:launchMode="singleTop"' attribute needs to
     * be set for the RegisterActivity in AndroidManifest.xml.
     *
     * @param context               the application context
     * @param message               the message to send
     * @param isError               true if the message is an error-related message; false
     *                              otherwise
     * @param isRegistrationMessage true if this message is related to registration/unregistration
     */
    private void sendNotificationIntent(Context context, String message,
                                        boolean isError, boolean isRegistrationMessage) {
        // App wird nicht angezeigt
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        String sound = settings.getString("ringtone", RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nachricht").setColor(getResources().getColor(R.color.material_blue_500));
        mBuilder.setContentText("Es gibt neue Änderungen auf dem Vertretungsplan");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Es gibt neue Änderungen auf dem Vertretungsplan"));
        if (!sound.equals("")) {
            Uri soundUri = Uri.parse(sound);
            mBuilder.setSound(soundUri);
        }
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, StartActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(StartActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private static class DeviceInfo {
        public String klasse;
        public String subId;
        public String deviceInfo;
        public String schoolId;
    }
}
