package com.tathastushop.app.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.Adapters.WalletTransAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.WalletHistoryModel;
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

public class WalletHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    //    private RecyclerView.LayoutManager layoutManager;
    private WalletTransAdapter adapter;
    private ArrayList<WalletHistoryModel> walletHistoryModelArrayList;
    private SwipeRefreshLayout refreshLayout;

    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private UserManager userManager;
    private TextView tvNoitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Wallet Trans.");
        comMethod = new ComMethod(this);
        setProgress();
        userManager = new UserManager(this);
        //List
        walletHistoryModelArrayList = new ArrayList<>();

        tvNoitem =  (TextView) findViewById(R.id.tv_no_item);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new WalletTransAdapter(this, walletHistoryModelArrayList);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!comMethod.checkNetworkConnection()) {
                    TastyToast.makeText(getApplicationContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } else {
                    refreshLayout.setRefreshing(true);
                    getWalletHistory();
                }
            }
        });

        getWalletHistory();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(WalletHistoryActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getWalletHistory() {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.WALLET_HISTORY
                + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                walletHistoryModelArrayList.clear();
                refreshLayout.setRefreshing(false);
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        tvNoitem.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    Toast.makeText(WalletHistoryActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            WalletHistoryModel model = new WalletHistoryModel();
                            model.setWhid(object.getInt("whid"));
                            model.setMid(object.getInt("mid"));
                            model.setAmount(object.getInt("amount"));
                            model.setIncometype(object.getString("incometype"));
                            model.setDeposit_withdraw(object.getString("deposit_withdraw"));
                            model.setDate(object.getString("date"));
                            model.setUsername(object.getString("username"));
                            walletHistoryModelArrayList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }else tvNoitem.setVisibility(View.VISIBLE);
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
