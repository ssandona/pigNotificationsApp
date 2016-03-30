package com.example.stefano.pigapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActivity extends AppCompatActivity {

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setTitle(R.string.notifications);

        Intent intent = getIntent();
        String messages = intent.getStringExtra("messages");


        /*Typeface myFontItalic=Typeface.createFromAsset(getAssets(), "fonts/PlayfairDisplay-Bold.ttf");
        Typeface myFontRegular=Typeface.createFromAsset(getAssets(),"fonts/PlayfairDisplay-BoldItalic.ttf");*/
        try {
            JSONObject notification = new JSONObject(messages);
            Log.d("prova", "MESS:" + messages);
            TextView titleView=(TextView)findViewById(R.id.title);
            titleView.setText(notification.getString("title"));
            //titleView.setTypeface(myFontItalic);

            TextView textView=(TextView)findViewById(R.id.field);
            textView.setText(notification.getString("text"));
            Drawable icon=null;
            String category=notification.getString("category");
            Log.d("CATEGORY",category);
            switch(category){
                case "biblio":{icon=getResources().getDrawable( R.drawable.ic_book_open_page_variant_light); break;}
                case "tess":{icon=getResources().getDrawable( R.drawable.ic_people_black_24dp); break;}
                case "info": {icon=getResources().getDrawable( R.drawable.ic_info_outline_black_24dp); break;}
                default: break;
            }
            ImageView image=(ImageView)findViewById(R.id.icon);
            image.setImageDrawable(icon);
            Log.d("NOTIFICATIONS","fatto tutto");
            //textView.setTypeface(myFontRegular);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}

