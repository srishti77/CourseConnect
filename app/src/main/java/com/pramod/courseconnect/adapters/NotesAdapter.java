package com.pramod.courseconnect.adapters;

import android.app.Activity;
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

public class NotesAdapter extends ArrayAdapter {

    private Activity context;
    private List<String> notesName = new ArrayList<String>();

    public NotesAdapter(@NonNull Activity context, List<String> notesName) {
        super(context,  R.layout.for_each_notes_list_item, notesName);
        this.context = context;
        this.notesName = notesName;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.for_each_notes_list_item, null, true);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.notesName);
            view.setTag(viewHolder);

        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(notesName.get(position).replace(".txt", ""));
       // ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);
        return view;
    }

    static class ViewHolder {

        private TextView textView;
    }
}
