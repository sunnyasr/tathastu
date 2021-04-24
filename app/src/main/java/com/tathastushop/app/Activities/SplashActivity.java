package com.tathastushop.app.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.tathastushop.app.BuildConfig;
import com.tathastushop.app.PrefManager.LoginManager;
import com.tathastushop.app.PrefManager.RegManager;
import com.tathastushop.app.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        TextView version = (TextView) findViewById(R.id.tv_version);
        version.setText("Version " + BuildConfig.VERSION_NAME);

        final RegManager regManager = new RegManager(this);
        final LoginManager loginManager = new LoginManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//
//                if (regManager.isFirstTimeLaunch()) {
//                    //ProductActivity
//                    Intent intent = new Intent(SplashActivity.this, ProductsActivity.class);
//                    startActivity(intent);
//                } else

                if (!loginManager.isFirstTimeLaunch()) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
//                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(SplashActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        }, 1500);

    }


}