package com.example.dtrebula.bbc2;


import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowAllEventActivity extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] eventNames;
    private final Bitmap[] imageList;
    private final Integer[] idsOfEvent;

    public ShowAllEventActivity(Activity context, String[] eventNames, Bitmap[] imageList, Integer[] idsOfEvent) {
        super(context, R.layout.activity_show_all_events, eventNames);
        this.context = context;
        this.eventNames = eventNames;
        this.imageList = imageList;
        this.idsOfEvent = idsOfEvent;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.activity_show_all_events, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText("" + " " + eventNames[position]);
        imageView.setImageBitmap(imageList[position]);
        return rowView;
    }
}