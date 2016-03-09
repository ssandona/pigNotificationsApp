package com.example.stefano.pigapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
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
        Typeface myFontItalic=Typeface.createFromAsset(getAssets(), "fonts/PlayfairDisplay-Bold.ttf");
        Typeface myFontRegular=Typeface.createFromAsset(getAssets(),"fonts/PlayfairDisplay-BoldItalic.ttf");
        try {
            JSONObject notification = new JSONObject(messages);
            Log.d("prova", "MESS:" + messages);
            TextView titleView=(TextView)findViewById(R.id.title);
            titleView.setText(notification.getString("title"));
            titleView.setTypeface(myFontItalic);

            TextView textView=(TextView)findViewById(R.id.field);
            textView.setText(notification.getString("text"));
            textView.setTypeface(myFontRegular);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

