package com.pramod.courseconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.pramod.courseconnect.activities.CCBaseActivity;
import com.pramod.courseconnect.activities.TakeNotesActivity;
import com.pramod.courseconnect.activities.ViewMaterialsActivity;
import com.pramod.courseconnect.adapters.NotesAdapter;
import com.pramod.courseconnect.adapters.RecordingsAdapter;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewListOfNotesFragment extends Fragment {

    CreateDirectories createDirectories = new CreateDirectories();
    public static List<String> notesName = new ArrayList<String>();
    ArrayList<String> selectedNotes = new ArrayList<String>();
    String selectedEvent;
    public static NotesAdapter arrayAdapter;
    ListView notesView;

    public ViewListOfNotesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState !=  null){
            selectedNotes = (ArrayList<String>) savedInstanceState.getSerializable("selectedNotes");
            selectedEvent = (String) savedInstanceState.getSerializable("selectedEvent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_view_list_of_notes, container, false);

        notesView = (ListView) view.findViewById(R.id.notesListView);
        arrayAdapter = new NotesAdapter(getActivity(), notesName);
        notesView.setAdapter(arrayAdapter);

        notesView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        notesView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                Log.i("Item change", b+"");
                Menu menu = actionMode.getMenu();
                if(b){
                    selectedNotes.add(notesName.get(i));
                }
                else{
                    selectedNotes.remove(notesName.get(i));
                }

                if(selectedNotes.size() == 1)
                {
                    menu.findItem(R.id.rename).setVisible(true);
                }
                else {
                    menu.findItem(R.id.rename).setVisible(false);
                }
                actionMode.setTitle(selectedNotes.size() + " "
                        +ViewListOfNotesFragment.this.getString(R.string.notes_selected));
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.action_bar_menu, menu);
                if(selectedNotes!= null && selectedNotes.size()>0){
                    actionMode.setTitle(selectedNotes.size() + " "+ ViewListOfNotesFragment.this.getString(R.string.notes_selected));
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
                        for (String noteName : selectedNotes) {
                            createDirectories.deleteFiles(selectedEvent + "/Notes", noteName);
                            notesName.remove(noteName);
                            arrayAdapter.notifyDataSetChanged();

                        }
                        Toast.makeText(getActivity(), selectedNotes.size() + " "+
                                        ViewListOfNotesFragment.this.getString(R.string.items_deleted)
                                , Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        return true;
                    case R.id.shareItem:
                        Log.i("Ready to share", "yes");
                        shareMultipleRecordings(actionMode);
                        return true;
                    case R.id.rename:
                        Log.i("Rename", "here");
                        renameNotes(actionMode);
                        return true;
                    default:
                        return false;

                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selectedNotes.clear();
            }
        });

        notesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TakeNotesActivity.class);
                Log.i("File Name:", notesName.get(i));
               // String notesContent = createDirectories.readContentOfNotesFile(selectedEvent + "/Notes/" + notesTitles.get(i)).toString();
                intent.putExtra("NotesName", notesName.get(i) + "");
                intent.putExtra("CurrentSelected", selectedEvent);
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

    public void shareMultipleRecordings(ActionMode actionMode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("text/*");

        ArrayList<Uri> files = new ArrayList<Uri>();

        for(String notes : selectedNotes) {
            Log.i("Notes Name", notes);
            File file = createDirectories.getFilePath(notes, selectedEvent, "Notes");
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.pramod.courseconnect", file);
            files.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
        actionMode.finish();
    }

    public void onSpinnerChanged(String event) {
        selectedEvent = event;
        notesName.clear();

        if (arrayAdapter != null) {
            arrayAdapter.clear();
            notesName.addAll(createDirectories.readAllDirectoryOrFileName(selectedEvent, "Notes"));
          /*  if(notesName.isEmpty()){
                ViewMaterialsActivity.loadingPanel.setVisibility(View.GONE);
            }*/
            arrayAdapter.notifyDataSetChanged();
            // AllMaterialFragment.notesBadge.setText(arrayAdapter.getCount() + "");
            }

        }


    public void renameNotes(final ActionMode actionMode){
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
                Log.i("SelectedElement", selectedNotes.get(0)+" ");
                Log.i("New Name",input.getText().toString());
                String newFileName = input.getText().toString();
                if(!newFileName.isEmpty()){
                    String oldFileName =selectedNotes.get(0);
                    if(createDirectories.isFileRenamed(oldFileName,newFileName, "Notes", selectedEvent)){
                        //since file name is changed
                       // selectedNotes.set(0, newFileName);
                        if(!newFileName.contains(".txt"))
                            newFileName= newFileName+".txt";
                        notesName.set(notesName.indexOf(oldFileName),newFileName);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("selectedEvent", selectedEvent);
        outState.putSerializable("selectedNotes", selectedNotes);

        //notesView.invalidateViews();
    }

    @Override
    public void onResume() {
        //onSpinnerChanged(selectedEvent);
        super.onResume();
    }
}
