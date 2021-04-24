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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.Adapters.TransAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.TransactionModel;
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

public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TransAdapter adapter;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvNoitem;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getIntent().getStringExtra("type"));

//        Toast.makeText(this, ""+getIntent().getStringExtra("paid"), Toast.LENGTH_SHORT).show();
        comMethod = new ComMethod(this);
        setProgress();
        userManager = new UserManager(this);
        tvNoitem =  (TextView) findViewById(R.id.tv_no_item);
        //List
        transactionModelArrayList = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TransAdapter(this, transactionModelArrayList);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!comMethod.checkNetworkConnection()) {
                    TastyToast.makeText(getApplicationContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } else {
                    refreshLayout.setRefreshing(true);
                    getTransactions();
                }
            }
        });


        getTransactions();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(TransactionsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void getTransactions() {

        String url;
        if (getIntent().getStringExtra("paid").equals("false"))
            url = APIConfig.TRANS_UNPAID;
        else url = APIConfig.TRANS_PAID;

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                transactionModelArrayList.clear();
                refreshLayout.setRefreshing(false);
                kProgressHUD.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        tvNoitem.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    Toast.makeText(TransactionsActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            TransactionModel model = new TransactionModel();
                            model.setTransactionid(object.getString("transactionid"));
                            model.setMid(object.getString("mid"));
                            model.setAmount(object.getString("amount"));
                            model.setAdmincharge(object.getString("admincharge"));
                            model.setTds(object.getString("tds"));
                            model.setNett(object.getString("nett"));
                            model.setStatus(object.getString("status"));
                            model.setReqdate(object.getString("reqdate"));
                            model.setPaydate(object.getString("paydate"));
                            model.setAcno(object.getString("acno"));
                            model.setAcname(object.getString("acname"));
                            model.setBankname(object.getString("bankname"));
                            model.setActivated(object.getString("activated"));
                            model.setEnabled(object.getString("enabled"));
                            model.setUsername(object.getString("username"));

                            transactionModelArrayList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }else  tvNoitem.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    kProgressHUD.dismiss();
                    Toast.makeText(TransactionsActivity.this, "kk"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
