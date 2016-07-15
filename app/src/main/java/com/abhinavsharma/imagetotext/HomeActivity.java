package com.abhinavsharma.imagetotext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.abhinavsharma.imagetotext.adapters.CustomListViewAdapter;
import com.abhinavsharma.imagetotext.helper.Preferences;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by abhinavsharma on 14/07/16.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private Button btnCamera;
    private ListView lvAllText;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private AlertDialog alertDialog;
    private ArrayList al;
    private CustomListViewAdapter customListViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
    }

    private void initializeViews() {
        btnCamera = (Button) findViewById(R.id.camera);
        lvAllText = (ListView) findViewById(R.id.lv_all_text);
        String recents = Preferences.getInstance().getRecents(getApplicationContext());
        al = new Gson().fromJson(recents,ArrayList.class);
        if (al != null) {
            customListViewAdapter = new CustomListViewAdapter(this,al);
            lvAllText.setAdapter(customListViewAdapter);
        }
        btnCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            new MyAsyncTask(this).execute(extras);
        }
    }

    class MyAsyncTask extends AsyncTask<Bundle, Void, Void> {

        private Context mContext;
        private ArrayList<String> al;

        public MyAsyncTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Bundle... params) {
            Bitmap imageBitmap = (Bitmap) params[0].get("data");
//            mImageView.setImageBitmap(imageBitmap);

            Frame frame = new Frame.Builder()
                    .setBitmap(imageBitmap).build();

            TextRecognizer textRecognizer = new TextRecognizer.Builder(mContext).build();
            if (!textRecognizer.isOperational()) {
                Log.w(TAG, "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(mContext, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                    Log.w(TAG, getString(R.string.low_storage_error));
                }
            }

            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
            if (textBlockSparseArray != null) {
                al = new ArrayList<>();
                for (int i = 0; i < textBlockSparseArray.size(); i++) {
                    if (textBlockSparseArray.valueAt(i).getValue() != null) {
                        al.add(textBlockSparseArray.valueAt(i).getValue());
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (al != null && al.size() > 0) {
                Intent intent = new Intent(HomeActivity.this, TextViewer.class);
                intent.putExtra("all_text", al);
                startActivity(intent);
            } else {
                showNoDataDialog();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void showNoDataDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("No Data Captured")
                .setMessage("Try to capture bigger characters !!")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .create();

        alertDialog.show();
    }
}
