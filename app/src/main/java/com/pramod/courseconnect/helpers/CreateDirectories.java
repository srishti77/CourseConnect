package com.pramod.courseconnect.helpers;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;


public class CreateDirectories {

    private String appDirName = "CourseConnect" + "/";

    //So that no external app can read the content
    public File createFile(String directory, String fileName) {
        File file = null;
        try {

            file = new File(directory + "/" + fileName);

            if (!file.exists())
                file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public File createNoMediaFile() {
        File file = null;
        try {

            file = new File(Environment.getExternalStorageDirectory(), appDirName + ".nomedia");

            if (!file.exists())
                file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //Directory name should be full name of the file, after the main DCIM directory

    /**
     * Create the folder if it doesn't exist. If exists return the dir path
     **/
    public File createFolder(String directoryName) {
        //
        directoryName = appDirName.concat(directoryName);

        //Initialize the path to the APP directory (Worst case it still doesn't throw an exception, stores the file in the app dir)
        File mediaStorageDir = new File(directoryName);
        try {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);

            if (!mediaStorageDir.exists()) {

                mediaStorageDir.mkdirs();
                Log.i("Directory created", directoryName);
            } else {
                Log.i("Directory exist", directoryName);
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), "Couldn't find the directory specified/ Couldn't create the dir, returning the app dir");
        }
        return mediaStorageDir;
    }
    /*
    This method can be used to read all the folder's content
    It will return Folders if the main folder = "Course Connect"
    It will return files in case specific event/directory name is passed
     */

    public List readAllDirectoryOrFileName(String event, String materialType) {
        List<String> listOfFolderContent = new ArrayList<String>();
        File mediaStorageDir = null;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (event == null) {
                    mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appDirName);
                } else if (event != null && materialType != null) {
                    mediaStorageDir = new File(
                            Environment
                                    .getExternalStorageDirectory(), appDirName + event + "/" + materialType);
                }
                listOfFolderContent.clear();
                if (mediaStorageDir.exists()) {

                    for (File fileName : mediaStorageDir.listFiles()) {
                        if (!fileName.getName().equals(".nomedia"))
                            listOfFolderContent.add(fileName.getName());

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return listOfFolderContent;
    }

    public void deleteFiles(String directory, String fileName) {

        String state = Environment.getExternalStorageState();
        File mediaStorageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appDirName + directory);
            for (File file : mediaStorageDir.listFiles()) {
                if (fileName.equals(file.getName())) {
                    file.delete();
                }

            }
        }
    }

    public void deleteFolders(ArrayList<String> directoryName) {

        String state = Environment.getExternalStorageState();
        File mediaStorageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "CourseConnect");
            for (File file : mediaStorageDir.listFiles()) {
                if (directoryName.contains(file.getName())) {
                    Log.i("Deleted", file.getName());
                    try{

                        for(File materialsFolder: file.listFiles()){
                            deleteDir(materialsFolder);

                        }
                        file.delete();
                    }
                    catch(Exception e){
                        Log.i("Exception", "---------------------------");
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public File getFilePath(String fileName, String selectedEvent, String materialType) {

        File directory = null;
        try {
            directory = new File(Environment.getExternalStorageDirectory(), appDirName + selectedEvent + "/" + materialType + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory;
    }

    public StringBuffer readContentOfNotesFile(String file, String selectedEvent) {

        StringBuffer stringBuffer = null;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {


                Log.i("Full file Name", file);
                File fileName = new File(Environment.getExternalStorageDirectory(),
                        appDirName + selectedEvent + "/Notes/" + file);

                FileInputStream fileInputStream = new FileInputStream(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                stringBuffer = new StringBuffer();
                int ch;
                while ((ch = inputStreamReader.read()) != -1) {
                    stringBuffer.append((char) ch);
                }


            }
        } catch (Exception e1) {
            Log.i("No such file exists", "no");
            e1.printStackTrace();
        }
        return stringBuffer;
    }

    public boolean isFileRenamed(String originalFileName, String newFileName, String materialType, String selectedEvent) {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File oldFile = new File(Environment.getExternalStorageDirectory(),
                    appDirName + selectedEvent + "/" + materialType + "/" + originalFileName);

            if (oldFile.exists()) {
                File newFile= null;
                Log.i("Inside Rename", "--");
                if(materialType.equals("Notes")){
                    if(!newFileName.contains(".txt"))
                        newFileName = newFileName+".txt";
                }
                else if(materialType.equals("Images")){
                    if(!newFileName.contains(".jpg"))
                    newFileName = newFileName+".jpg";
                }
                else if(materialType.equals("Recordings")){
                    if(!newFileName.contains(".wav"))
                    newFileName = newFileName+".wav";
                }

                newFile = new File(Environment.getExternalStorageDirectory(),
                        appDirName + selectedEvent + "/" + materialType + "/" + newFileName);

                oldFile.renameTo(newFile);
                return true;
            }
        }
        return false;
    }
}
