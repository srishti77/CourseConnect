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
import com.pramod.courseconnect.activities.ViewMaterialsActivity;

import java.util.ArrayList;
import java.util.List;

public class RecordingsAdapter extends ArrayAdapter {

    List<String> recordingsName = new ArrayList<String>();
    Activity context;
    public RecordingsAdapter(@NonNull Activity context, List<String> recordingsName) {
        super(context, R.layout.for_each_recordings_list_item, recordingsName);
        this.recordingsName = recordingsName;
        this.context = context;
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.for_each_recordings_list_item, null, true);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.recordIcon);
            viewHolder.textView = (TextView) view.findViewById(R.id.recordName);
            view.setTag(viewHolder);

        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(recordingsName.get(position).replace(".wav", ""));
        //remove the progress bar when loading is done
        //ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);

        return view;
    }

    static class ViewHolder {
        private ImageView imageView;
        private TextView textView;

    }
}
