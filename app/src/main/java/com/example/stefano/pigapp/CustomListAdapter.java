package com.example.stefano.pigapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Drawable[] images;

    public CustomListAdapter(Activity context, String[] itemname, Drawable[] images) {
        super(context, R.layout.custom_list, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.images=images;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_list, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtDescription = (TextView) rowView.findViewById(R.id.itemDescription);

        txtDescription.setText(Html.fromHtml(itemname[position]));
        imageView.setImageDrawable(images[position]);
        return rowView;

    };
}
