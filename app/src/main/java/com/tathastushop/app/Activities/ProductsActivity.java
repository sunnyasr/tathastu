
package com.tathastushop.app.Activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.tathastushop.app.Adapters.ProductAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.ProductModel;
import com.tathastushop.app.Permissions.RunTimePermissions;
import com.tathastushop.app.R;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private ArrayList<ProductModel> productArrayList;
    private ProductAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ComMethod comMethod;
    //    private KProgressHUD kProgressHUD;
//    private SharedPreferences sharedPreferences;
    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        new RunTimePermissions(this).requestStoragePermission();
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Products");
        comMethod = new ComMethod(this);
//        setProgress();

        productArrayList = new ArrayList<>();
        adapter = new ProductAdapter(this, productArrayList);

        RecyclerView recyclerViewProduct = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setItemViewCacheSize(20);
        recyclerViewProduct.setDrawingCacheEnabled(true);
        recyclerViewProduct.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewProduct.setLayoutManager(mLayoutManager);


//        recyclerViewProduct.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(recyclerViewProduct)
                .adapter(adapter)
                .load(R.layout.skeleton_products)
                .shimmer(true)
                .angle(20)
                .color(R.color.kprogresshud_grey_color)
                .show();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (comMethod.checkNetworkConnection())
                    getProduct();
                else
                    TastyToast.makeText(getApplicationContext(), "Please check inetenet connection", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });

        if (comMethod.checkNetworkConnection())
            getProduct();
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        else
            TastyToast.makeText(getApplicationContext(), "Please check inetenet connection", TastyToast.LENGTH_LONG, TastyToast.ERROR);
    }

    public void getProduct() {
//        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_FIRST_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                skeletonScreen.hide();
//                Toast.makeText(ProductsActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
                productArrayList.clear();
//                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ProductModel productModel = new ProductModel();
                        productModel.setProductid(jsonObject.getString("productid"));
                        productModel.setPcode(jsonObject.getString("pcode"));
                        productModel.setPname(jsonObject.getString("pname"));
                        productModel.setShipping(jsonObject.getInt("shipping"));
                        productModel.setDescription(jsonObject.getString("description"));
                        productModel.setImage(jsonObject.getString("image"));
                        productModel.setMrp(jsonObject.getInt("mrp"));
                        productModel.setPrice(jsonObject.getInt("price"));
                        productModel.setDiscount(jsonObject.getString("discount"));
                        productArrayList.add(productModel);

//                        Toast.makeText(ProductsActivity.this, ""+jsonObject.getString("pfor"), Toast.LENGTH_SHORT).show();

                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProductsActivity.this, "A : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductsActivity.this, "B : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (error instanceof AuthFailureError) {
                    comMethod.logout();
                    finish();
                }
            }
        });
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + sharedPreferences.getString("tokenno", ""));
//                return headers;
//            }
//        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_whatsapp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.login) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
