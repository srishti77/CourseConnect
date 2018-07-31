package com.pramod.courseconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pramod.courseconnect.R;

import java.util.ArrayList;


public class EventsAdapter extends ArrayAdapter {

    Activity context;
    ArrayList<String> eventName = new ArrayList<String>();
    public EventsAdapter(Activity context, ArrayList<String> eventName) {
        super(context,  R.layout.for_events_list, eventName);
        this.context = context;
        this.eventName = eventName;

    }


    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.for_events_list, null, true);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.eventName);
            view.setTag(viewHolder);

        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(eventName.get(position));

        return view;
    }

    static class ViewHolder {
        private TextView textView;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

}
