package com.pramod.courseconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.ViewFullImagesActivity;
import com.pramod.courseconnect.activities.ViewMaterialsActivity;
import com.pramod.courseconnect.adapters.ImageAdapter;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewListOfImagesFragment extends Fragment{

    View view;

    public static List<String> imageNames = new ArrayList<String>();
    public static List<String> imagePath = new ArrayList<String>();
   // Drawable drawableImage;
    public static ImageAdapter arrayAdapter;
    ArrayList<String> selectedImages = new ArrayList<String>();
    ActionMode actionModeSave;
    CreateDirectories createDirectories = new CreateDirectories();
    String selectedEvent;
    GridView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState !=  null){
            selectedImages = (ArrayList<String>) savedInstanceState.getSerializable("selectedImages");
            selectedEvent = (String) savedInstanceState.getSerializable("selectedEvent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_list_of_images, container, false);
        recyclerView = (GridView) view.findViewById(R.id.recycleView);

        arrayAdapter = new ImageAdapter(getActivity(), imagePath);

        recyclerView.setAdapter(arrayAdapter);

        recyclerView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        recyclerView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                Log.i("Item change", b+"");
                actionModeSave = actionMode;
                Menu menu = actionMode.getMenu();
                if(b){
                    selectedImages.add(imageNames.get(i));
                }
                else{
                    selectedImages.remove(imageNames.get(i));
                }

                if(selectedImages.size() == 1)
                {
                    menu.findItem(R.id.rename).setVisible(true);
                }
                else {
                    menu.findItem(R.id.rename).setVisible(false);
                }

                actionMode.setTitle(selectedImages.size() + " "+ ViewListOfImagesFragment.this.getString(R.string.images_selected));
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.action_bar_menu, menu);

                if(selectedImages!= null && selectedImages.size()>0){
                    actionMode.setTitle(selectedImages.size() + " "+ ViewListOfImagesFragment.this.getString(R.string.images_selected));
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.deleteItem:
                        deleteImages(actionMode, selectedImages, selectedEvent);
                        return true;
                    case R.id.shareItem:
                        Log.i("Ready to share", "yes");
                        shareMultipleImage(actionMode);
                        return true;
                    case R.id.rename:
                        Log.i("Rename", "here");
                        renameImages(actionMode);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selectedImages.clear();
            }
        });

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("OnClick", "called");
                Intent intent = new Intent(getActivity(), ViewFullImagesActivity.class);
                intent.putExtra("position", i);
                Bundle b=new Bundle();
               /* b.putStringArray("ImageNames", new String[](imageNames));
                intent.putExtra("ImageNames", imageNames);*/
                intent.putExtra("Selected Event", selectedEvent);
                intent.putExtra("ImageName", imageNames.get(i));
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onSpinnerChanged(String event) {
        selectedEvent = event;
        Log.i("Inside Images", "spinner");
        if (arrayAdapter != null) {
            imageNames.clear();

            imagePath.clear();
            arrayAdapter.clear();
            Log.i("Spinner", "spinner is changed");
            imageNames = createDirectories.readAllDirectoryOrFileName(selectedEvent, "Images");
            Log.i("No of image", imageNames.size()+"");
             for (int j = 0; j < imageNames.size(); j++) {
                //imageDrawables.add(Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/CourseConnect/"+selectedEvent+"/Images/" + imageNames.get(j)));
                imagePath.add(Environment.getExternalStorageDirectory() + "/CourseConnect/"+selectedEvent+"/Images/" + imageNames.get(j));

             }
          /* if(imageNames.size()<1){
              ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);
            }
            */
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void shareMultipleImage(ActionMode actionMode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/jpeg");

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(String image : selectedImages) {
            File file = createDirectories.getFilePath(image, selectedEvent, "Images");
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.pramod.courseconnect", file);
            files.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
        actionMode.finish();
    }

    public void deleteImages(ActionMode actionMode, ArrayList<String> selectedImages, String selectedEvent ){
        for(String imageName:  selectedImages){
          Log.i("selected Image", selectedEvent );
            createDirectories.deleteFiles(selectedEvent+"/Images", imageName);
           // imageDrawables.remove(imageNames.indexOf(imageName));
            imagePath.remove(imageNames.indexOf(imageName));
            imageNames.remove(imageName);
            //Toast.makeText(getActivity(), selectedImages.size() +" "+ViewListOfImagesFragment.this.getString(R.string.items_deleted), Toast.LENGTH_SHORT).show();


        }
        arrayAdapter.notifyDataSetChanged();
        selectedImages.clear();
        if(actionMode !=null)
        actionMode.finish();
    }

    public void renameImages(final ActionMode actionMode){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.new_file_name);
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("SelectedElement", selectedImages.get(0)+" ");
                Log.i("New Name",input.getText().toString());
                String newFileName = input.getText().toString();
                if(!newFileName.isEmpty()){
                    String oldFileName =selectedImages.get(0);
                    if(createDirectories.isFileRenamed(oldFileName,newFileName, "Images", selectedEvent)){
                        //since file name is changed
                        if(!newFileName.contains(".jpg"))
                            newFileName= newFileName+".jpg";
                        imageNames.set(imageNames.indexOf(oldFileName),newFileName);
                        arrayAdapter.notifyDataSetChanged();
                        if(actionMode!= null)
                            actionMode.finish();
                    }
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedEvent", selectedEvent);
        outState.putSerializable("selectedImages", selectedImages);

    }

    @Override
    public void onResume() {
        Log.i("On Resume", "called");
     /*   if(imageNames.size()<1){
            Log.i("Inside", "create");
            ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);
        }
*/
        super.onResume();
    }
}
