package com.pramod.courseconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.ViewMaterialsActivity;
import com.pramod.courseconnect.adapters.RecordingsAdapter;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewListOfRecordingsFragment extends Fragment {

    CreateDirectories createDirectories = new CreateDirectories();
    public static List<String> recordingsName = new ArrayList<String>();
    ArrayList<String> selectedRecordings = new ArrayList<String>();
    String selectedEvent;
    public static RecordingsAdapter arrayAdapter;
    ListView recordView;
    boolean onPlayStart = true;
    MediaPlayer mediaPlayer;
    public ViewListOfRecordingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState !=  null){
            selectedRecordings = (ArrayList<String>) savedInstanceState.getSerializable("selectedRecordings");
            selectedEvent = (String) savedInstanceState.getSerializable("selectedEvent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_view_list_of_recordings, container, false);
        recordView = (ListView) view.findViewById(R.id.recordingsListView);
        arrayAdapter = new RecordingsAdapter(getActivity(), recordingsName);
        recordView.setAdapter(arrayAdapter);
        recordView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        recordView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                Log.i("Item change", b+"");
                Menu menu = actionMode.getMenu();
                if(b){
                    selectedRecordings.add(recordingsName.get(i));
                }
                else{
                    selectedRecordings.remove(recordingsName.get(i));
                }
                if(selectedRecordings.size() == 1)
                {
                    menu.findItem(R.id.rename).setVisible(true);
                }
                else {
                    menu.findItem(R.id.rename).setVisible(false);
                }
                actionMode.setTitle(selectedRecordings.size() + " "+
                        ViewListOfRecordingsFragment.this.getString(R.string.audios_selected));

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.action_bar_menu, menu);
                if(selectedRecordings!= null && selectedRecordings.size()>0){
                    actionMode.setTitle(selectedRecordings.size() + " "+ ViewListOfRecordingsFragment.this.getString(R.string.audios_selected));
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.deleteItem:
                        for (String recordName : selectedRecordings) {

                            createDirectories.deleteFiles(selectedEvent + "/Recordings", recordName);
                            recordingsName.remove(recordName);
                            arrayAdapter.notifyDataSetChanged();

                        }
                        Toast.makeText(getActivity(), selectedRecordings.size() + " "+
                                        ViewListOfRecordingsFragment.this.getString(R.string.items_deleted),
                                Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        return true;
                    case R.id.shareItem:
                        Log.i("Ready to share", "yes");
                        shareMultipleRecordings(actionMode);
                        return true;
                    case R.id.rename:
                        Log.i("Rename", "here");
                        renameRecordings(actionMode);
                        return true;
                    default:
                        return false;

                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selectedRecordings.clear();
            }
        });

        recordView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onPlayStart = true;

                letsPlay(view, i, recordingsName.get(i));
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

        if (arrayAdapter != null) {
            Log.i("Inside", "Recorder");
            recordingsName.clear();
            arrayAdapter.clear();
            recordingsName.addAll(createDirectories.readAllDirectoryOrFileName(selectedEvent,
                    "Recordings"));
        /*    if(recordingsName.isEmpty()){

                ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);
            }*/
            Log.i("Recordings: ", recordingsName.size()+"");

            this.arrayAdapter.notifyDataSetChanged();

        }

    }

    public void shareMultipleRecordings(ActionMode actionMode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("audio/*");

        ArrayList<Uri> files = new ArrayList<Uri>();
        if(selectedRecordings!= null)
            Log.i("Selected Audio", selectedRecordings.size()+"");
        for(String recording : selectedRecordings) {
            File file = createDirectories.getFilePath(recording, selectedEvent, "Recordings");
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.pramod.courseconnect", file);
            files.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
        actionMode.finish();
    }

    public void renameRecordings(final ActionMode actionMode){
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
                Log.i("SelectedElement", selectedRecordings.get(0)+" ");
                Log.i("New Name",input.getText().toString());
                String newFileName = input.getText().toString();
                if(!newFileName.isEmpty()){
                    String oldFileName =selectedRecordings.get(0);
                    if(createDirectories.isFileRenamed(oldFileName,newFileName,
                            "Recordings", selectedEvent)){
                        //since file name is changed
                        if(!newFileName.contains(".wav"))
                            newFileName= newFileName+".wav";
                        recordingsName.set(recordingsName.indexOf(oldFileName),newFileName);
                        arrayAdapter.notifyDataSetChanged();
                        actionMode.finish();
                    }
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        // onSpinnerChanged(selectedEvent);
        super.onResume();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedEvent", selectedEvent);
        outState.putSerializable("selectedRecordings", selectedRecordings);
    }

    public void letsPlay(View view, int i, String recording ){


        view = getLayoutInflater().inflate(R.layout.activity_recordings_play, null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(view);

        TextView recordingName = (TextView) view.findViewById(R.id.Name_Of_Recordings);
        recordingName.setText(recording);

        final ImageView playOrpause = (ImageView) view.findViewById(R.id.play_pause_recording);
        final ImageView close = (ImageView) view.findViewById(R.id.close);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        final TextView timeOfSeekBar = (TextView) view.findViewById(R.id.timeOfSeekBar);
        bottomSheetDialog.show();
        mediaPlayer = new MediaPlayer();
        String outputFile = Environment
                .getExternalStorageDirectory()
                +"/CourseConnect/" + selectedEvent
                + "/Recordings/" + recording;
        Log.i("Output:", outputFile);

        try{

            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();

        }
        catch (Exception e){

            e.printStackTrace();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMediaPlayer();
                bottomSheetDialog.cancel();
            }
        });
        playOrpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlayStart) {
                    playOrpause.setImageResource(R.drawable.ic_pause_black_24dp);
                    onPlayStart = false;
                    mediaPlayer.start();
                    try{
                        seekBar.setMax(mediaPlayer.getDuration() / 1000);
                    }
                    catch(Exception e){
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                    }
                    final Handler handler = new Handler();

                    Runnable updateSeekBar = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                seekBar.setProgress(currentPosition);
                                int secsCurr = currentPosition;
                                int minsCurr = secsCurr/60;
                                secsCurr = secsCurr % 60;
                                int hoursCurr = minsCurr/60;
                                int secsTotal = (int) (mediaPlayer.getDuration()/1000);
                                int minsTotal = secsTotal/60;
                                secsTotal = secsTotal % 60;
                                int hoursTotal = minsTotal/60;
                                timeOfSeekBar.setText(String.format("%02d", hoursCurr)+":"
                                        + String.format("%02d", minsCurr)
                                        +":"
                                        +String.format("%02d", secsCurr) + "/" +String.format("%02d", hoursTotal)+":"
                                        + String.format("%02d", minsTotal)
                                        +":"+String.format("%02d", secsTotal));
                                if(currentPosition == (mediaPlayer.getDuration() / 1000) ){
                                    playOrpause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                    onPlayStart = true;
                                }

                            }
                            handler.postDelayed(this, 0);
                        }
                    };
                    handler.postDelayed(updateSeekBar, 0);

                }
                else {

                    mediaPlayer.pause();
                    playOrpause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    onPlayStart = true;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("Progress", "Changed");
                if(mediaPlayer!= null && b){
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                closeMediaPlayer();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
               closeMediaPlayer();
            }
        });

    }

    public void closeMediaPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

}

