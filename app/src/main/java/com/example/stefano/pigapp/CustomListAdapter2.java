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
    private final ArrayList<Drawable>  status;
    private boolean isImageFitToScreen=false;


    public CustomListAdapter2(Activity context, ArrayList<String> notificationsTitle, ArrayList<String>  notificationsText, ArrayList<String>  notificationsDate, ArrayList<Drawable>  images, ArrayList<Drawable>  status) {
        super(context, R.layout.custom_list, notificationsTitle);
        // TODO Auto-generated constructor stub

        this.mContext=context;
        this.notificationsTitle=notificationsTitle;
        this.notificationsText=notificationsText;
        this.notificationsDate=notificationsDate;
        this.status=status;
        this.images=images;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=rowView=inflater.inflate(R.layout.custom_list2, null, true);


        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        final ImageView statusView = (ImageView) rowView.findViewById(R.id.status);


        TextView txtTitle = (TextView) rowView.findViewById(R.id.itemTitle);
        TextView txtDescription = (TextView) rowView.findViewById(R.id.itemDescription);
        TextView txtDate = (TextView) rowView.findViewById(R.id.itemDate);



        txtTitle.setText(notificationsTitle.get(position));
        txtDescription.setText(notificationsText.get(position));
        txtDate.setText(notificationsDate.get(position));
        imageView.setImageDrawable(images.get(position));
        statusView.setImageDrawable(status.get(position));

        return rowView;

    };
}
