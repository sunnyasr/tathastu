package com.tathastushop.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
//import com.paykun.sdk.PaykunApiCall;
//import com.paykun.sdk.eventbus.Events;
//import com.paykun.sdk.eventbus.GlobalBus;
//import com.paykun.sdk.helper.PaykunHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.PrefManager.RegManager;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;
//import com.razorpay.Checkout;
//import com.razorpay.PaymentResultListener;
import com.sdsmdg.tastytoast.TastyToast;

//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener , PaymentResultListener {

    private String transtype = "1";
    private String onlinetrans = "12345567890";
    //    private String sleep = "";
    private RelativeLayout sleep_layout;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private int amount;
    private TextView tvAmount;
    private String depositid = null;
    private JSONObject preFill = null;
    private RegManager regManager;

    private Bitmap bitmap;
    private final int IMAGE_REQUEST_CODE = 23, CAMERA = 22;
    private boolean isSelected = false;
    private ImageView idCard;
    private String photoUrl = "";

    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Payment");
        setProgress();
        regManager = new RegManager(this);
        comMethod = new ComMethod(this);
        sleep_layout = (RelativeLayout) findViewById(R.id.sleep_layout);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvAmount.setText("₹ " + getIntent().getIntExtra("total", 0));
        amount = getIntent().getIntExtra("total", 0);
        idCard = (ImageView) findViewById(R.id.iv_proof);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioCompany) {
                    transtype = "1";
                    sleep_layout.setVisibility(View.VISIBLE);
                }
                if (checkedId == R.id.radioOnline) {
                    transtype = "2";
                    sleep_layout.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.btn_make_payment).setOnClickListener(this);
        findViewById(R.id.btn_choose_image).setOnClickListener(this);
//        sleep_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_make_payment) {
            if (comMethod.checkNetworkConnection()) {
                if (transtype.equals("2")) {

                    startPayment();
                } else if (transtype.equals("1")) {
//                    Toast.makeText(this, "Mnually", Toast.LENGTH_SHORT).show();
                    if (!photoUrl.equals(""))
                        payNow();
                    else Toast.makeText(this, "Please Upload receipt", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.btn_choose_image) {
            showPictureDialog();

        }
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(PaymentActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }


    /*Razorpay*/
    public void startPayment() {
        final Activity activity = this;

        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("description", "Order #" + new Random().nextInt(61) + 20);
            jsonObject.put("curency", "INR");
            jsonObject.put("amount", amount * 100);

            preFill = new JSONObject();
            preFill.put("email",getIntent().getStringExtra("email") );
            preFill.put("contact",  getIntent().getStringExtra("phone"));
            jsonObject.put("prefill", preFill);
            checkout.open(activity, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
//        Toast.makeText(this, "Success " + s, Toast.LENGTH_SHORT).show();
//        updateDialog("Thank You!", "Your Payment is successfully done.");
        onlinetrans = s;
        payNow();
//        depositeUpdate(s, "Successfully");
    }

    private void payNow() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.BUY_NOW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
//                Toast.makeText(PaymentActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("error"))
                        TastyToast.makeText(PaymentActivity.this, "Please Try Again", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    else {

//                        regManager.setFirstTimeLaunch(false);
                        Intent intent = new Intent(getApplicationContext(), OrderSuccessActivity.class);
                        intent.putExtra("memberid", getIntent().getStringExtra("memberid"));
                        intent.putExtra("password", getIntent().getStringExtra("password"));
                        intent.putExtra("phone", getIntent().getStringExtra("phone"));
                        intent.putExtra("name", getIntent().getStringExtra("name"));

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pid", getIntent().getStringExtra("pid"));
                params.put("qty", String.valueOf(getIntent().getIntExtra("qty", 0)));
                params.put("gst", String.valueOf(getIntent().getIntExtra("gst", 0)));
                params.put("total", String.valueOf(amount));
                params.put("memberid", getIntent().getStringExtra("memberid"));
                params.put("transtype", transtype);
                params.put("onlinetrans", onlinetrans);
                params.put("sleep", photoUrl);
                params.put("size", getIntent().getStringExtra("size"));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(PaymentActivity.this).addRequestQueue(stringRequest);
    }

    @Override
    public void onPaymentError(int i, String s) {
//        Toast.makeText(this, "Your payment failed ", Toast.LENGTH_SHORT).show();
//        updateDialog("Payment Failed", "Your transaction is failed.");

        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }


    //Paykun
//    public void startPayment() {
//        if (amount < 10)
//            Toast.makeText(this, "Please enter more than 10  Rupees", Toast.LENGTH_SHORT).show();
//        else {
//
//            JSONObject object = new JSONObject();
//            try {
//                String merchantIdSandbox = "804421196427680";
//                String accessTokenSandbox = "D3957716647185C6FDECF0AC6A13A3A7";
//
//                object.put("merchant_id", merchantIdSandbox);
//                object.put("access_token", accessTokenSandbox);
//                object.put("customer_name", getIntent().getStringExtra("name"));
//                object.put("customer_email", getIntent().getStringExtra("email"));
//                object.put("customer_phone", getIntent().getStringExtra("phone"));
//                object.put("product_name", getIntent().getStringExtra("pid"));
//                object.put("order_no", System.currentTimeMillis()); // order no. should have 10 to 30 character in numeric format
//                object.put("amount", amount);  // minimum amount should be 10
//                object.put("isLive", true); // need to send false if you are in sandbox mode
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            new PaykunApiCall.Builder(PaymentActivity.this).sendJsonObject(object);
//
//        }
//
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void getResults(Events.PaymentMessage message) {
//        if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SUCCESS)) {
//            // do your stuff here
//            // message.getTransactionId() will return your failed or succeed transaction id
//            /* if you want to get your transaction detail call message.getTransactionDetail()
//             *  getTransactionDetail return all the field from server and you can use it here as per your need
//             *  For Example you want to get Order id from detail use message.getTransactionDetail().order.orderId */
//            if (!TextUtils.isEmpty(message.getTransactionId())) {
////                Toast.makeText(PaymentActivity.this, "Your Transaction is succeed with transaction id : "+message.getTransactionId() , Toast.LENGTH_SHORT).show();
////                depositeUpdate(message.getTransactionId(), "Successfully");
//                onlinetrans = message.getTransactionId();
//                payNow();
//                Log.v(" order id ", " getting order id value : " + message.getTransactionDetail().order.orderId);
//            }
//        } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_FAILED)) {
//            // do your stuff here
////            updateDialog("Payment Failed", "Your transaction is failed.");
//            Toast.makeText(PaymentActivity.this, "Your Transaction is failed", Toast.LENGTH_SHORT).show();
//        } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SERVER_ISSUE)) {
//            // do your stuff here
//            Toast.makeText(PaymentActivity.this, PaykunHelper.MESSAGE_SERVER_ISSUE, Toast.LENGTH_SHORT).show();
//        } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_ACCESS_TOKEN_MISSING)) {
//            // do your stuff here
//            Toast.makeText(PaymentActivity.this, "Access Token missing", Toast.LENGTH_SHORT).show();
//        } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_MERCHANT_ID_MISSING)) {
//            // do your stuff here
//            Toast.makeText(PaymentActivity.this, "Merchant Id is missing", Toast.LENGTH_SHORT).show();
//        } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_INVALID_REQUEST)) {
//            Toast.makeText(PaymentActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
//        } else if (message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE)) {
//            Toast.makeText(PaymentActivity.this, "Network is not available", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Register this activity to listen to event.
//        GlobalBus.getBus().register(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // Unregister from activity
//        GlobalBus.getBus().unregister(this);
//    }

    private void uploadImage() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.STORE_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        idCard.setVisibility(View.VISIBLE);
                        isSelected = true;
                        photoUrl = jsonObject.getString("image");
                        TastyToast.makeText(getApplicationContext(), "Receipt upload successfully", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(AddMemberActivity.this, "" + response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sleep", comMethod.imageToString(bitmap));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(PaymentActivity.this).addRequestQueue(stringRequest);

    }

    private String imageToString(Bitmap bitmap) {

        final int maxSize = 1000;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();


        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;

        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
    }

    private void showPictureDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog_media_style, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        LinearLayout camera_sel = (LinearLayout) view.findViewById(R.id.camera);
        LinearLayout gallery_sel = (LinearLayout) view.findViewById(R.id.gallery);
        camera_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
                dialog.dismiss();
            }
        });
        gallery_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(path, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                int rotateImage = getCameraPhotoOrientation(PaymentActivity.this, path, filePath);
                if (rotateImage == 90) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            uploadImage();
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);

            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

}
