package com.pramod.courseconnect.adapters;

import android.app.Activity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.ViewMaterialsActivity;
import com.pramod.courseconnect.fragments.ViewListOfImagesFragment;
import com.pramod.courseconnect.models.GlideApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends ArrayAdapter {
    Activity context;

    //private List<Drawable> imageDrawable = new ArrayList<Drawable>();
    private List<String> imagePath = new ArrayList<String>();

    /* public ImageAdapter( Activity context, List<Drawable> imageDrawable) {
        super(context,  R.layout.for_each_image_list_item, imageDrawable);
        this.context = context;
        this.imageDrawable = imageDrawable;
    }
*/
   public ImageAdapter( Activity context, List<String> imagePath) {
       super(context,  R.layout.for_each_image_list_item, imagePath);
       this.context = context;
       this.imagePath = imagePath;
   }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.for_each_image_list_item, null, true);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
            view.setTag(viewHolder);


        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        //viewHolder.imageView.setImageDrawable(imageDrawable.get(position));
        File file = new File(imagePath.get(position));
        Uri imageUri = Uri.fromFile(file);

      GlideApp.with(context)
                .load(imageUri)
                .fitCenter()
               .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into( viewHolder.imageView);

     //   ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);
        return view;
    }

    static class ViewHolder {
        private ImageView imageView;
    }

}
