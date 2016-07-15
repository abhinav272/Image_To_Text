package com.abhinavsharma.imagetotext.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abhinavsharma on 14/07/16.
 */
public class Preferences {

    private static final String RECENTS = "recents";
    private static final String itemsPreferences = "item_preferences";
    private static Preferences preferences;

    private Preferences() {
    }

    public static Preferences getInstance(){
        if (preferences == null) {
            preferences = new Preferences();
        }
        return preferences;
    }

//    public void setRecents(Context context, String jsonString){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(itemsPreferences,Context.MODE_APPEND);
//        sharedPreferences.edit().putString(RECENTS,jsonString).commit();
//    }
//
//    public String getRecents(Context context){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(itemsPreferences,Context.MODE_APPEND);
//        return sharedPreferences.getString(RECENTS,null);
//    }

    public void setImageTextData(Context context, String imagePath, String imageText){
        SharedPreferences sharedPreferences = context.getSharedPreferences(itemsPreferences, Context.MODE_APPEND);
        sharedPreferences.edit().putString(imagePath,imageText).commit();
    }

    public String getImageTextData(Context context, String imagePath){
        SharedPreferences sharedPreferences = context.getSharedPreferences(itemsPreferences, Context.MODE_APPEND);
        return sharedPreferences.getString(imagePath,null);
    }
}
