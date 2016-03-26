package com.example.stefano.pigapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by stefano on 26/03/16.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        ObservableObject.getInstance().updateValue(intent);

    }
}
