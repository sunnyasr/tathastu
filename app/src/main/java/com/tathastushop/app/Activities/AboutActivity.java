package com.tathastushop.app.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutActivity extends AppCompatActivity {
    private KProgressHUD kProgressHUD;
    private TextView tvTitle, tvDesc;
    private ComMethod comMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("About Us");
        setProgress();
        comMethod = new ComMethod(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        getAbout();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(AboutActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getAbout() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.ABOUT + "1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(AboutActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject =  new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    JSONObject object = jsonArray.getJSONObject(0);
                    tvTitle.setText(object.getString("page"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        tvDesc.setText(Html.fromHtml(object.getString("description"), Html.FROM_HTML_MODE_LEGACY));
                    } else
                        tvDesc.setText(Html.fromHtml(object.getString("description")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AboutActivity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {
                    comMethod.logout();
                    finish();
                }

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
            finish();
            return true;
        }
        return false;
    }
}
