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
import com.tathastushop.app.Adapters.LevelTreeAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.MemberModel;
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

public class LevelFinalActivity extends AppCompatActivity {

    private ArrayList<MemberModel> memberArrayList;
    private LevelTreeAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_final);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getIntent().getStringExtra("level"));
        comMethod = new ComMethod(this);
        setProgress();
        userManager = new UserManager(this);
        memberArrayList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LevelTreeAdapter(this, memberArrayList);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getLevelMember();
            }
        });

        if (comMethod.checkNetworkConnection())
            getLevelMember();
        else comMethod.alertDialog("Network", "Please check internet connection", true, null);
    }

    private void getLevelMember() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.LEVEL_MEMBER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(LevelFinalActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        kProgressHUD.dismiss();
                        refreshLayout.setRefreshing(false);
                        memberArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            Toast.makeText(LevelFinalActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            if (jsonObject.getBoolean("error"))
                                TastyToast.makeText(getApplicationContext(), "Server Error", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                            else {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    MemberModel model = new MemberModel();
                                    model.setUsername(object.getString("username"));
                                    model.setMobile(object.getString("mobile"));
                                    model.setMemberid(object.getString("memberid"));
                                    model.setRefferal(object.getString("refferal"));
                                    model.setRegdate(object.getString("regdate"));
                                    memberArrayList.add(model);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LevelFinalActivity.this, "FFF" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("memberid", userManager.getMID());
                params.put("position", String.valueOf(getIntent().getExtras().getInt("position")));
                return params;
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
        kProgressHUD = KProgressHUD.create(LevelFinalActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}
