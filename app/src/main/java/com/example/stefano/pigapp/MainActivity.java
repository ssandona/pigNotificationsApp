package com.example.stefano.pigapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class MainActivity extends AppCompatActivity implements Observer {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList mSelectedItems;
    boolean changed=false;
    private static Context mContext;
    private Menu myMenu=null;

    /*3 fragments reference usefull for the population via the asynctask*/
    static ProposteFragment proposte;
    static NewsFragment news;
    static NotificationsFragment notifiche;
    int cont=0;
    boolean hide=false;

    /*to enable after the tutorial or in case the tutorial is was already shown*/
    boolean myItemShouldBeEnabled=false;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myFontApp=Typeface.createFromAsset(getAssets(), "fonts/FunSized.ttf");
        myFontApp=Typeface.createFromAsset(getAssets(), "fonts/Comix_Loud.ttf");
        myFontApp=Typeface.createFromAsset(getAssets(), "fonts/Fishfingers.ttf");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(myFontApp);

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
                /*Hide the fragments title during the scrolling*/
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
                /*Show the fragments title when the fragment is stable*/
                if (hide) {
                    proposte.showTitle();
                    news.showTitle();
                    notifiche.showTitle();
                    hide = false;
                }


            }
        });
        mContext=getApplicationContext();

        /*retrieve GCM token to receive notifications*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
                myItemShouldBeEnabled=false;
            }
            else {myItemShouldBeEnabled=true;}
        }

        loadEverything();

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


    /*real time update of notifications*/
    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "NEWWWW NOTIFICATIONNNNNN");
        notifiche.setContent();
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


    private static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


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
            launchDialog();
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
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    /*open whatsapp on the PIG chat*/
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

    /*open the dialog of Notifications Setting*/
    public void launchDialog() {

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

        }


    /*Tour Guide*/
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

    /*End Tour Guide*/


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


        public void refresh(){
            adapter.notifyDataSetChanged();
        }

        public void hideTitle(){
            titleView.setVisibility(View.INVISIBLE);
        }

        public void showTitle(){
            titleView.setVisibility(View.VISIBLE);
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
                    }
                });
            }

            refresh();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            titleView = (TextView) rootView.findViewById(R.id.proposte_title);
            titleView.setTypeface(myFontApp);

            /*String[] titles2=null;
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
            }*/

            Log.d("TAG","Proposte creation");
            proposte=this;

            return rootView;
        }




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


            /*String[] titles2=null;
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
            }*/
            Log.d("TAG","News creation");
            news=this;

            return rootView;
        }

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


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

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


            /*String[] titles2=null;
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
            }*/
            Log.d("TAG","Notifiche creation");
            notifiche=this;

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Log.d(TAG,"PROPOSTE GET ITEM");
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


}
