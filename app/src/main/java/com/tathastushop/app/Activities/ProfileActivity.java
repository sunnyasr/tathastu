package com.tathastushop.app.Activities;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.CircleImageView;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.PrefManager.UserManager;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnUpdateImage;
    private TextView mMobile, mUid, mAmount;
    private CircleImageView profileImage;
    private ComMethod comMethod;
    private UserManager userManager;

    private Bitmap bitmap;
    private final int IMAGE_REQUEST_CODE = 23, CAMERA = 22;
    private final int IMAGE_REQUEST_CODE_ID_CARD = 233, CAMERA_CARD = 222;
    private BottomSheetDialog dialog;
    private KProgressHUD kProgressHUD;

    private EditText etName, etAdharNo, etEmail, etPincode,etDistrict,etState,etAcno, etBranch, etAcname, etIfsc, etAddress, etNearloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Profile");

        setProgress();
        comMethod = new ComMethod(this);
        userManager = new UserManager(this);
        btnUpdateImage = (ImageButton) findViewById(R.id.btn_update_image);
        btnUpdateImage.setOnClickListener(this);

        initTextview();
    }

    public void initTextview() {
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        mMobile = (TextView) findViewById(R.id.tv_mobile);
        mAmount = (TextView) findViewById(R.id.tv_amount);
        mUid = (TextView) findViewById(R.id.tv_id);

        etName = (EditText) findViewById(R.id.et_username);
        etAdharNo = (EditText) findViewById(R.id.et_adhar_no);
        etAcno = (EditText) findViewById(R.id.et_acno);
        etAcname = (EditText) findViewById(R.id.et_acname);
        etIfsc = (EditText) findViewById(R.id.et_ifsc_code);
        etBranch = (EditText) findViewById(R.id.et_branch);
        etAddress = (EditText) findViewById(R.id.et_caddress);
        etPincode =(EditText) findViewById(R.id.et_pincode);
        etDistrict =(EditText) findViewById(R.id.et_district);
        etState =(EditText) findViewById(R.id.et_state);
        etNearloc = (EditText) findViewById(R.id.et_near_loc);
        etEmail = (EditText) findViewById(R.id.et_email);

        findViewById(R.id.btn_preview).setOnClickListener(this);
        findViewById(R.id.btn_change).setOnClickListener(this);

        findViewById(R.id.btn_update).setOnClickListener(this);
        if (comMethod.checkNetworkConnection()) {
            Picasso.get()
                    .load(APIConfig.BASE_IMAGE + "uploads/" + userManager.getPhoto())
                    .placeholder(R.drawable.tree_user_grey)
                    .error(R.drawable.tree_user_grey)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profileImage);
//            Toast.makeText(this, "Load Image", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Check Connection", Toast.LENGTH_SHORT).show();
        // TastyToast.makeText(this, "Check your internet connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

        mAmount.setText("Wallet  Amount\n₹ " + userManager.getWallet());
        mMobile.setText(userManager.getMobile());
        mUid.setText("Member ID\n" + userManager.getMID());

        etName.setText(userManager.getName());
        etAdharNo.setText(userManager.getAdharNo());
        etAcno.setText(userManager.getAcno());
        etAcname.setText(userManager.getAcname());
        etIfsc.setText(userManager.getIFSC());
        etBranch.setText(userManager.getBranch());
        etAddress.setText(userManager.getAddress());
        etNearloc.setText(userManager.getCity());
        etEmail.setText(userManager.getEmail());
        etPincode.setText(userManager.getPinCode());
        etDistrict.setText(userManager.getDistrict());
        etState.setText(userManager.getState());

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_preview) {
            Intent intent = new Intent(this, PopUpActivity.class);
            intent.putExtra("adharphoto", userManager.getAdharPhoto());
            startActivity(intent);
        }

        if (v.getId() == R.id.btn_change) {
            if (comMethod.checkNetworkConnection())
                showPictureDialog("idcard");
            else
                TastyToast.makeText(this, "Check your internet connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }


        if (v.getId() == R.id.btn_update_image) {
            if (comMethod.checkNetworkConnection())
                showPictureDialog("profile");
            else
                TastyToast.makeText(this, "Check your internet connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);

        }

        if (v.getId() == R.id.btn_update) {
            if (comMethod.checkNetworkConnection())
                update();
            else
                TastyToast.makeText(this, "Check your internet connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    private void showPictureDialog(final String call) {
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog_media_style, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        LinearLayout camera_sel = (LinearLayout) view.findViewById(R.id.camera);
        LinearLayout gallery_sel = (LinearLayout) view.findViewById(R.id.gallery);
        camera_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call.equals("profile"))
                    takePhotoFromCamera(CAMERA);
                else takePhotoFromCamera(CAMERA_CARD);
                dialog.dismiss();
            }
        });
        gallery_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call.equals("profile"))
                    selectImage(IMAGE_REQUEST_CODE);
                else selectImage(IMAGE_REQUEST_CODE_ID_CARD);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void takePhotoFromCamera(int code) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, code);
    }

    private void selectImage(int code) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                int rotateImage = getCameraPhotoOrientation(ProfileActivity.this, path, filePath);
                if (rotateImage == 90) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
                profileImage.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(bitmap);
            uploadImage();
        } else if (requestCode == IMAGE_REQUEST_CODE_ID_CARD && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(path, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                int rotateImage = getCameraPhotoOrientation(ProfileActivity.this, path, filePath);
                if (rotateImage == 90) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
//                profileImage.setImageBitmap(bitmap);
                uploadAdharPhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_CARD && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
//            profileImage.setImageBitmap(bitmap);
            uploadAdharPhoto();
        }
    }

    private String imageToString(Bitmap bitmap) {

        final int maxSize = 250;
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

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(ProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
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

    private void uploadImage() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.UPLOAD_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        kProgressHUD.dismiss();
//                        Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
//                                Toast.makeText(ProfileActivity.this, "Image Upload Successfully !!", Toast.LENGTH_SHORT).show();
                                TastyToast.makeText(getApplicationContext(), "Image Upload Successfully !!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                userManager.setPhoto(jsonObject.getString("img"));
                            } else
//                                Toast.makeText(ProfileActivity.this, "Image Updating Fail..!", Toast.LENGTH_SHORT).show();
                                TastyToast.makeText(getApplicationContext(), "Image Updating Fail..!", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            kProgressHUD.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("photo", imageToString(bitmap));
                params.put("memberid", userManager.getMID());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(ProfileActivity.this).addRequestQueue(stringRequest);
    }

    private void uploadAdharPhoto() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.STORE_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        adharPhoto(jsonObject.getString("image"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "." + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        MySingleton.getmInstanc(ProfileActivity.this).addRequestQueue(stringRequest);

    }

    private void adharPhoto(final String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ID_PHOTO_CHANGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        kProgressHUD.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                userManager.setAdharPhoto(url);
                                TastyToast.makeText(getApplicationContext(), "ID Card changed Successfully !!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            } else
                                TastyToast.makeText(getApplicationContext(), "Image Updating Fail..!", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            kProgressHUD.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, ".." + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("photo", url);
                params.put("memberid", userManager.getMID());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(ProfileActivity.this).addRequestQueue(stringRequest);
    }

    private void update() {

        final String username = etName.getText().toString().trim();
        final String adharno = etAdharNo.getText().toString().trim();
        final String acno = etAcno.getText().toString().trim();
        final String acname = etAcname.getText().toString().trim();
        final String ifsc = etIfsc.getText().toString().trim();
        final String branch = etBranch.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String nealoc = etNearloc.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String pincode = etPincode.getText().toString().trim();
        final String district = etDistrict.getText().toString().trim();
        final String state = etState.getText().toString().trim();

        if (TextUtils.isEmpty(username))
            TastyToast.makeText(getApplicationContext(), "Please enter name", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(email))
            TastyToast.makeText(getApplicationContext(), "Please enter email", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(adharno))
            TastyToast.makeText(getApplicationContext(), "Please enter aadhaar no.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(acno))
            TastyToast.makeText(getApplicationContext(), "Please enter account no.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(acname))
            TastyToast.makeText(getApplicationContext(), "Please enter account holder name", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(ifsc))
            TastyToast.makeText(getApplicationContext(), "Please enter ifsc code.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(branch))
            TastyToast.makeText(getApplicationContext(), "Please enter branch", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(address))
            TastyToast.makeText(getApplicationContext(), "Please enter address", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(nealoc))
            TastyToast.makeText(getApplicationContext(), "Please enter near location.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(district))
            TastyToast.makeText(this, "Please enter District.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(state))
            TastyToast.makeText(this, "Please enter State.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pincode))
            TastyToast.makeText(this, "Please enter Pin Code.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (pincode.length() < 6)
            TastyToast.makeText(this, "Please enter valid Pin code.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {

            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.UPDATE_PROFILE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
//                    Toast.makeText(ProfileActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (!jsonObject.getBoolean("error")) {
                            TastyToast.makeText(getApplicationContext(), "Profile Updated Successfully!!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            userManager.setName(username);
                            userManager.setAdharNo(adharno);
                            userManager.setAcno(acno);
                            userManager.setAcname(acname);
                            userManager.setIFSC(ifsc);
                            userManager.setBranch(branch);
                            userManager.setAddress(address);
                            userManager.setCity(nealoc);
                            userManager.setEmail(email);
                            userManager.setDistrict(district);
                            userManager.setPinCode(pincode);
                            userManager.setState(state);

                        } else
                            TastyToast.makeText(getApplicationContext(), "Profile Updating Fail..!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        kProgressHUD.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    params.put("username", username);
                    params.put("email", email);
                    params.put("adharno", adharno);
                    params.put("acno", acno);
                    params.put("acname", acname);
                    params.put("ifsc", ifsc);
                    params.put("branch", branch);
                    params.put("address", address);
                    params.put("nearloc", nealoc);
                    params.put("pincode", pincode);
                    params.put("district", district);
                    params.put("statename", state);
                    params.put("memberid", userManager.getMID());
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstanc(ProfileActivity.this).addRequestQueue(stringRequest);

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
