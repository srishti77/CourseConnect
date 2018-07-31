package com.pramod.courseconnect.activities;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.adapters.FullScreenImageAdapter;
import com.pramod.courseconnect.fragments.ViewListOfImagesFragment;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.io.File;
import java.util.ArrayList;

public class ViewFullImagesActivity extends AppCompatActivity {

    String imageName;
    String selectedEvent;
    ViewListOfImagesFragment fragment = new ViewListOfImagesFragment();
    CreateDirectories createDirectories = new CreateDirectories();
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview_slider);
        Intent intent = getIntent();
        imageName = intent.getStringExtra("ImageName");
        int position = intent.getIntExtra("position", -1);
        selectedEvent = intent.getStringExtra("Selected Event");
      //  final Drawable image = ViewListOfImagesFragment.imageDrawables.get(position);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new FullScreenImageAdapter(ViewFullImagesActivity.this, position);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.images_bottom_nav_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.shareItem:
                                shareImages(ViewFullImagesActivity.this.getContentResolver(), ViewListOfImagesFragment.imagePath.get(viewPager.getCurrentItem()));
                                break;
                            case R.id.deleteItem:
                                ArrayList<String> selectedImage = new ArrayList<String>();
                                selectedImage.add(ViewListOfImagesFragment.imageNames.get(viewPager.getCurrentItem()));
                                Log.i("hello", "delete");
                                fragment.deleteImages(null, selectedImage,selectedEvent);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), R.string.deleted_successfully,Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.rename:
                                renameImages(ViewListOfImagesFragment.imageNames.get(viewPager.getCurrentItem()));
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    public void shareImages(ContentResolver contentResolver, String path){

       /* Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        String path = MediaStore.Images.Media.insertImage(contentResolver,
                bitmap, "Design", null);
        Uri uri = Uri.parse(path);
*/
       File file = new File(path);
        Uri uri = FileProvider.getUriForFile(this, "com.pramod.courseconnect", file);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(shareIntent);

    }

        public void renameImages(final String oldFileName){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.new_file_name);
            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i("SelectedElement", imageName+" ");
                    Log.i("New Name",input.getText().toString());
                    String newFileName = input.getText().toString();
                    if(!newFileName.isEmpty()){
                      //  String oldFileName = ViewListOfImagesFragment.imageNames.get(viewPager.getCurrentItem());
                        if(createDirectories.isFileRenamed(oldFileName,newFileName, "Images", selectedEvent)){
                            //since file name is changed
                            if(!newFileName.contains(".jpg"))
                                newFileName= newFileName+".jpg";
                            ViewListOfImagesFragment.imageNames.set(ViewListOfImagesFragment.imageNames.indexOf(oldFileName),newFileName);
                            ViewListOfImagesFragment.arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), R.string.renamed_successfully,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


}
