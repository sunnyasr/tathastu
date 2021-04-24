package com.tathastushop.app.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.tathastushop.app.Adapters.MainSliderAdapter;
import com.tathastushop.app.Adapters.ProductInnerAdapter;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.CircleImageView;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.ProductModel;
import com.tathastushop.app.PrefManager.LoginManager;
import com.tathastushop.app.PrefManager.UserManager;
import com.tathastushop.app.R;
import com.tathastushop.app.Services.PicassoImageLoadingService;
import com.tathastushop.app.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<String> imageArray;
    private MainSliderAdapter sliderAdapter;
    private Slider slider;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    private ArrayList<ProductModel> productArrayList;
    private ProductInnerAdapter adapter;
    private SkeletonScreen skeletonScreen;
    private UserManager userManager;
    private TextView headerName, headerAmount;
    private CircleImageView navProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Tathastu");

        comMethod = new ComMethod(this);
        setProgress();

        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_opne, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        headerName = (TextView) header.findViewById(R.id.tv_name);
        headerAmount = (TextView) header.findViewById(R.id.tv_wallet);
        navProfile = (CircleImageView) header.findViewById(R.id.avatar);

        imageArray = new ArrayList<>();
        sliderAdapter = new MainSliderAdapter(this, imageArray);
        Slider.init(new PicassoImageLoadingService(this));
        slider = findViewById(R.id.banner_slider1);
        slider.setAdapter(sliderAdapter);
        getSlider();
        userManager = new UserManager(this);

        productArrayList = new ArrayList<>();
        adapter = new ProductInnerAdapter(this, productArrayList);

        RecyclerView recyclerViewProduct = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setItemViewCacheSize(20);
        recyclerViewProduct.setDrawingCacheEnabled(true);
        recyclerViewProduct.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerViewProduct.setLayoutManager(mLayoutManager);


//        recyclerViewProduct.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(recyclerViewProduct)
                .adapter(adapter)
                .load(R.layout.skeleton_products)
                .shimmer(true)
                .angle(20)
                .color(R.color.kprogresshud_grey_color)
                .show();

        if (comMethod.checkNetworkConnection()) {
            getProduct();
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            getMe();
        } else
            TastyToast.makeText(getApplicationContext(), "Please check inetenet connection", TastyToast.LENGTH_LONG, TastyToast.ERROR);

    }

    public void getProduct() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                skeletonScreen.hide();
                productArrayList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
//                    Toast.makeText(MainActivity.this, ""+jsonArray.length(), Toast.LENGTH_SHORT).show();
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
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "A : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "B : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void getSlider() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.SLIDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imageArray.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        imageArray.add(jsonObject.getString("image"));
                    }
                    slider.setAdapter(sliderAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    private void getMe() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    userManager.setMID(jsonObject.getString("memberid"));
                    userManager.setName(jsonObject.getString("username"));
                    userManager.setMobile(jsonObject.getString("mobile"));
                    userManager.setEmail(jsonObject.getString("email"));
                    userManager.setPhoto(jsonObject.getString("photo"));
                    userManager.setPass(jsonObject.getString("tpassword"));
                    userManager.setAddress(jsonObject.getString("address"));
                    userManager.setWallet(jsonObject.getInt("wallet"));
                    userManager.setAdharNo(jsonObject.getString("adhaarno"));
                    userManager.setAdharPhoto(jsonObject.getString("adhaarphoto"));
                    userManager.setAcno(jsonObject.getString("acno"));
                    userManager.setAcname(jsonObject.getString("acname"));
                    userManager.setBName(jsonObject.getString("bankname"));
                    userManager.setBranch(jsonObject.getString("branch"));
                    userManager.setIFSC(jsonObject.getString("ifsccode"));
                    userManager.setCity(jsonObject.getString("city"));
                    userManager.setPinCode(jsonObject.getString("pincode"));
                    userManager.setDistrict(jsonObject.getString("district"));
                    userManager.setState(jsonObject.getString("statename"));
                    headerName.setText(userManager.getName());
                    headerAmount.setText("₹ " + userManager.getWallet());

                    Picasso.get()
                            .load(APIConfig.BASE_IMAGE + "uploads/" + userManager.getPhoto())
                            .placeholder(R.drawable.tree_user_grey)
                            .error(R.drawable.tree_user_grey)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(navProfile);

//                    Toast.makeText(MainActivity.this, "" + userManager.getPhoto(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "[SELF]" + error.getMessage(), Toast.LENGTH_SHORT).show();
                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {
                    logout();
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

        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_profile) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (item.getItemId() == R.id.nav_tree) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, LevelActivity.class));
        }

        if (item.getItemId() == R.id.nav_orders) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, MyOrdersActivity.class));
        }


        if (item.getItemId() == R.id.nav_level_income) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), LevelIncomeActivity.class));
        }

        if (item.getItemId() == R.id.nav_trans_req) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), TransRequestActivity.class));
        }

        if (item.getItemId() == R.id.nav_trans_pen) {
//            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);
            intent.putExtra("type", "Pending Transactions");
            intent.putExtra("paid", "false");
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_trans_deli) {
//            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);
            intent.putExtra("type", "Delivered Transactions");
            intent.putExtra("paid", "true");
            startActivity(intent);
        }

        if (item.getItemId() == R.id.nav_wallet_trans) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, WalletHistoryActivity.class));
        }

        if (item.getItemId() == R.id.nav_change_pass) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
        }
        if (item.getItemId() == R.id.nav_projection) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), ProjectionActivity.class));
        }

        if (item.getItemId() == R.id.nav_refund) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), RefundActivity.class));
        }
        if (item.getItemId() == R.id.nav_share) {
//            drawer.closeDrawer(GravityCompat.START);
            comMethod.share(MainActivity.this);
        }
        if (item.getItemId() == R.id.nav_star) {
//            drawer.closeDrawer(GravityCompat.START);
            comMethod.rate(MainActivity.this);
        }
        if (item.getItemId() == R.id.nav_about) {
//            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        }
        if (item.getItemId() == R.id.nav_logout) {
//            drawer.closeDrawer(GravityCompat.START);
            logout();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        LoginManager loginManager = new LoginManager(this);
        loginManager.setFirstTimeLaunch(true);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        headerName.setText(userManager.getName());
        headerAmount.setText("₹ " + userManager.getWallet());
        Picasso.get()
                .load(APIConfig.BASE_IMAGE + "uploads/" + userManager.getPhoto())
                .placeholder(R.drawable.tree_user_grey)
                .error(R.drawable.tree_user_grey)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(navProfile);
    }
}
