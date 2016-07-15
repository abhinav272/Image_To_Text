package com.abhinavsharma.imagetotext.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by abhinavsharma on 15/07/16.
 */
public class Utils {

    public static final String BASE_FOLDER_NAME = "image_to_text_images";

    public static String saveImageToLocal(Bitmap bitmap){
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + BASE_FOLDER_NAME + File.separator;
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();

        String format = new SimpleDateFormat("yyyyMMddHHmmss",
                java.util.Locale.getDefault()).format(new Date());

        File file = new File(file_path + format + ".png");
        FileOutputStream fOut;
        try {
            if(!file.exists()) file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("saveImageToLocal: ", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static ArrayList<String> getAllFiles() throws NullPointerException{
        File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BASE_FOLDER_NAME);
        File[] files = rootFolder.listFiles();
        ArrayList<String> filesList = new ArrayList<>();
        for (File f : files) {
            if (f != null) {
                filesList.add(f.getAbsolutePath());
            }
        }
        return filesList;
    }


}
