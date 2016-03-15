package com.example.stefano.pigapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList mSelectedItems;
    boolean changed=false;
    private static Context mContext;
    ProposteFragment proposte;
    NewsFragment news;
    NotificationsFragment notifiche;

    private static Typeface myFontTitle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myFontTitle=Typeface.createFromAsset(getAssets(), "fonts/PlayfairDisplay-Bold.ttf");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Caricamento");
        progress.setMessage("Attendi...");
        progress.show();*/
// To dismiss the dialog
        //progress.dismiss();


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

        mContext=getApplicationContext();
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
                launchDialog(true);
            }
        }
        String urlProposte="http://progettointercomunalegiovani.it/wp-json/wp/v2/posts?filter[category_name]=eventi&filter[posts_per_page]=3";
        String urlNews="http://progettointercomunalegiovani.it/wp-json/wp/v2/posts?filter[category_name]=notizie-pig&filter[posts_per_page]=3";

        new RetrieveFeedTask().execute(urlProposte);


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
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
                                    changed=true;
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
                            if(changed) {
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
                            changed=false;
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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ProposteFragment extends Fragment {

        private BroadcastReceiver mRegistrationBroadcastReceiver;
        private ProgressBar mRegistrationProgressBar;
        private TextView mInformationTextView;
        String content="";
        View rootView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProposteFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            this.setArguments(args);
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
            CustomListAdapter adapter=new CustomListAdapter(getActivity(), excerpt_strings, images);
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            /*TextView textView = (TextView) rootView.findViewById(R.id.proposte_content);
            textView.setText(content);*/
            TextView titleView = (TextView) rootView.findViewById(R.id.proposte_title);
            titleView.setTypeface(myFontTitle);




            /*Spanned result = Html.fromHtml(formattedText);
            view.setText(result);*/


            //mRegistrationProgressBar = (ProgressBar) rootView.findViewById(R.id.registrationProgressBar);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
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
            };
            //mInformationTextView = (TextView) rootView.findViewById(R.id.informationTextView);

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
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public NewsFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_news, container, false);
            TextView titleView = (TextView) rootView.findViewById(R.id.news_title);
            titleView.setTypeface(myFontTitle);
            /*TextView textView = (TextView) rootView.findViewById(R.id.news_content);
            textView.setText(content);*/
            return rootView;
        }
    }

    /**
     * A fragment containing the news
     */
    public static class NotificationsFragment extends Fragment {

        private BroadcastReceiver mRegistrationBroadcastReceiver;
        private ProgressBar mRegistrationProgressBar;
        private TextView mInformationTextView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public NotificationsFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
            TextView titleView = (TextView) rootView.findViewById(R.id.notifications_title);
            titleView.setTypeface(myFontTitle);
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
                case 0: {proposte= new ProposteFragment(0);return proposte;}
                case 1: {news= new NewsFragment(1);return news;}
                default : {notifiche=new NotificationsFragment(2);return notifiche;}
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

        String[] eventi_links={null,null,null};
        String[] eventi_excerpt_strings={null,null,null};
        Drawable[] eventi_images={null,null,null};

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
            dialog.setMessage("Loading....");
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
                                eventi_links[i]=link;
                            }

                            JSONObject excerpt = jo.getJSONObject("excerpt");
                            Log.d(TAG, "RESULT: " + excerpt.toString());
                            String excerpt_string = excerpt.getString("rendered");
                            Log.d(TAG, "RESULT: " + excerpt_string);
                            if(proposte){
                                proposte_excerpt_strings[i]=excerpt_string;
                            }
                            else{
                                eventi_excerpt_strings[i]=excerpt_string;
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
                                    eventi_images[i]=LoadImage(linkHref);;
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
            //populate(false,params[0]);

            // Return the data from specified url
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
                //proposte.setText(excerpt_string);
                //proposte.setImage(d);
            proposte.setContent(proposte_excerpt_strings, proposte_images, proposte_links);

                dialog.dismiss();
                dialog.cancel();




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