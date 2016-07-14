package com.abhinavsharma.imagetotext;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

/**
 * Created by abhinavsharma on 14/07/16.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private Button btnCamera;
    private ListView lvAllText;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
    }

    private void initializeViews() {
        btnCamera = (Button) findViewById(R.id.camera);
        lvAllText = (ListView) findViewById(R.id.lv_all_text);
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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);

            Frame frame = new Frame.Builder()
                    .setBitmap(imageBitmap).build();

            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
            if (!textRecognizer.isOperational()) {
                Log.w(TAG, "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                    Log.w(TAG, getString(R.string.low_storage_error));
                }
            }

            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
            if (textBlockSparseArray != null) {
                ArrayList<String> al = new ArrayList<>();
                for (int i = 0; i < textBlockSparseArray.size(); i++) {
                    if (textBlockSparseArray.valueAt(i).getValue() != null) {
                        al.add(textBlockSparseArray.valueAt(i).getValue());
                    }
                }
                if (al != null) {
                    Intent intent = new Intent(HomeActivity.this, TextViewer.class);
                    intent.putExtra("all_text", al);
                    Log.e(TAG, "onActivityResult: Size "+al.size());
                    startActivity(intent);
                }
            } else Log.e(TAG, "onActivityResult: No data found..");

        }
    }
}
