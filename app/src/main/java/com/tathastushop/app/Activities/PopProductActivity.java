package com.tathastushop.app.Activities;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.Models.ProductsImages;
import com.tathastushop.app.R;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class PopProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private ArrayList<ProductsImages> images;
    private int selectedPosition = 0;
    private MyViewPopupAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_product);
        viewPager = (ViewPager) findViewById(R.id.pager);
        images = (ArrayList<ProductsImages>) getIntent().getSerializableExtra("images");
        selectedPosition = getIntent().getIntExtra("position", 0);
        myViewPagerAdapter = new MyViewPopupAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_prev).setOnClickListener(this);
        findViewById(R.id.iv_next).setOnClickListener(this);

        setCurrentItem(selectedPosition);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_close) {
            onBackPressed();
        }
        if (view.getId() == R.id.iv_prev) {
            viewPager.setCurrentItem(getItem(-1), true);
        }
        if (view.getId() == R.id.iv_next) {
            viewPager.setCurrentItem(getItem(+1), true);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public class MyViewPopupAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPopupAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView imgDisplay;
            final SpinKitView waiting_indicator;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            imgDisplay = (PhotoView) view.findViewById(R.id.image_preview);
            waiting_indicator = (SpinKitView) view.findViewById(R.id.spin_kit);
            ProductsImages image = images.get(position);

            Glide.with(getApplicationContext()).load(APIConfig.BASE_IMAGE + "products/" + image.getProductimg())
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
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


}
