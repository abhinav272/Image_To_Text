package com.abhinavsharma.imagetotext;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhinavsharma.imagetotext.helper.Preferences;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by abhinavsharma on 15/07/16.
 */
public class PreviewActivity extends AppCompatActivity{

    private ImageView imagePreview;
    private TextView textPreview;
    private String imagePath, imageText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initializeViews();
    }

    private void initializeViews() {
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        textPreview = (TextView) findViewById(R.id.text_preview);
        imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath != null && new File(imagePath).exists()) {
            Picasso.with(this).load(new File(imagePath)).fit().centerCrop().into(imagePreview);
            textPreview.setText(Preferences.getInstance().getImageTextData(this,imagePath));
        } else textPreview.setText("Image does not exists!!");
    }


}
