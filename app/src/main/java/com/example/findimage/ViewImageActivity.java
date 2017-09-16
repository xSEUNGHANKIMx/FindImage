package com.example.findimage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_layout);
        ImageView ivImage = (ImageView) findViewById(R.id.viewImageView);
        TextView tvImageName = (TextView) findViewById(R.id.viewImageTitle);

        FindResultInfo imageFindResult = (FindResultInfo) getIntent().getParcelableExtra("result");
        String url = imageFindResult.getFullUrl();
        Picasso.with(this).load(url).into(ivImage);
        tvImageName.setText(imageFindResult.getTitle());
    }
}
