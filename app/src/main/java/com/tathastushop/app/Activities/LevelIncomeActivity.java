package com.tathastushop.app.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.Adapters.LevelIncomeAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.LevelIncomeModel;
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

public class LevelIncomeActivity extends AppCompatActivity {

    private ArrayList<LevelIncomeModel> incomeArrayList;
    private LevelIncomeAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ComMethod checkNetworkConnection;
    //    private SharedPreferences sharedPreferences;
    private UserManager userManager;
    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_income);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Level Income");
        checkNetworkConnection = new ComMethod(this);
        userManager = new UserManager(this);
        incomeArrayList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        setProgress();
        adapter = new LevelIncomeAdapter(this, incomeArrayList);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!checkNetworkConnection.checkNetworkConnection()) {
                    TastyToast.makeText(getApplicationContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } else {
                    refreshLayout.setRefreshing(true);
                    getLevelIncome();
                }
            }

        });

        if (!checkNetworkConnection.checkNetworkConnection()) {
            TastyToast.makeText(this, "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        } else {
            getLevelIncome();
        }
    }


    public void getLevelIncome() {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.LEVEL_INCOME +
                userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                incomeArrayList.clear();
                refreshLayout.setRefreshing(false);
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        LevelIncomeModel userModel = new LevelIncomeModel();
                        userModel.setIncomeid(jsonObject.getString("incomeid"));
                        userModel.setMid(jsonObject.getString("mid"));
                        userModel.setFid(jsonObject.getString("fid"));
                        userModel.setLevelid(jsonObject.getString("level"));
                        userModel.setProductid(jsonObject.getString("productid"));
                        userModel.setAmount(jsonObject.getString("amount"));
                        userModel.setCreated_date(jsonObject.getString("created_date"));

                        userModel.setActivated(jsonObject.getString("activated"));
                        userModel.setEnabled(jsonObject.getString("enabled"));
                        userModel.setPercent(jsonObject.getString("percent"));
                        userModel.setNett(jsonObject.getString("nett"));
                        incomeArrayList.add(userModel);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    kProgressHUD.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {
                    checkNetworkConnection.logout();
                    finish();
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
            finish();
            return true;
        }
        return false;
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(LevelIncomeActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}
