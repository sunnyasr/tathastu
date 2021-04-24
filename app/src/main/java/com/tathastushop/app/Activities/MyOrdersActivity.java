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
import com.tathastushop.app.Adapters.OrderAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.OrdersModel;
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

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoitem;
    private OrderAdapter adapter;
    private ArrayList<OrdersModel> orderArrayList;
    private SwipeRefreshLayout refreshLayout;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("My Orders");

        setProgress();
        comMethod = new ComMethod(this);
        userManager = new UserManager(this);
        orderArrayList = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        tvNoitem =  (TextView) findViewById(R.id.tv_no_item);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OrderAdapter(getApplicationContext(), orderArrayList);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!comMethod.checkNetworkConnection()) {
                    TastyToast.makeText(getApplicationContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } else {
                    refreshLayout.setRefreshing(true);
                    getDelivered();
                }
            }
        });


        if (comMethod.checkNetworkConnection())
            getDelivered();
        else
            comMethod.alertDialog("Network", "Please check internet connection !!", true, null);
    }

    public void getDelivered() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_ORDERS
                + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(MyOrdersActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                kProgressHUD.dismiss();
                refreshLayout.setRefreshing(false);
                orderArrayList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        tvNoitem.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            OrdersModel model = new OrdersModel();

                            model.setOrderid(object.getString("orderid"));
                            model.setMid(object.getString("mid"));
                            model.setPid(object.getString("pid"));
                            model.setStatus(object.getString("status"));
                            model.setDiscount(object.getString("discount"));
                            model.setPrice(object.getString("price"));
                            model.setNett(object.getString("nett"));
                            model.setQty(object.getString("qty"));
                            model.setTotal(object.getString("total"));
                            model.setOrder_date(object.getString("order_date"));
                            model.setDelivered_date(object.getString("delivered_date"));
                            model.setPname(object.getString("pname"));
                            model.setPcode(object.getString("pcode"));
                            model.setImage(object.getString("image"));
                            model.setActivated(object.getString("activated"));
                            model.setEnabled(object.getString("enabled"));
                            model.setUsername(object.getString("username"));
                            model.setMobile(object.getString("mobile"));
                            model.setAddress(object.getString("address"));

                            orderArrayList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }else tvNoitem.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(OrderDeliveredActivity.this, "1)." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

//                T?oast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {
                    comMethod.logout();
                    finish();
                }
                Toast.makeText(MyOrdersActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        kProgressHUD = KProgressHUD.create(MyOrdersActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

}
