package com.abhinavsharma.imagetotext;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.abhinavsharma.imagetotext.adapters.CustomListViewAdapter;
import com.abhinavsharma.imagetotext.adapters.CustomListViewAdapterIV;
import com.abhinavsharma.imagetotext.helper.Preferences;
import com.abhinavsharma.imagetotext.helper.Utils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by abhinavsharma on 14/07/16.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "HomeActivity";
    private Button btnCamera;
    private ListView lvAllText;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private AlertDialog alertDialog;
    private ArrayList al;
    private CustomListViewAdapterIV customListViewAdapter;
    private Bundle extras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
    }

    private void requestStoragePermission() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }else {
                if (extras != null) {
                    new MyAsyncTask(this).execute(extras);
                }
            }
        } else {
            if (extras != null) {
                new MyAsyncTask(this).execute(extras);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            al = Utils.getAllFiles();
        }catch (Exception e){

        }
        if (al != null) {
            customListViewAdapter = new CustomListViewAdapterIV(this, al);
            lvAllText.setAdapter(customListViewAdapter);
            lvAllText.setOnItemClickListener(this);
        }
    }

    private void initializeViews() {
        btnCamera = (Button) findViewById(R.id.camera);
        lvAllText = (ListView) findViewById(R.id.lv_all_text);
//        String recents = Preferences.getInstance().getRecents(getApplicationContext());
//        al = new Gson().fromJson(recents,ArrayList.class);

        btnCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                    if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.CAMERA},0);
                    }
                    else {
                        shootPhotoIntent();
                    }

                } else {
                    shootPhotoIntent();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                shootPhotoIntent();
            } else Toast.makeText(HomeActivity.this, "You did not gave the CAMERA Permission", Toast.LENGTH_SHORT).show();
        }

        if(requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (extras != null) {
                    new MyAsyncTask(this).execute(extras);
                }
            } else Toast.makeText(HomeActivity.this, "You did not gave the STORAGE Permission", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void shootPhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.extras = data.getExtras();
            requestStoragePermission();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent previewIntent = new Intent(HomeActivity.this, PreviewActivity.class);
        previewIntent.putExtra("imagePath", (String) al.get(position));
        startActivity(previewIntent);
    }

    class MyAsyncTask extends AsyncTask<Bundle, Void, Void> {

        private Context mContext;
        private ArrayList<String> al;
        private String filePath;

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
                        filePath = Utils.saveImageToLocal(imageBitmap);
                        if (filePath != null) {
                            Preferences.getInstance().setImageTextData(mContext, filePath, textBlockSparseArray.valueAt(i).getValue());
                        }
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
