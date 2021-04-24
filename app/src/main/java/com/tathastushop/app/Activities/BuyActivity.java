package com.tathastushop.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.nex3z.togglebuttongroup.button.LabelToggle;
import com.sdsmdg.tastytoast.TastyToast;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.Models.ProductsImages;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvMRP, tvPname, tvDesc, tvPrice, tvWallet, tvTotal, tvSTotal, tvQty, tvGST, tvShipping, tvPCode;
    //    private ImageView ivProduct;
    private SharedPreferences sharedPreferences;
    private int wallet = 0;
    private int price = 0;
    private int total = 0;
    private int qty = 0;
    private int gst = 0;
    private int shipping = 0;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private ElegantNumberButton elegantNumberButton;

    private ViewPager viewPager;
    private ArrayList<ProductsImages> images;
    private MyViewPopupAdapter myViewPagerAdapter;

    private LinearLayout sliderDotspanel;
    private ImageView[] dots;

    private String size = "";

    private Bitmap bitmap;

    private EditText etPincode;

    private String pincode;

    private CircularToggle m, l, xl, xxl, xxxl, freesize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        final Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Buy Product");
        setProgress();
        comMethod = new ComMethod(this);
        //SharedPreference
        sharedPreferences = getSharedPreferences("mLoginUserPref", Context.MODE_PRIVATE);
//        sharedPreferencesBank = getSharedPreferences("prefBank", Context.MODE_PRIVATE);

        wallet = sharedPreferences.getInt("wallet", 0);
//        getProductImages();

        //Product MRP
        tvMRP = (TextView) findViewById(R.id.product_mrp);
        tvMRP.setPaintFlags(tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvMRP.setText("MRP " + getIntent().getIntExtra("mrp", 0));

        //Product EMI
        tvPrice = (TextView) findViewById(R.id.product_price);
        tvPrice.setText("MRP " + getIntent().getIntExtra("price", 0));
        price = getIntent().getIntExtra("price", 0);

        //Sub Total
        tvSTotal = (TextView) findViewById(R.id.tv_sub_total);
        tvSTotal.setText("₹" + price);

        //Shipping
        tvShipping = (TextView) findViewById(R.id.tv_shipping);
        tvShipping.setText("₹" + getIntent().getIntExtra("shipping", 0));
        shipping = getIntent().getIntExtra("shipping", 0);

        //Product Total
        tvTotal = (TextView) findViewById(R.id.product_total);
        tvTotal.setText("Total ₹" + getIntent().getIntExtra("total", 0));

        //Product Name
        tvPname = (TextView) findViewById(R.id.tv_pname);
        tvPname.setText(getIntent().getStringExtra("pname"));

        //Product Desc.
        tvDesc = (TextView) findViewById(R.id.product_desc);
        tvDesc.setText("Description \n" + getIntent().getStringExtra("desc"));

        //Product Code.
        tvPCode = (TextView) findViewById(R.id.tv_pcode);
        tvPCode.setText("(" + getIntent().getStringExtra("pcode") + ")");

        //Wallet
        tvWallet = (TextView) findViewById(R.id.tv_wallet);
        tvWallet.setText("Wallet\n₹" + sharedPreferences.getInt("wallet", 0));

        tvTotal = (TextView) findViewById(R.id.tv_total);

        //Qty
        tvQty = (TextView) findViewById(R.id.tv_qty);
//        getGST();
        tvGST = (TextView) findViewById(R.id.tv_gst);

        elegantNumberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        qty = Integer.parseInt(elegantNumberButton.getNumber());

        tvQty.setText("" + qty);
        total = price * qty;
        int gtotal = shipping + total + (total * gst / 100);
        tvTotal.setText("₹" + gtotal);
//        tvTotal.setText("₹" + price + " x " + qty + " = " + total);
        elegantNumberButton.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty = Integer.parseInt(elegantNumberButton.getNumber());
                tvQty.setText("" + qty);
                total = price * qty;
                tvSTotal.setText("₹" + total);
                int gtotal = shipping + total + (total * gst / 100);
                tvTotal.setText("₹" + gtotal);

            }
        });
        m = (CircularToggle) findViewById(R.id.choice_m);
        l = (CircularToggle) findViewById(R.id.choice_l);
        xl = (CircularToggle) findViewById(R.id.choice_xl);
        xxl = (CircularToggle) findViewById(R.id.choice_xxl);
        xxxl = (CircularToggle) findViewById(R.id.choice_xxxl);
        freesize = (CircularToggle) findViewById(R.id.choice_fsize);

        SingleSelectToggleGroup single = (SingleSelectToggleGroup) findViewById(R.id.group_choices);
        single.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {

                CircularToggle toggle = findViewById(checkedId);
                size = toggle.getText().toString().trim();
//                Toast.makeText(BuyActivity.this, "" + toggle.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        //Product Images

        images = new ArrayList<>();

        viewPager = (ViewPager) findViewById(R.id.iv_product);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        myViewPagerAdapter = new MyViewPopupAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        getProductImages();

//        ivProduct = (ImageView) findViewById(R.id.iv_product);

//        Picasso.get()
//                .load(APIConfig.BASE_IMAGE + "products/" + getIntent().getStringExtra("pimage"))
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_CACHE)
//                .into(ivProduct);

        //Payment Button
        findViewById(R.id.btn_buy_now).setOnClickListener(this);
        findViewById(R.id.btn_share_whatsapp).setOnClickListener(this);
        findViewById(R.id.btn_other_share).setOnClickListener(this);

        //Pincode
        etPincode = (EditText) findViewById(R.id.et_pincode);
        findViewById(R.id.btn_pincode).setOnClickListener(this);

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(BuyActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getProductImages() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.PRODUCT_SINGLE
                + getIntent().getStringExtra("pid"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(BuyActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                ProductsImages images1 = new ProductsImages();
                images1.setProductimg(getIntent().getStringExtra("pimage"));
                images.add(images1);
                try {
                    JSONObject object = new JSONObject(response);
                    gst = object.getInt("gst");
                    tvGST.setText(object.getString("gst") + "%");

                    int gtotal = shipping + total + (total * gst / 100);
                    tvTotal.setText("₹" + gtotal);

                    JSONArray jsonArray = object.getJSONArray("pimages");
                    JSONArray sizeArray = object.getJSONArray("sizes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ProductsImages productsImages = new ProductsImages();
                        productsImages.setProductimg(jsonObject.getString("image"));
                        images.add(productsImages);
                    }
                    JSONObject sizeObject = sizeArray.getJSONObject(0);

                    if (sizeObject.getString("m").equals("false")) {
                        m.setEnabled(false);
                        m.setMarkerColor(R.color.colorLine);
                        m.setTextColor(R.color.colorLine);
                    }
                    if (sizeObject.getString("l").equals("false")) {
                        l.setEnabled(false);
                        l.setMarkerColor(R.color.colorLine);
                        l.setTextColor(R.color.colorLine);
                    }
                    if (sizeObject.getString("xl").equals("false")) {
                        xl.setEnabled(false);
                        xl.setMarkerColor(R.color.colorLine);
                        xl.setTextColor(R.color.colorLine);
                    }
                    if (sizeObject.getString("xxl").equals("false")) {
                        xxl.setEnabled(false);
                        xxl.setMarkerColor(R.color.colorLine);
                        xxl.setTextColor(R.color.colorLine);
                    }
                    if (sizeObject.getString("xxxl").equals("false")) {
                        xxxl.setEnabled(false);
                        xxxl.setMarkerColor(R.color.colorLine);
                        xxxl.setTextColor(R.color.colorLine);
                    }
                    if (sizeObject.getString("freesize").equals("false")) {
                        freesize.setEnabled(false);
                        freesize.setMarkerColor(R.color.colorLine);
                        freesize.setTextColor(R.color.colorLine);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                myViewPagerAdapter.notifyDataSetChanged();

                dots = new ImageView[myViewPagerAdapter.getCount()];
                for (int i = 0; i < myViewPagerAdapter.getCount(); i++) {

                    dots[i] = new ImageView(BuyActivity.this);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }
                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sharedPreferences.getString("tokenno", ""));
                return headers;
            }


        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public void getGST() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_GST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(BuyActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    gst = jsonObject.getInt("gst");
                    tvGST.setText(jsonObject.getString("gst") + "%");

                    int gtotal = shipping + total + (total * gst / 100);
                    tvTotal.setText("₹" + gtotal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BuyActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sharedPreferences.getString("tokenno", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_buy_now) {
//            makePayment();
//            Toast.makeText(this, "Payment", Toast.LENGTH_SHORT).show();
            pincode = etPincode.getText().toString().trim();
            if (size.equals("")) {
                Toast.makeText(this, "Please select size", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pincode))
                TastyToast.makeText(getApplicationContext(), "Please enter pincode", TastyToast.LENGTH_LONG, TastyToast.INFO);
            else if (pincode.length() < 6)
                TastyToast.makeText(getApplicationContext(), "Please enter valid pincode", TastyToast.LENGTH_LONG, TastyToast.INFO);
            else if (pincode.length() < 6)
                TastyToast.makeText(getApplicationContext(), "Please enter valid pincode", TastyToast.LENGTH_LONG, TastyToast.INFO);
            else {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("pid", getIntent().getStringExtra("pid"));
                intent.putExtra("qty", qty);
                intent.putExtra("gst", gst);
                intent.putExtra("total", shipping + total + (total * gst / 100));
                intent.putExtra("pincode", pincode);
                intent.putExtra("size", size);
                startActivity(intent);
            }
        }

        if (v.getId() == R.id.btn_share_whatsapp) {
            comMethod.prepareShareIntent(bitmap, "whatsapp", getIntent().getStringExtra("pname") + "\n" +
                    "₹" + getIntent().getIntExtra("price", 0) + "\n" +
                    "https://play.google.com/store/apps/details?id=" + this.getPackageName());
        }
        if (v.getId() == R.id.btn_other_share) {
            comMethod.prepareShareIntent(bitmap, "other", getIntent().getStringExtra("pname") + "\n" +
                    "₹" + getIntent().getIntExtra("price", 0) + "\n" +
                    "https://play.google.com/store/apps/details?id=" + this.getPackageName());

        }

        if (v.getId() == R.id.btn_pincode) {
            pincode = etPincode.getText().toString().trim();
            if (TextUtils.isEmpty(pincode))
                TastyToast.makeText(getApplicationContext(), "Please enter pincode", TastyToast.LENGTH_LONG, TastyToast.INFO);
            else if (pincode.length() < 6)
                TastyToast.makeText(getApplicationContext(), "Please enter valid pincode", TastyToast.LENGTH_LONG, TastyToast.INFO);
            else
                checkpincode(pincode);
        }
    }

    private void checkpincode(final String pincode) {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.CHECK_PINCODE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
//                Toast.makeText(BuyActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("error")) {
                        shipping = 0;
                        tvShipping.setText("₹" + shipping);
                        int gtotal = shipping + total + (total * gst / 100);
                        tvTotal.setText("₹" + gtotal);
                    } else {
                        shipping = 50;
                        tvShipping.setText("₹" + shipping);
                        int gtotal = shipping + total + (total * gst / 100);
                        tvTotal.setText("₹" + gtotal);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BuyActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pincode", pincode);
                return params;
            }
        };

        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public class MyViewPopupAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPopupAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageView imgDisplay;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_product_preview, container, false);

            imgDisplay = (ImageView) view.findViewById(R.id.iv_product);
            final ProductsImages image = images.get(position);


//            Picasso.get()
//                    .load(APIConfig.BASE_IMAGE + "products/" + image.getProductimg())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .into(imgDisplay);

            Glide.with(BuyActivity.this).load(APIConfig.BASE_IMAGE + "products/" + image.getProductimg()).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(new BitmapImageViewTarget(imgDisplay) {
                @Override
                public void onResourceReady(final Bitmap bmp, GlideAnimation anim) {
                    imgDisplay.setImageBitmap(bmp);
                    bitmap = bmp;
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                }
            });

            container.addView(view);

            imgDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images);
                    bundle.putInt("position", position);

                    Intent intent = new Intent(BuyActivity.this, PopProductActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < myViewPagerAdapter.getCount(); i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            }

            dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

//    private void makePayment() {
//        if (total > wallet) {
//            Intent intent = new Intent(getApplicationContext(), AddMoneyActivity.class);
//            comMethod.alertDialog("Wallet", "Insufficient Wallet Amount $" + wallet, true, intent);
//        } else if (sharedPreferences.getString("distributor", "").equals("1")) {
//            if (qty < 5)
//                TastyToast.makeText(getApplicationContext(), "Please minimum 5 box buy !", TastyToast.LENGTH_LONG, TastyToast.INFO);
//            else
//                placeOrder();
//        } else {
//            placeOrder();
//
//        }
//    }

//    public void placeOrder() {
//        kProgressHUD.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.BUY_NOW, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                kProgressHUD.dismiss();
//                try {
//                    JSONObject object = new JSONObject(response);
//                    if (object.getBoolean("error")) {
//                        Intent intent = new Intent(getApplicationContext(), AddMoneyActivity.class);
//                        comMethod.alertDialog("Buy Product", object.getString("msg"), true, intent);
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        comMethod.alertDialog("Buy Product", object.getString("msg"), false, intent);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                kProgressHUD.dismiss();
//                if (error instanceof AuthFailureError) {
//                    comMethod.logout();
//                    finish();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + sharedPreferences.getString("tokenno", ""));
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("memberid", sharedPreferences.getString("memberid", ""));
//                params.put("pid", getIntent().getStringExtra("pid"));
//                params.put("total", String.valueOf(total));
//                params.put("qty", String.valueOf(qty));
//                return params;
//            }
//        };
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
//                0, 0));
//        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
//
//    }


}
