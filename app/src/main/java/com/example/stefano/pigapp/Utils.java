package com.example.stefano.pigapp;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by stefano on 09/04/16.
 */
public class Utils {

    public static JSONArray retrievePastNotifications(Context mContext){
        JSONArray pastNotifications;
        String notifications = PreferenceManager.
                getDefaultSharedPreferences(mContext).getString("pastNotifications", "");
        JSONObject jo;

        if(notifications!= null && !notifications.equals(""))
        {
            Log.d("UTILS", "NON vuota -> " + notifications);
            try {
                pastNotifications=new JSONArray(notifications);
            } catch (JSONException e) {
                pastNotifications=new JSONArray();
                e.printStackTrace();
            }

        }
        else{
            Log.d("UTILS","vuota iu ga");
            pastNotifications=new JSONArray();
        }
        return pastNotifications;
    }
}
