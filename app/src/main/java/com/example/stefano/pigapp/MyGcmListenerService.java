package com.example.stefano.pigapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyGcmListenerService extends GcmListenerService {
    private final String TAG = "MyGcmListenerService";
    final static String GROUP_KEY_NOTIFICATIONS = "group_key_notifications";
    Context mContext;
    public JSONArray notifications=new JSONArray();

    public void onCreate() {
        mContext = getApplicationContext();
        /*
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.putInt("notifications", 0);
        editor.commit();*/

    };

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        int defaultValue = -1;
        int numberOfActualNotifications = mPrefs.getInt("notifications", defaultValue);
        Log.d(TAG, "NOTIF: " + numberOfActualNotifications);
        if(numberOfActualNotifications==defaultValue){
            numberOfActualNotifications=0;
            Log.d(TAG, "Initial -1");
        }
        /*if(numberOfActualNotifications==0){
            notifications=new JSONArray();
        }*/
        Log.d(TAG, ""+data.get("notification"));
        try {
            JSONObject notification=new JSONObject(""+data.get("notification"));


            //notifications.put(data.get("notification"));
       /* String text="";
        numberOfActualNotifications+=1;
        if(numberOfActualNotifications>1){
            text+=(numberOfActualNotifications+" ");
            text+=getString(R.string.messages);
        }
        else{
            text+=(numberOfActualNotifications+" ");
            text+=getString(R.string.message);
        }*/


// Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(MyGcmListenerService.this, NotificationActivity.class);
            resultIntent.putExtra("messages", notification.toString());


            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new android.support.v7.app.NotificationCompat.Builder(MyGcmListenerService.this)
                            .setSmallIcon(R.drawable.ic_stat_ic_notification)
                            .setContentTitle(getString(R.string.notifications_title))
                            .setAutoCancel(true)
                            .setGroup(GROUP_KEY_NOTIFICATIONS)
                            .setContentText(notification.getString("title"));




// The stack builder object will contain an artificial backs stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyGcmListenerService.this);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(NotificationActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);

            Random r = new Random();
            int nId=r.nextInt(1000);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            nId,
                            PendingIntent.FLAG_UPDATE_CURRENT

                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Log.d(TAG, "NotifID: " + nId);
            mNotificationManager.notify(nId, mBuilder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*Log.d(TAG, "NOTIF NOW: " + numberOfActualNotifications);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.putInt("notifications", numberOfActualNotifications);
        editor.commit();*/

        /*String message = data.getString("text");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }*/

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
