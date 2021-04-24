package com.tathastushop.app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.R;
import com.github.ybq.android.spinkit.SpinKitView;

import uk.co.senab.photoview.PhotoView;

public class PopUpActivity extends AppCompatActivity implements View.OnClickListener {

    SpinKitView waiting_indicator;
    PhotoView imgDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        findViewById(R.id.iv_close).setOnClickListener(this);
        waiting_indicator = (SpinKitView) findViewById(R.id.spin_kit);
        imgDisplay = (PhotoView) findViewById(R.id.image_preview);
        Glide.with(getApplicationContext()).load(APIConfig.BASE_IMAGE + "uploads/" + getIntent().getStringExtra("adharphoto"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        waiting_indicator.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        waiting_indicator.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imgDisplay);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_close) {
            onBackPressed();
        }
    }
}
