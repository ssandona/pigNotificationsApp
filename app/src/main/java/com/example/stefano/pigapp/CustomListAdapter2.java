package com.example.stefano.pigapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter2 extends ArrayAdapter<String> {

    private final Activity mContext;
    private final ArrayList<String>  notificationsTitle;
    private final ArrayList<String>  notificationsText;
    private final ArrayList<String>  notificationsDate;
    private final ArrayList<Drawable>  images;
    private boolean isImageFitToScreen=false;


    public CustomListAdapter2(Activity context, ArrayList<String> notificationsTitle, ArrayList<String>  notificationsText, ArrayList<String>  notificationsDate, ArrayList<Drawable>  images) {
        super(context, R.layout.custom_list, notificationsTitle);
        // TODO Auto-generated constructor stub

        this.mContext=context;
        this.notificationsTitle=notificationsTitle;
        this.notificationsText=notificationsText;
        this.notificationsDate=notificationsDate;
        this.images=images;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=rowView=inflater.inflate(R.layout.custom_list2, null, true);

        /*DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int imageWidth=(int)(dpWidth*20)/100;
        int titleWidth=((int)dpWidth*60)/100;
        int textWidth=(int)(dpWidth*80)/100;
        int dateWidth=(int)(dpWidth*20)/100;*/

        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        //imageView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));


        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                }
            }
        });*/
        Typeface myFontRegular=Typeface.createFromAsset(mContext.getAssets(),"fonts/PlayfairDisplay-Regular.ttf");

        TextView txtTitle = (TextView) rowView.findViewById(R.id.itemTitle);
        //txtTitle.setLayoutParams(new RelativeLayout.LayoutParams(titleWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));
        TextView txtDescription = (TextView) rowView.findViewById(R.id.itemDescription);
        //txtDescription.setLayoutParams(new RelativeLayout.LayoutParams(textWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));
        TextView txtDate = (TextView) rowView.findViewById(R.id.itemDate);
        //txtDate.setLayoutParams(new RelativeLayout.LayoutParams(dateWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));
        //txtDescription.setTypeface(myFontRegular);


        txtTitle.setText(notificationsTitle.get(position));
        txtDescription.setText(notificationsText.get(position));
        txtDate.setText(notificationsDate.get(position));
        imageView.setImageDrawable(images.get(position));
        return rowView;

    };
}
