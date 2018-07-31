package com.pramod.courseconnect.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.ReminderListActivity;

import java.util.ArrayList;

/**
 * Created by User on 27/05/2018.
 */
import com.pramod.courseconnect.models.Reminder;

public class ReminderListAdapter  extends ArrayAdapter {

    private ArrayList<Reminder> mItems;
    ReminderListActivity context;

    public ReminderListAdapter(@NonNull ReminderListActivity context, ArrayList<Reminder> reminderItems) {
        super(context, R.layout.item_reminder_list, reminderItems);
        this.mItems = reminderItems;
        this.context = context;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_reminder_list, null, true);
            viewHolder = new ViewHolder();
            viewHolder.mTitleText = (TextView) view.findViewById(R.id.recycle_title);
            viewHolder.mDateAndTimeText = (TextView) view.findViewById(R.id.recycle_date_time);
            viewHolder.mRepeatInfoText = (TextView) view.findViewById(R.id.recycle_repeat_info);
            viewHolder.mActiveImage = (ImageView) view.findViewById(R.id.active_image);


            view.setTag(viewHolder);

        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Reminder item = mItems.get(position);
        viewHolder.mTitleText.setText(item.getrTitle());
        viewHolder.mDateAndTimeText.setText(item.getrDate()+ " "+item.getrTime());

        if(item.getrRepeat().equals("true")){
            viewHolder.mRepeatInfoText.setText(context.getString(R.string.every)+" "+ item.getrRepeatNo() + " "
                    + item.getrRepeatType() + context.getString(R.string.plural_end));
        }else if (item.getrRepeat().equals("false")) {
            viewHolder.mRepeatInfoText.setText(R.string.repeat_off);
        }
        if(item.getrActive().equals("true")){
            viewHolder.mActiveImage.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        }else if (item.getrActive().equals("false")) {
            viewHolder.mActiveImage.setImageResource(R.drawable.ic_notifications_off_black_24dp);
        }
        //remove the progress bar when loading is done
        //ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);

        return view;
    }

    static class ViewHolder {
        private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
        private ImageView mActiveImage;
        private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        private TextDrawable mDrawableBuilder;
    }

}
