package com.johan.vertretungsplan_2;

import java.io.IOException;
import java.net.URLEncoder;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.Toast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import com.johan.vertretungsplan_2.deviceinfoendpoint.Deviceinfoendpoint;
import com.johan.vertretungsplan_2.deviceinfoendpoint.model.DeviceInfo;

/**
 * This class is started up as a service of the Android application. It listens
 * for Google Cloud Messaging (GCM) messages directed to this device.
 * 
 * When the device is successfully registered for GCM, a message is sent to the
 * App Engine backend via Cloud Endpoints, indicating that it wants to receive
 * broadcast messages from the it.
 * 
 * Before registering for GCM, you have to create a project in Google's Cloud
 * Console (https://code.google.com/apis/console). In this project, you'll have
 * to enable the "Google Cloud Messaging for Android" Service.
 * 
 * Once you have set up a project and enabled GCM, you'll have to set the
 * PROJECT_NUMBER field to the project number mentioned in the "Overview" page.
 * 
 * See the documentation at
 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class GCMIntentService extends GCMBaseIntentService {
	private final Deviceinfoendpoint endpoint;

	/*
	 * TODO: Set this to a valid project number. See
	 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
	 * information.
	 */
	protected static final String PROJECT_NUMBER = "133323756868";

	/**
	 * Register the device for GCM.
	 * 
	 * @param mContext
	 *            the activity's context.
	 */
	public static void register(Context mContext) {
		GCMRegistrar.checkDevice(mContext);
		GCMRegistrar.checkManifest(mContext);
		GCMRegistrar.register(mContext, PROJECT_NUMBER);
	}

	/**
	 * Unregister the device from the GCM service.
	 * 
	 * @param mContext
	 *            the activity's context.
	 */
	public static void unregister(Context mContext) {
		GCMRegistrar.unregister(mContext);
	}

	public GCMIntentService() {
		super(PROJECT_NUMBER);
		Deviceinfoendpoint.Builder endpointBuilder = new Deviceinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize(HttpRequest httpRequest) {
					}
				});
		endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
	}

	/**
	 * Called on registration error. This is called in the context of a Service
	 * - no dialog or UI.
	 * 
	 * @param context
	 *            the Context
	 * @param errorId
	 *            an error message
	 */
	@Override
	public void onError(Context context, String errorId) {

		
	}

	/**
	 * Called when a cloud message has been received.
	 */
	@Override
	public void onMessage(Context context, Intent intent) {
		sendNotificationIntent(
				context,
						intent.getStringExtra("message"), true, false);
	}

	/**
	 * Called back when a registration token has been received from the Google
	 * Cloud Messaging service.
	 * 
	 * @param context
	 *            the Context
	 */
	@Override
	public void onRegistered(Context context, String registration) {
		boolean alreadyRegisteredWithEndpointServer = false;

		try {
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			
			DeviceInfo deviceInfo = new DeviceInfo();
			deviceInfo
				.setDeviceRegistrationID(registration)
				.setTimestamp(System.currentTimeMillis())
				.setDeviceInformation(
						URLEncoder
						.encode(android.os.Build.MANUFACTURER
								+ " "
								+ android.os.Build.PRODUCT,
								"UTF-8"));
			deviceInfo.setKlasse(settings.getString("klasse", ""));
			deviceInfo.setSchoolId(settings.getString("selected_school", ""));

			/*
			 * Using cloud endpoints, see if the device has already been
			 * registered with the backend
			 */
			DeviceInfo existingInfo = endpoint.getDeviceInfo(registration)
					.execute();

			if (existingInfo != null && registration.equals(existingInfo.getDeviceRegistrationID())
					&& deviceInfo.getKlasse().equals(existingInfo.getKlasse())
					&& deviceInfo.getSchoolId().equals(existingInfo.getSchoolId())) {
				alreadyRegisteredWithEndpointServer = true;
			}

			if (!alreadyRegisteredWithEndpointServer) {
				/*
				 * We are not registered as yet. Send an endpoint message
				 * containing the GCM registration id and some of the device's
				 * product information over to the backend. Then, we'll be
				 * registered.
				 */
				if(existingInfo != null) {
					endpoint.removeDeviceInfo(registration).execute();
				}
				endpoint.insertDeviceInfo(deviceInfo).execute();
			}
		} catch (IOException e) {
			Log.e(GCMIntentService.class.getName(),
					"Exception received when attempting to register with server at "
							+ endpoint.getRootUrl(), e);

			Log.d("Vertretungsplan",
					"1) Registration with Google Cloud Messaging...SUCCEEDED!\n\n"
							+ "2) Registration with Endpoints Server...FAILED!\n\n"
							+ "Unable to register your device with your Cloud Endpoints server running at "
							+ endpoint.getRootUrl()
							+ ". Either your Cloud Endpoints server is not deployed to App Engine, or "
							+ "your settings need to be changed to run against a local instance "
							+ "by setting LOCAL_ANDROID_RUN to 'true' in CloudEndpointUtils.java.");
			return;
		}
	}

	/**
	 * Called back when the Google Cloud Messaging service has unregistered the
	 * device.
	 * 
	 * @param context
	 *            the Context
	 */
	@Override
	protected void onUnregistered(Context context, String registrationId) {

		if (registrationId != null && registrationId.length() > 0) {

			try {
				endpoint.removeDeviceInfo(registrationId).execute();
			} catch (IOException e) {
				Log.e(GCMIntentService.class.getName(),
						"Exception received when attempting to unregister with server at "
								+ endpoint.getRootUrl(), e);
				return;
			}
		}
	}

	/**
	 * Generate a notification intent and dispatch it to the RegisterActivity.
	 * This is how we get information from this service (non-UI) back to the
	 * activity.
	 * 
	 * For this to work, the 'android:launchMode="singleTop"' attribute needs to
	 * be set for the RegisterActivity in AndroidManifest.xml.
	 * 
	 * @param context
	 *            the application context
	 * @param message
	 *            the message to send
	 * @param isError
	 *            true if the message is an error-related message; false
	 *            otherwise
	 * @param isRegistrationMessage
	 *            true if this message is related to registration/unregistration
	 */
	private void sendNotificationIntent(Context context, String message,
			boolean isError, boolean isRegistrationMessage) {
		//App wird nicht angezeigt
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String sound = settings.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_notification)
		.setContentTitle("Nachricht");
		mBuilder.setContentText(message);
		if (!sound.equals("")) {
			Uri soundUri = Uri.parse(sound);
			mBuilder.setSound(soundUri);
		}
		mBuilder.setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS);
		mBuilder.setOnlyAlertOnce(true);
		mBuilder.setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, StartActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(StartActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		int mId = 1;
		mNotificationManager.notify(mId, mBuilder.build());
	}

	private String getWebSampleUrl(String endpointUrl) {
		// Not the most elegant solution; we'll improve this in the future
		if (CloudEndpointUtils.LOCAL_ANDROID_RUN) {
			return CloudEndpointUtils.LOCAL_APP_ENGINE_SERVER_URL
					+ "index.html";
		}
		return endpointUrl.replace("/_ah/api/", "/index.html");
	}
}
