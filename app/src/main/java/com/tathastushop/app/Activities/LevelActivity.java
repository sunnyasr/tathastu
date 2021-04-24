package com.tathastushop.app.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.Adapters.LevelS1Adapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.LevelModel;
import com.tathastushop.app.PrefManager.UserManager;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class LevelActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LevelS1Adapter adapter;
    private ArrayList<LevelModel> levelArrayList;
    private SwipeRefreshLayout refreshLayout;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Levels");
        setProgress();
        comMethod = new ComMethod(this);
        userManager = new UserManager(this);
        levelArrayList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LevelS1Adapter(getApplicationContext(), levelArrayList);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!comMethod.checkNetworkConnection()) {
                    TastyToast.makeText(getApplicationContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } else {
                    refreshLayout.setRefreshing(true);
                    getLevelCount();
                }
            }
        });


        if (!comMethod.checkNetworkConnection()) {
            TastyToast.makeText(getApplicationContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        } else {
            getLevelCount();
        }
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getLevelCount() {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.LEVEL_COUNT
                + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(LevelActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                levelArrayList.clear();
                refreshLayout.setRefreshing(false);
                kProgressHUD.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            LevelModel model = new LevelModel();
                            model.setLevel(object.getString("level"));
                            model.setCount(object.getString("count"));
                            levelArrayList.add(model);
                        }
                        adapter.notifyDataSetChanged();

                    } else
                        TastyToast.makeText(getApplicationContext(), "555" + jsonObject.getString("msg"), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                    kProgressHUD.dismiss();
                    Toast.makeText(LevelActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {
                    comMethod.logout();
                    finish();
                }
//                Toast.makeText(LevelActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }


    public void setProgress() {
        kProgressHUD = KProgressHUD.create(LevelActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}
