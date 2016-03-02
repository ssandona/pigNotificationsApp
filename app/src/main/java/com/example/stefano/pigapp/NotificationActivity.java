package com.example.stefano.pigapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Intent intent = getIntent();
        String messages = intent.getStringExtra("messages");
        try {
            JSONObject notification=new JSONObject(messages);
            Log.d("prova", "MESS:" + messages);
            TextView titleView=(TextView)findViewById(R.id.title);
            titleView.setText(notification.getString("title"));
            TextView textView=(TextView)findViewById(R.id.field);
            textView.setText(notification.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

