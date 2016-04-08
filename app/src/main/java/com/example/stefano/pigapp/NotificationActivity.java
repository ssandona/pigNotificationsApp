package com.example.stefano.pigapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActivity extends AppCompatActivity {
    int src=1;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setTitle(R.string.notifications);

        Intent intent = getIntent();
        String id = intent.getStringExtra("message");
        src=intent.getIntExtra("src", 1);
        JSONArray pastNotifications= Utils.retrievePastNotifications(getApplicationContext());
       int i;
       JSONObject notification=null;
       try{
           for(i=0;i<pastNotifications.length();i++){
               JSONObject actualNotification=pastNotifications.getJSONObject(i);
               if(actualNotification.getString("id").equals(id)){
                    notification=actualNotification;
               }
           }
           TextView titleView=(TextView)findViewById(R.id.title);
           TextView textView=(TextView)findViewById(R.id.field);
           ImageView image=(ImageView)findViewById(R.id.icon);

           if(notification!=null){
               Log.d("prova", "MESS:" + notification);

               titleView.setText(notification.getString("title"));
               //titleView.setTypeface(myFontItalic);


               textView.setText(notification.getString("text"));
               Drawable icon=null;
               String category=notification.getString("category");
               Log.d("CATEGORY", category);
               switch(category){
                   case "biblio":{icon=getResources().getDrawable( R.drawable.ic_book_open_page_variant_light); break;}
                   case "tess":{icon=getResources().getDrawable( R.drawable.ic_people_black_24dp); break;}
                   case "info": {icon=getResources().getDrawable( R.drawable.ic_info_outline_black_24dp); break;}
                   default: break;
               }
               image.setImageDrawable(icon);
               Log.d("NOTIFICATIONS", "fatto tutto");

               notification.put("viewed",true);
               PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString("pastNotifications",pastNotifications.toString()).apply();

           }
           else{
               textView.setText("Errore");
               textView.setText("Errore nel reperire il testo della notifica.");
           }
       }
       catch(Exception e){}

    }
    String TAG="NOTIICATION";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"Menu item id: "+item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "FINISH");
                onBackPressed();
                // app icon in action bar clicked; goto parent activity.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "Metodo2");
            if(src==0){
                Log.d("CDA", "onBackPressed Called");
                onBackPressed();
            }
            else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                this.finish();
            }
            return true;
        }
        Log.d("CDA", "FINIO Called");
        //return super.onKeyDown(keyCode, event);
        return false;
    }*/


    @Override
    public void onBackPressed() {
        Log.d("CDA", "Metodo1");
        if(src==0){
            super.onBackPressed();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}

