package com.example.stefano.pigapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity mContext;
    private final String[] itemname;
    private final Drawable[] images;
    private final boolean small;

    public CustomListAdapter(Activity context, String[] itemname, Drawable[] images, boolean small) {
        super(context, R.layout.custom_list, itemname);
        // TODO Auto-generated constructor stub

        this.mContext=context;
        this.itemname=itemname;
        this.images=images;
        this.small=small;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_list, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Typeface myFontRegular=Typeface.createFromAsset(mContext.getAssets(),"fonts/PlayfairDisplay-Regular.ttf");

        TextView txtDescription = (TextView) rowView.findViewById(R.id.itemDescription);
        //txtDescription.setTypeface(myFontRegular);

        if(small){
            imageView.getLayoutParams().height = 100;
            imageView.getLayoutParams().width = 100;
            txtDescription.setTextSize(20);
        }

        txtDescription.setText(Html.fromHtml(itemname[position]));
        imageView.setImageDrawable(images[position]);
        return rowView;

    };
}
