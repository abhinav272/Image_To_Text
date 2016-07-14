package com.abhinavsharma.imagetotext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.abhinavsharma.imagetotext.adapters.CustomListViewAdapter;
import com.abhinavsharma.imagetotext.helper.Preferences;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by abhinavsharma on 14/07/16.
 */
public class TextViewer extends AppCompatActivity {

    private ArrayList<String> al;
    private ListView lvDisplay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_viewer);
        initializeViews();
    }

    private void initializeViews() {
        al = getIntent().getStringArrayListExtra("all_text");
        if (al != null && al.size() > 0) {
            lvDisplay = (ListView) findViewById(R.id.lv_image_all_text);
            CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(this, al);
            lvDisplay.setAdapter(customListViewAdapter);
            new UpdatePreferencesTask(getApplicationContext()).execute(al);
        }
    }

    public class UpdatePreferencesTask extends AsyncTask<ArrayList<String>, Void, Void>{

        private Context mContext;

        public UpdatePreferencesTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            String jsonText = new Gson().toJson(al);
            Preferences.getInstance().setRecents(mContext,jsonText);
            return null;
        }
    }
}
