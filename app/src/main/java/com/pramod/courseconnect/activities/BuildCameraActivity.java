package com.pramod.courseconnect.activities;


import android.Manifest;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.pramod.courseconnect.R;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import java.util.Date;

public class BuildCameraActivity extends CCBaseActivity  {

    CameraView cameraView;
    ImageView captureImage, flashLight, rotateCamera;
    byte[] saveImages = null;
    boolean backCamera = true;
    boolean flashOff = true;

    CreateDirectories createDirectories = new CreateDirectories();
    String selectedLanguage;

    String permissions[] ={
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BuildCameraActivity.this);
        if(CCMainActivity.sharedPreferences!= null){
            selectedLanguage = CCMainActivity.sharedPreferences.getString("preferredLanguage", null);
            if(selectedLanguage != null){
                setLocale(selectedLanguage);
            }
        }

        setContentView(R.layout.activity_build_camera);
        cameraView = (CameraView) findViewById(R.id.camera);
        captureImage = (ImageView) findViewById(R.id.buttonToTakeImage);
        flashLight = (ImageView) findViewById(R.id.flash);
        rotateCamera = (ImageView) findViewById(R.id.rotate);

        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER);
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE);
        cameraView.setPlaySounds(false);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                //_bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 40, picture);
                /*Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, blob);
                byte[] bitmapdata = blob.toByteArray();
                saveImages = bitmapdata;
                saveImage(bitmapdata);                */

                saveImages = picture;
                saveImage(picture);
            }

        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.capturePicture();
            }
        });
        flashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flashOff)
                {
                    cameraView.setFlash(Flash.ON);
                    flashLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_black_24dp));
                    flashOff= false;
                }
                else{
                    cameraView.setFlash(Flash.OFF);
                    flashLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_black_24dp));
                    flashOff= true;
                }
            }
        });

        rotateCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(backCamera)
                {
                    cameraView.setFacing(Facing.FRONT);
                    backCamera= false;
                }
                else{
                    cameraView.setFacing(Facing.BACK);
                    backCamera= true;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions(permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("OnResume", "called");

        if(hasPermissions(this, permissions)){
            cameraView.start();

            if(CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()  ){
                goToSettingsPage();
            }

            else{
                selectedCurrentEvent();
                Log.i("Refereshed", "ref");
                if(saveImages!= null && saveImages.length > 0 && !selectedCurrentEvent.isEmpty()){
                    saveImage(saveImages);
                }
            }
        }

        //get the current event
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
        if(alertForSettingsConfig!= null)
            alertForSettingsConfig.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    public void saveImage(final byte[] bytes) {
        File mediaStorageDir;
        Log.i("Saved", "Images");
        //check if the current event- exist or not
       if(CCMainActivity.selectedCalendars!= null && !CCMainActivity.selectedCalendars.isEmpty()){

            if (selectedCurrentEvent != null && !selectedCurrentEvent.isEmpty()) {
                Log.i("GetOutput", selectedCurrentEvent);
                String directory = selectedCurrentEvent + "/" + "Images";
                mediaStorageDir = createDirectories.createFolder(directory);
                createDirectories.createNoMediaFile();
                saveImageInLocation(mediaStorageDir);
                //.fill(saveImages, null)
            }
            else{
                createEvent();
            }
       }
        else{
            goToSettingsPage();
        }

    }

    public void saveImageInLocation(File directory){
        if (directory != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(directory.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
            MediaScannerConnection.scanFile(BuildCameraActivity.this, new String[]{mediaFile.toString()}, null, null);
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(mediaFile);
                outputStream.write(saveImages);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), this.getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), this.getString(R.string.could_not_save), Toast.LENGTH_SHORT).show();
        }
        saveImages = null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.newNotes).setVisible(false);
        menu.findItem(R.id.save).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

           case R.id.folder:
                Intent intent = new Intent(BuildCameraActivity.this, ViewMaterialsActivity.class);
                intent.putExtra("SelectedEvent", selectedCurrentEvent);
                intent.putExtra("viewpager_position", 0);
                startActivity(intent);
                break;



            case R.id.reload:
                selectedCurrentEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
