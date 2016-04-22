package com.example.stefano.pigapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity implements Observer {
    private boolean bound = false;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList mSelectedItems;
    boolean changed=false;
    private static Context mContext;
    private MainActivity main;
    private static Activity activity;
    private Menu myMenu=null;

    ProposteFragment proposte;
    NewsFragment news;
    NotificationsFragment notifiche;
    int cont=0;
    boolean hide=false;

    boolean myItemShouldBeEnabled=false;

    private static Typeface myFontTitle;

    private static Typeface myFontApp;

    //http://progettointercomunalegiovani.it/wp-json/wp/v2/posts?filter[category_name]=eventi&filter[posts_per_page]=2


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Button fab;



    /* Defined by ServiceCallbacks interface */
    public void refreshNotifications() {
        notifiche.refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //myFontApp=Typeface.createFromAsset(getAssets(), "fonts/acmesai.ttf");
        myFontApp=Typeface.createFromAsset(getAssets(), "fonts/FunSized.ttf");
        myFontApp=Typeface.createFromAsset(getAssets(), "fonts/Comix_Loud.ttf");
        myFontApp=Typeface.createFromAsset(getAssets(), "fonts/Fishfingers.ttf");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(myFontApp);

        /*fab = (ButtonFloat) findViewById(R.id.buttonFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TUTORIAL","FINE");
                myItemShouldBeEnabled=true;
                enableDisableMenuItems();
                if(mTourGuideHandler!=null) {
                    mTourGuideHandler.cleanUp();
                }
            }
        });*/



        myFontTitle=Typeface.createFromAsset(getAssets(), "fonts/PlayfairDisplay-Bold.ttf");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

                //DO THINGS HERE

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
                //Log.d(TAG,"SCROLLED");
                if (cont > 1) {
                    if (!hide) {
                        proposte.hideTitle();
                        news.hideTitle();
                        notifiche.hideTitle();
                        hide = true;
                    }
                } else {
                    cont++;
                }

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

                // TODO Auto-generated method stub
                //Log.d(TAG, "STATE CHANGED");
                if (hide) {
                    proposte.showTitle();
                    news.showTitle();
                    notifiche.showTitle();
                    hide = false;
                }


            }
        });
        mContext=getApplicationContext();
        main=this;

        /*Button retry=(Button)rootView.findViewById(R.id.retry);
        retry.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                if (isNetworkConnected()) {
                    initializeEverything();
                } else {
                    Toast.makeText(mContext, (String) getResources().getString(R.string.no_internet),
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                Log.d(TAG,"gone");
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.d(TAG, getString(R.string.gcm_send_message));
                } else {
                    Log.d(TAG,getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            int defaultValue = -1;
            int topicsSubscriptions = PreferenceManager.
                    getDefaultSharedPreferences(this).getInt("topics", defaultValue);

            if (topicsSubscriptions == defaultValue) {
                PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                        .putInt("topics", 3).apply();
            }
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            if (topicsSubscriptions == defaultValue) {
                //launchDialog(true);
                myItemShouldBeEnabled=false;
            }
            else {myItemShouldBeEnabled=true;}
        }
        /*
        if(isNetworkConnected()){
            initializeEverything();
        }
        else{
            Toast.makeText(mContext, (String) getResources().getString(R.string.no_internet),
                    Toast.LENGTH_LONG).show();
            no_internet.setVisibility(View.VISIBLE);
        }*/
        loadEverything();



        /*if(isNetworkConnected()){
            initializeEverything();
        }
        else{
            Toast.makeText(mContext, (String) getResources().getString(R.string.no_internet),
                    Toast.LENGTH_LONG).show();
        }*/

    }

    public void loadEverything(){
        if(isNetworkConnected()){
            initializeEverything();
        }
        else{
            Toast.makeText(mContext, (String) getResources().getString(R.string.no_internet),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "NEWWWW NOTIFICATIONNNNNN");
        notifiche.setContent();
        //Toast.makeText(this, String.valueOf("activity observer " + data), Toast.LENGTH_SHORT).show();
    }


    public void initializeEverything() {
        String urlProposte = "http://progettointercomunalegiovani.it/wp-json/wp/v2/posts?filter[category_name]=eventi&filter[posts_per_page]=3";
        String urlNews="http://progettointercomunalegiovani.it/wp-json/wp/v2/posts?filter[category_name]=notizie-pig&filter[posts_per_page]=3";
        new RetrieveFeedTask().execute(urlProposte, urlNews);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        ObservableObject.getInstance().addObserver(this);
        Log.d(TAG, "ON RESUME");
        if(notifiche!=null) {
            notifiche.setContent();
        }

    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        ActionMenuItemView amiv=(ActionMenuItemView)tb.findViewById(R.id.refresh);
        if(amiv==null){
            Log.d("DEBUG", "onWindowFocusChanged NULL");
        }
        else {
            Log.d("DEBUG", "onWindowFocusChanged NOT NULL");
        }
    }

    /*@Override
    protected void onPostResume (){
        super.onPostResume();
        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        mButton1=(ActionMenuItemView)tb.findViewById(R.id.share);
        if(mButton1==null){
            Log.d(TAG,"NO WAY");
        }
        else {
            //startTutorial();
            Log.d(TAG, "YESSSSSS");
        }
        startTutorial();
    }*/

    private static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

/*
    @Override
    protected void onResume() {
        Log.v("Example", "onResume");

        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);

        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }*/

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void parseRSS(String result){
        //parse JSON data
        try {
            int i;
            JSONArray jArray = new JSONArray(result);
            for(i=0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);

                String name = jObject.getString("name");
                String tab1_text = jObject.getString("tab1_text");
                int active = jObject.getInt("active");

            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }

    /*private static String retrieveRSS(String url_string){
            String result="";
            try {
                URL url = new URL(url_string);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(1000);
                InputStream inputStream= urlConnection.getInputStream();
                // Convert response to string using String Builder
                try {
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sBuilder.append(line + "\n");
                    }

                    inputStream.close();
                    result = sBuilder.toString();
                    Log.d(TAG,"RETRIEVE: "+result);

                } catch (Exception e) {
                    Log.d(TAG,"RETRIEVE: errore");
                }
            } catch (Exception ex) {
                Log.d(TAG,"RETRIEVE ERROR: "+ex.toString());
                return null;
            }
        return result;
    }*/

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "On create menu");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notifications_settings) {
            launchDialog(false);
            return true;
        }
        if (id == R.id.share) {
            sendMessageV2();
            return true;
        }
        if (id == R.id.refresh) {
            if(isNetworkConnected()){
                initializeEverything();
            }
            else{
                Toast.makeText(mContext, (String) getResources().getString(R.string.no_internet),
                        Toast.LENGTH_LONG).show();
            /*LinearLayout no_internet=(LinearLayout)findViewById(R.id.no_internet);
            no_internet.setVisibility(View.VISIBLE);*/
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*Whatsapp*/
    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void sendMessageV1() {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            //Uri uri = Uri.parse("smsto:" + "3409473763");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Good Morning");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else {
            Toast.makeText(this, "WhatsApp non installato",
                    Toast.LENGTH_SHORT).show();
            //Uri uri = Uri.parse("market://details?id=com.whatsapp");
            //Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            //startActivity(goToMarket);

        }
    }

    public void sendMessageV2() {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            Uri uri = Uri.parse("smsto:" + "3667360354");
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,uri);
            sendIntent.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(sendIntent,""));
        } else {
            Toast.makeText(this, "WhatsApp non installato",
                    Toast.LENGTH_SHORT).show();
            //Uri uri = Uri.parse("market://details?id=com.whatsapp");
            //Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            //startActivity(goToMarket);

        }
    }
    /*end Whatsapp*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "On prepare");
        super.onPrepareOptionsMenu(menu);
        enableDisableMenuItems();

        return true;
    }

    public void enableDisableMenuItems(){
        MenuItem item1 = myMenu.findItem(R.id.share);
        MenuItem item2 = myMenu.findItem(R.id.refresh);
        MenuItem item3 = myMenu.findItem(R.id.notifications_settings);

        if (myItemShouldBeEnabled) {
            item1.setEnabled(true);
            item2.setEnabled(true);
            item3.setEnabled(true);
        } else {
            // disabled
            item1.setEnabled(false);
            item2.setEnabled(false);
            item3.setEnabled(false);
        }
    }

    public void launchDialog(final boolean firstTime) {

        mSelectedItems = new ArrayList();
        final boolean[] checkedValues = new boolean[getResources().getStringArray(R.array.notifications_topics).length];
        int defaultValue = -1;
        int topicsSubscriptions = PreferenceManager.
                getDefaultSharedPreferences(this).getInt("topics", defaultValue);
        switch(topicsSubscriptions){
            case(0):{
                checkedValues[0] = false;
                checkedValues[1] = false;
                break;
            }
            case(1):{
                checkedValues[0] = true;
                checkedValues[1] = false;
                break;
            }
            case(2):{
                checkedValues[0] = false;
                checkedValues[1] = true;
                break;
            }
            case(3):{
                checkedValues[0] = true;
                checkedValues[1] = true;
                break;
            }
            default:{
                checkedValues[0] = true;
                checkedValues[1] = true;
                break;
            }
        }
              // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


            // Set the dialog title
            builder.setTitle(R.string.notifications_settings)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(R.array.notifications_topics, checkedValues,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    Log.d(TAG, "CLICK -> " + isChecked);
                                    changed = true;
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        mSelectedItems.add(which);

                                    } else if (mSelectedItems.contains(which)) {
                                        // Else, if the item is already in the array, remove it
                                        mSelectedItems.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (changed) {
                                if (checkedValues[0] && checkedValues[1]) {
                                    PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                            .putInt("topics", 3).apply();
                                } else {
                                    if (checkedValues[0]) {
                                        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                                .putInt("topics", 1).apply();
                                    } else {
                                        if (checkedValues[1]) {
                                            PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                                    .putInt("topics", 2).apply();
                                        } else {
                                            PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                                    .putInt("topics", 0).apply();
                                        }
                                    }
                                }
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                            }
                            changed = false;
                            dialog.cancel();
                            if (firstTime) {
                                /*ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(main);

                                showCaseBuilder.setTarget(new ViewTarget(((Toolbar) findViewById(R.id.toolbar)).getChildAt(1)));
                                showCaseBuilder.setContentTitle("Title");
                                showCaseBuilder.setContentText("text");
                                showCaseBuilder.setStyle(R.style.ShowcaseView_Light);
                                showCaseBuilder.build();*/
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            Dialog notificationSettings = builder.create();
            notificationSettings.show();
        if(firstTime){

        }

        }

    public ChainTourGuide mTourGuideHandler;
    public Activity mActivity;
    private View mButton1, mButton2, mButton3;
    private View toolbar;
    private Animation mEnterAnimation, mExitAnimation;

    public static int OVERLAY_METHOD = 1;
    public static int OVERLAY_LISTENER_METHOD = 2;

    public static String CONTINUE_METHOD = "continue_method";
    private int mChosenContinueMethod;

    public void startTutorial(){

        /*MenuItem mi=(myMenu.findItem(R.id.share));
        mButton1 = (Toolbar)findViewById(R.id.toolbar);
        View av=(View)MenuItemCompat.getActionView(mi);*/
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        mButton1=(ActionMenuItemView)toolbar.findViewById(R.id.share);
        mButton2=(ActionMenuItemView)toolbar.findViewById(R.id.refresh);
        mButton3=(ActionMenuItemView)toolbar.findViewById(R.id.notifications_settings);
        if(mButton1==null || mButton2==null || mButton3==null){
            Log.d(TAG,"NO WAY");
        }
        else Log.d(TAG,"YESSSSSS");

        Intent intent = getIntent();
        //mChosenContinueMethod = intent.getIntExtra(CONTINUE_METHOD, OVERLAY_METHOD);
        mChosenContinueMethod = intent.getIntExtra(CONTINUE_METHOD, OVERLAY_LISTENER_METHOD);

        mActivity = this;

        //setContentView(R.layout.activity_in_sequence);

        /* Get 3 buttons from layout */
        //mButton1 = (Toolbar) findViewById(R.id.toolbar);
        //mButton2 = mButton1;
        //mButton3 = mButton2;

        /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);


        if (mChosenContinueMethod == OVERLAY_METHOD) {
            runOverlay_ContinueMethod();
        } else if (mChosenContinueMethod == OVERLAY_LISTENER_METHOD){
            runOverlayListener_ContinueMethod();
        }
        /*MenuItem ref = myMenu.getItem(0);
        Log.d("TUTORIAL",ref.toString());
        ImageView button = (ImageView) ref.getActionView();
        Log.d("TUTORIAL",button.toString());*/
        /*Toolbar tb=(Toolbar) findViewById(R.id.toolbar);

        Animation animation = new TranslateAnimation(0f, 0f, 200f, 0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setInterpolator(new BounceInterpolator());

        ToolTip toolTip = new ToolTip()
                .setTitle("Next Button")
                .setDescription("Click on Next button to proceed...")
                .setTextColor(Color.parseColor("#bdc3c7"))
                .setBackgroundColor(Color.parseColor("#e74c3c"))
                .setShadow(true)
                .setGravity(Gravity.TOP | Gravity.LEFT)
                .setEnterAnimation(animation);

        TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(toolTip)
                .setOverlay(new Overlay().disableClick(true))
                .playOn(tb);*/

        /*Toolbar tb=(Toolbar) findViewById(R.id.toolbar);
        TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setTitle("Benvenuto! Questo è un tutorial su come utilizzare l'app.").setDescription("Nel menu sono presenti 3 icone: \"Condividi le tue idee\", \"Refresh\" e \"Gestione notifiche\"."))
                .setOverlay(new Overlay())
                .playOn(tb);*/


        /*// sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(main, "tutorial");

        sequence.setConfig(config);

        sequence.addSequenceItem((Button) findViewById(R.id.share),
                "This is button one", "GOT IT");

        sequence.addSequenceItem((Button)findViewById(R.id.refresh),
                "This is button two", "GOT IT");

        sequence.addSequenceItem((Button)findViewById(R.id.notifications_settings),
                "This is button three", "GOT IT");

        sequence.start();*/
    }

    private void runOverlay_ContinueMethod(){
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Benvenuto nella nuova app del PIG!")
                                .setDescription("Questo è un breve tutorial su come utilizzare l'applicazione. Per passare allo step successivo, cliccare un punto qualsiasi dello schermo.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                                .setHoleRadius(0)
                )
                        // note that there is not Overlay here, so the default one will be used
                .playLater(toolbar);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Menu")
                                .setDescription("Nel menu sono presenti 3 icone: \"Condividi le tue idee\", \"Refresh\" e \"Gestione notifiche\".")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(toolbar);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Condividi le tue idee")
                                .setDescription("Permette di comunicare direttamente con noi. Utilizzalo per inviarci pensieri, opinioni, consigli e quant'altro.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(mButton1);


        ChainTourGuide tourGuide4 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Refresh")
                                .setDescription("Permette di aggiornare la lista degli eventi e proposte all'ultima versione online.")
                                .setGravity(Gravity.BOTTOM)
                )
                        // note that there is not Overlay here, so the default one will be used
                .playLater(mButton2);

        ChainTourGuide tourGuide5 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Gestione notifiche")
                                .setDescription("Permette di selezionare il tipo di notifiche che si è interessati a ricevere (Biblioteca, Tesserati).")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(mButton3);

        ChainTourGuide tourGuide6 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Navigazione")
                                .setDescription("Per passare da una sezione all'altra dell'app, trascina la schermata a destra e sinistra.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                                .setHoleRadius(0)
                )
                .playLater(toolbar);

        ChainTourGuide tourGuide7 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Fine")
                                .setDescription("Il tutorial è finito. Cliccare il bottone in basso a destra per iniziare ad utilizzare l'app.")
                                .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                                .setHoleRadius(0)
                                .setEnterAnimation(mEnterAnimation)
                                /*.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TUTORIAL","FINE");
                        myItemShouldBeEnabled=true;
                        enableDisableMenuItems();
                        mTourGuideHandler.cleanUp();
                    }
                })*/
                )
                .playLater((View)fab);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3,tourGuide4,tourGuide5,tourGuide6, tourGuide7)
                .setDefaultOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        mTourGuideHandler = ChainTourGuide.init(this).playInSequence(sequence);
        //mTourGuideHandler.cleanUp();

        Log.d("TUTORIAL","FINE");
    }

    private void runOverlayListener_ContinueMethod(){
        Log.d("TUTORIAL", "CLEANNNNNN");
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Benvenuto nella nuova app del PIG!")
                                .setDescription("Questo è un breve tutorial su come utilizzare l'applicazione. Per passare allo step successivo, cliccare un punto qualsiasi dello schermo.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                                .setHoleRadius(0)
                )
                        // note that there is not Overlay here, so the default one will be used
                .playLater(toolbar);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Menu")
                                .setDescription("Nel menu sono presenti 3 icone: \"Condividi le tue idee\", \"Refresh\" e \"Gestione notifiche\".")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(toolbar);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Condividi le tue idee")
                                .setDescription("Permette di comunicare direttamente con noi. Utilizzalo per inviarci pensieri, opinioni, consigli e quant'altro.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(mButton1);


        ChainTourGuide tourGuide4 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Refresh")
                                .setDescription("Permette di aggiornare la lista degli eventi e proposte all'ultima versione online.")
                                .setGravity(Gravity.BOTTOM)
                )
                        // note that there is not Overlay here, so the default one will be used
                .playLater(mButton2);

        ChainTourGuide tourGuide5 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Gestione notifiche")
                                .setDescription("Permette di selezionare il tipo di notifiche che si è interessati a ricevere (Biblioteca, Tesserati).")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                )
                .playLater(mButton3);

        ChainTourGuide tourGuide6 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Navigazione")
                                .setDescription("Per passare da una sezione all'altra dell'app, trascina la schermata a destra e sinistra.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                                .setHoleRadius(0)
                )
                .playLater(toolbar);

        ChainTourGuide tourGuide7 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Fine")
                                .setDescription("Il tutorial è finito. Cliccare un punto a caso dello schermo per iniziare ad utilizzare l'app.")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setHoleRadius(0)
                                .setEnterAnimation(mEnterAnimation)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("TUTORIAL","FINE");
                                        myItemShouldBeEnabled=true;
                                        enableDisableMenuItems();
                                        mTourGuideHandler.cleanUp();
                                    }
                                })
                                /*.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TUTORIAL","FINE");
                        myItemShouldBeEnabled=true;
                        enableDisableMenuItems();
                        mTourGuideHandler.cleanUp();
                    }
                })*/
                )
                .playLater(toolbar);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3, tourGuide4, tourGuide5, tourGuide6, tourGuide7)
                .setDefaultOverlay(new Overlay()
                                .setEnterAnimation(mEnterAnimation)
                                .setExitAnimation(mExitAnimation)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mTourGuideHandler.next();
                                    }
                                })
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OverlayListener)
                .build();

        mTourGuideHandler = ChainTourGuide.init(this).playInSequence(sequence);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ProposteFragment extends Fragment {

        private BroadcastReceiver mRegistrationBroadcastReceiver;
        private ProgressBar mRegistrationProgressBar;
        private TextView mInformationTextView;
        String content="";
        View rootView;
        CustomListAdapter adapter=null;
        String[] excerpt_strings;
        Drawable[] images;
        String[] links;
        TextView titleView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /*public ProposteFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }*/

        public void refresh(){
            adapter.notifyDataSetChanged();
        }

        public void hideTitle(){
            titleView.setVisibility(View.INVISIBLE);
        }

        public void showTitle(){
            titleView.setVisibility(View.VISIBLE);
        }

        public void setText(String text) {
            /*TextView t = (TextView) getView().findViewById(R.id.proposte_content);  //UPDATE
            t.setText(Html.fromHtml(text));*/
        }

        public void setImage(Drawable d){
            /*ImageView img=(ImageView)rootView.findViewById((R.id.proposteImage));
            img.setImageDrawable(d);*/
        }

        public void setContent(String[] excerpt_strings, Drawable[] images, final String[] links){
            this.excerpt_strings = excerpt_strings;
            this.images = images;
            this.links = links;
            if(adapter==null) {
                adapter = new CustomListAdapter(getActivity(), excerpt_strings, images, false);

                ListView list=(ListView)rootView.findViewById(R.id.proposteList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        String Slecteditem = links[+position];
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Slecteditem));
                        startActivity(browserIntent);
                        //Toast.makeText(mContext, Slecteditem, Toast.LENGTH_SHORT).show();

                    }
                });
            }

            refresh();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            /*TextView textView = (TextView) rootView.findViewById(R.id.proposte_content);
            textView.setText(content);*/
            titleView = (TextView) rootView.findViewById(R.id.proposte_title);
            titleView.setTypeface(myFontApp);

            String[] titles2=null;
            if(savedInstanceState!=null) {
                titles2 = savedInstanceState.getStringArray("titles");
            }
            if(titles2!=null){
                int i;
                for(i=0;i<titles2.length;i++){
                    Log.d("PROPOSTE ELEMENTONSAVED",titles2[i]);
                }
            }
            else{
                Log.d("PROPOSTE ELEMENTONSAVED","empty");
            }




            /*Spanned result = Html.fromHtml(formattedText);
            view.setText(result);*/


            //mRegistrationProgressBar = (ProgressBar) rootView.findViewById(R.id.registrationProgressBar);
            /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        mInformationTextView.setText(getString(R.string.gcm_send_message));
                    } else {
                        mInformationTextView.setText(getString(R.string.token_error_message));
                    }
                }
            };*/
            //mInformationTextView = (TextView) rootView.findViewById(R.id.informationTextView);

            return rootView;
        }

        /*@Override
        public void onSaveInstanceState(Bundle icicle) {
            // NEVER CALLED
            super.onSaveInstanceState(icicle);
            Log.d("FRAGMENTONSAVE", "on save");
            icicle.putStringArray("titles", excerpt_strings);
            icicle.putStringArray("links", links);
            Bitmap[] bitmapImages=new Bitmap[images.length];
            int i;
            for(i=0;i<images.length;i++){
                bitmapImages[i]=((BitmapDrawable) images[i]).getBitmap();
            }
            icicle.putParcelableArray("images", bitmapImages);

        }*/


    }

    /**
     * A fragment containing the news
     */
    public static class NewsFragment extends Fragment {

        private BroadcastReceiver mRegistrationBroadcastReceiver;
        private ProgressBar mRegistrationProgressBar;
        private TextView mInformationTextView;
        String content="";
        View rootView;
        CustomListAdapter adapter;
        String[] excerpt_strings;
        Drawable[] images;
        String[] links;
        TextView titleView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /*public NewsFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }*/

        public void refresh(){
            adapter.notifyDataSetChanged();
        }

        public void hideTitle(){
            titleView.setVisibility(View.INVISIBLE);
        }

        public void showTitle(){
            titleView.setVisibility(View.VISIBLE);
        }

        public void setContent(String[] excerpt_strings, Drawable[] images, final String[] links) {
            this.excerpt_strings = excerpt_strings;
            this.images = images;
            this.links = links;
            if (adapter == null) {
                adapter = new CustomListAdapter(getActivity(), excerpt_strings, images, false);

                ListView list = (ListView) rootView.findViewById(R.id.newsList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        String Slecteditem = links[+position];
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Slecteditem));
                        startActivity(browserIntent);
                        //Toast.makeText(mContext, Slecteditem, Toast.LENGTH_SHORT).show();

                    }
                });
            }

            refresh();
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_news, container, false);
            titleView = (TextView) rootView.findViewById(R.id.news_title);
            titleView.setTypeface(myFontApp);


            String[] titles2=null;
            if(savedInstanceState!=null) {
                titles2 = savedInstanceState.getStringArray("titles");
            }
            if(titles2!=null){
                int i;
                for(i=0;i<titles2.length;i++){
                    Log.d("NEWS ELEMENTONSAVED",titles2[i]);
                }
            }
            else{
                Log.d("NEWS ELEMENTONSAVED","empty");
            }

            /*TextView textView = (TextView) rootView.findViewById(R.id.news_content);
            textView.setText(content);*/
            return rootView;
        }

        /*@Override
        public void onSaveInstanceState(Bundle icicle) {
            // NEVER CALLED
            super.onSaveInstanceState(icicle);
            Log.d("FRAGMENTONSAVE", "on save");
            icicle.putStringArray("titles", excerpt_strings);
            icicle.putStringArray("links", links);
            Bitmap[] bitmapImages=new Bitmap[images.length];
            int i;
            for(i=0;i<images.length;i++){
                bitmapImages[i]=((BitmapDrawable) images[i]).getBitmap();
            }
            icicle.putParcelableArray("images", bitmapImages);

        }*/
    }



    /**
     * A fragment containing the news
     */
    public static class NotificationsFragment extends Fragment {

        private BroadcastReceiver mRegistrationBroadcastReceiver;
        View rootView;
        boolean opened[];
        CustomListAdapter2 adapter;
        ArrayList<String> titles=null;
        ArrayList<String> contents=null;
        ArrayList<String> dates=null;
        ArrayList<Drawable> icons=null;
        ArrayList<String> ids=null;
        ArrayList<Drawable> status=null;
        TextView titleView;
        TextView noNotificationsView;

        /*@Override
        public void onSaveInstanceState(Bundle icicle) {
            // NEVER CALLED
            super.onSaveInstanceState(icicle);
            Log.d("FRAGMENTONSAVE", "on save");
            icicle.putStringArrayList("titles", titles);
            icicle.putStringArrayList("links", contents);
            icicle.putStringArrayList("links", dates);
            ArrayList<Bitmap> bitmapImages=new ArrayList<Bitmap>();
            int i;
            for(i=0;i<icons.size();i++){
                bitmapImages.add(((BitmapDrawable) icons.get(i)).getBitmap());
            }
            icicle.putParcelableArrayList("images", bitmapImages);

        }/*

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /*public NotificationsFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }*/

        public void refresh(){
            adapter.notifyDataSetChanged();
        }

        public void hideTitle(){
            titleView.setVisibility(View.INVISIBLE);
        }

        public void showTitle(){
            titleView.setVisibility(View.VISIBLE);
        }

        public void setContent(){
            Activity activity = getActivity();
            if (!isAdded() || activity == null) {
                return;
            }
            JSONArray pastNotifications= Utils.retrievePastNotifications(mContext);
            int num=pastNotifications.length();
            if(num==0){
                return;
            }
            Log.d(TAG, "NOTIFICATIONS: "+pastNotifications.toString());

            opened=new boolean[num];
            if(adapter==null){
                titles=new ArrayList<String>();
                contents=new ArrayList<String>();
                dates=new ArrayList<String>();
                icons=new ArrayList<Drawable>();
                ids=new ArrayList<String>();
                status=new ArrayList<Drawable>();
            }
            else{
                titles.clear();
                contents.clear();
                dates.clear();
                icons.clear();
                ids.clear();
                status.clear();
            }

            int i;

            for(i=0;i<num;i++){
                Log.d(TAG,"NUM ITEM: "+num);
                try {
                    JSONObject jo=pastNotifications.getJSONObject(i);
                    Log.d(TAG, "ELEM: i-" + i + " json: " + jo.toString());
                    titles.add(0, jo.getString("title"));
                    ids.add(0,jo.getString("id"));
                    contents.add(0,jo.getString("text"));
                    dates.add(0,jo.getString("date"));
                    String category=jo.getString("category");
                    switch(category){
                        case "biblio":{icons.add(0,getResources().getDrawable( R.drawable.ic_book_open_page_variant_light)); break;}
                        case "tess":{icons.add(0,getResources().getDrawable( R.drawable.ic_people_black_24dp)); break;}
                        case "info": {icons.add(0,getResources().getDrawable( R.drawable.ic_info_outline_black_24dp)); break;}
                        default: break;
                    }
                    boolean viewed=jo.getBoolean("viewed");
                    if (viewed == false) {
                        status.add(0, getResources().getDrawable(R.drawable.circle));
                    } else {
                        status.add(0, getResources().getDrawable(R.drawable.transparent));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for(i=0;i<pastNotifications.length();i++){
                Log.d(TAG, "i: "+titles.get(i));
            }
            if(pastNotifications.length()>0){
                noNotificationsView.setVisibility(View.GONE);
            }
            if(adapter==null) {
                adapter = new CustomListAdapter2(getActivity(), titles, contents, dates, icons, status);
                ListView list=(ListView)rootView.findViewById(R.id.notificationsList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        /*AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle(titles.get(position))
                                .setMessage(contents.get(position))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                        textView.setMaxLines(10);
                        textView.setScroller(new Scroller(mContext));
                        textView.setVerticalScrollBarEnabled(true);
                        textView.setMovementMethod(new ScrollingMovementMethod());*/
                        Intent resultIntent = new Intent(mContext, NotificationActivity.class);
                        resultIntent.putExtra("message", ids.get(position));
                        resultIntent.putExtra("src", 0);
                        startActivity(resultIntent);

                    }
                });
            }

            refresh();

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
            titleView = (TextView) rootView.findViewById(R.id.notifications_title);
            noNotificationsView = (TextView) rootView.findViewById(R.id.noNotifications);
            titleView.setTypeface(myFontApp);
            setContent();


            String[] titles2=null;
            if(savedInstanceState!=null) {
                titles2 = savedInstanceState.getStringArray("titles");
            }
            if(titles2!=null){
                int i;
                for(i=0;i<titles2.length;i++){
                    Log.d("PROPOSTE ELEMENTONSAVED",titles2[i]);
                }
            }
            else{
                Log.d("PROPOSTE ELEMENTONSAVED","empty");
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position)
            {
                case 0: {proposte= new ProposteFragment();return proposte;}
                case 1: {news= new NewsFragment();return news;}
                default : {notifiche=new NotificationsFragment();return notifiche;}
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }





    public class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        String[] proposte_links={null,null,null};
        String[] proposte_excerpt_strings={null,null,null};
        Drawable[] proposte_images={null,null,null};

        String[] news_links={null,null,null};
        String[] news_excerpt_strings={null,null,null};
        Drawable[] news_images={null,null,null};

        public Drawable LoadImage(String url) {
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                Log.d(TAG,"CARICATA");
                return d;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Caricamento....");
            dialog.show();
        }

        private void populate(boolean proposte, String urlString){
                int i;
                HTTPDataHandler hh = new HTTPDataHandler();
                String stream = hh.GetHTTPData(urlString);
                if (stream != null) {
                    try {
                        Log.d(TAG, "RESULT: " + stream);
                        // Get the full HTTP Data as JSONObject
                        JSONArray feed = new JSONArray(stream);
                        Log.d(TAG, "RESULT: OK");

                        for(i=0;i<3;i++) {
                            /*retrieve post excerpt*/

                            JSONObject jo = (JSONObject) feed.get(i);
                            String link = jo.getString("link");
                            Log.d(TAG, "RESULT: " + link);
                            if(proposte){
                            proposte_links[i]=link;
                            }
                            else{
                                news_links[i]=link;
                            }

                            //Content
                            /*JSONObject excerpt = jo.getJSONObject("excerpt");
                            Log.d(TAG, "RESULT: " + excerpt.toString());
                            String excerpt_string = excerpt.getString("rendered");
                            Log.d(TAG, "RESULT: " + excerpt_string);*/

                            //Title
                            JSONObject excerpt = jo.getJSONObject("title");
                            Log.d(TAG, "RESULT: " + excerpt.toString());
                            String excerpt_string = excerpt.getString("rendered");
                            Log.d(TAG, "RESULT: " + excerpt_string);

                            if(proposte){

                                proposte_excerpt_strings[i]=excerpt_string;
                            }
                            else{
                                news_excerpt_strings[i]=excerpt_string;
                            }


                            /*retrieve post image*/
                            JSONObject content = jo.getJSONObject("content");
                            String rendered = content.getString("rendered");
                            Document doc = Jsoup.parse(rendered);
                            Elements imgElements = doc.getElementsByTag("img");
                            for (Element img : imgElements) {
                                String linkHref = img.attr("src");
                                Log.d(TAG, "RESULT: IMG" + linkHref);
                                if(proposte){
                                    proposte_images[i]=LoadImage(linkHref);
                                }
                                else{
                                    news_images[i]=LoadImage(linkHref);;
                                }
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            int i;

            populate(true,params[0]);
            populate(false,params[1]);

            // Return the data from specified url
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
                //proposte.setText(excerpt_string);
                //proposte.setImage(d);
            proposte.setContent(proposte_excerpt_strings, proposte_images, proposte_links);
            news.setContent(news_excerpt_strings, news_images, news_links);
            notifiche.setContent();

            dialog.dismiss();
            dialog.cancel();
            if(!myItemShouldBeEnabled) {
                startTutorial();
            }
        }
    }

    /*class RetrieveFeedTask extends AsyncTask<String, Void, RSSFeed> {

        private Exception exception;

        protected RSSFeed doInBackground(String... urls) {
            try {
                URL url= new URL(urls[0]);
                SAXParserFactory factory =SAXParserFactory.newInstance();
                SAXParser parser=factory.newSAXParser();
                XMLReader xmlreader=parser.getXMLReader();
                RssHandler theRSSHandler=new RssHandler();
                xmlreader.setContentHandler(theRSSHandler);
                InputSource is=new InputSource(url.openStream());
                xmlreader.parse(is);
                return theRSSHandler.getFeed();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(RSSFeed feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
    */


}
