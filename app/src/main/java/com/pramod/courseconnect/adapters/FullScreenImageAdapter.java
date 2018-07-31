package com.pramod.courseconnect.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jsibbold.zoomage.ZoomageView;
import com.pramod.courseconnect.R;
import com.pramod.courseconnect.fragments.ViewListOfImagesFragment;
import com.pramod.courseconnect.models.GlideApp;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 17/04/2018.
 */

public class FullScreenImageAdapter extends PagerAdapter {

    Activity activity;
    int position;
    LayoutInflater inflater;
    String pathFile;
    public FullScreenImageAdapter(Activity activity,
                                  int position) {
        this.activity = activity;
        this.position = position;

    }

    @Override
    public int getCount() {
      // notifyDataSetChanged();
       /* if(ViewListOfImagesFragment.dataChanged){
            notifyDataSetChanged();
            ViewListOfImagesFragment.dataChanged = false;
        }*/

        return ViewListOfImagesFragment.imageNames.size();
    }
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) activity.getLayoutInflater();
        View viewLayout = inflater.inflate(R.layout.activity_view_full_images, container, false);
        ZoomageView imageView = (ZoomageView) viewLayout.findViewById(R.id.fullScreenImage);
       // final Drawable image = ViewListOfImagesFragment.path.get(position);
        if(position != -1){
            pathFile = ViewListOfImagesFragment.imagePath.get(position);
            File file = new File(pathFile);
            Uri imageUri = Uri.fromFile(file);

            //imageView.setImageDrawable(image); ViewListOfImagesFragment.imageDrawables.get(position);

            GlideApp.with(activity)
                    .load(imageUri)
                    .fitCenter()
                    .into( imageView);
        }


        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}

