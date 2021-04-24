package com.tathastushop.app.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.ComMethod;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etMobile, etEmail, etRefID, etAddress,  etDistrict, etState, etNLoc, etAcName, etAcno, etIfscCode, etBankName,etBranch, etAdharNo, etPass;
    private String adhar_image = "";
    private Button btnSubmit, btnAdharUpload;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private ImageView ivUpload;
    private Bitmap bitmap;

    private final int IMAGE_REQUEST_CODE_ID_CARD = 233, CAMERA_CARD = 222;
    private BottomSheetDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Register");
        initEditText();
    }

    public void initEditText() {
        comMethod = new ComMethod(this);
        setProgress();
        etRefID = (EditText) findViewById(R.id.et_id_ref);
        etName = (EditText) findViewById(R.id.et_name);
        etAddress = (EditText) findViewById(R.id.et_address);
//        etPinCode = (EditText) findViewById(R.id.et_pincode);
        etDistrict = (EditText) findViewById(R.id.et_district);
        etState = (EditText) findViewById(R.id.et_state);
        etNLoc = (EditText) findViewById(R.id.et_near_loc);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etEmail = (EditText) findViewById(R.id.et_email);
        etAcName = (EditText) findViewById(R.id.et_ac_name);
        etAcno = (EditText) findViewById(R.id.et_ac_no);
        etIfscCode = (EditText) findViewById(R.id.et_ifsc_code);
        etBankName = (EditText) findViewById(R.id.et_bankname);
        etBranch = (EditText) findViewById(R.id.et_branch);
        etAdharNo = (EditText) findViewById(R.id.et_adhar_no);
        etPass = (EditText) findViewById(R.id.et_pass);
        btnSubmit = (Button) findViewById(R.id.btn_addMember);
        btnAdharUpload = (Button) findViewById(R.id.btn_choose_image);
        ivUpload = (ImageView) findViewById(R.id.iv_proof);
        btnSubmit.setOnClickListener(this);
        btnAdharUpload.setOnClickListener(this);
        AppCompatCheckBox checkbox = (AppCompatCheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_addMember) {
            if (comMethod.checkNetworkConnection()) {
                register();
            } else
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.btn_choose_image) {
            if (comMethod.checkNetworkConnection()) {
                showPictureDialog();
            } else
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadAdharPhoto() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.STORE_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(RegisterActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        adhar_image = jsonObject.getString("image");
                        ivUpload.setVisibility(View.VISIBLE);
                    } else ivUpload.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                Toast.makeText(RegisterActivity.this, "." + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        MySingleton.getmInstanc(RegisterActivity.this).addRequestQueue(stringRequest);

    }

    public void register() {

        final String name = etName.getText().toString().trim();
        final String ref = etRefID.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String pincode = getIntent().getStringExtra("pincode");
        final String district = etDistrict.getText().toString().trim();
        final String state = etState.getText().toString().trim();
        final String nloc = etNLoc.getText().toString().trim();
        final String mobile = etMobile.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String acname = etAcName.getText().toString().trim();
        final String acno = etAcno.getText().toString().trim();
        final String ifsc = etIfscCode.getText().toString().trim();
        final String branch = etBranch.getText().toString().trim();
        final String bankname = etBankName.getText().toString().trim();
        final String adharno = etAdharNo.getText().toString().trim();
        final String pass = etPass.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(ref))
            TastyToast.makeText(this, "Please enter Sponsor Id.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(name))
            TastyToast.makeText(this, "Please enter name.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(address))
            TastyToast.makeText(this, "Please enter Address.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(district))
            TastyToast.makeText(this, "Please enter District.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(state))
            TastyToast.makeText(this, "Please enter State.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pincode))
            TastyToast.makeText(this, "Please enter Pin Code.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (pincode.length() < 6)
            TastyToast.makeText(this, "Please enter valid Pin code.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(nloc))
            TastyToast.makeText(this, "Please enter near location.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(mobile))
            TastyToast.makeText(this, "Please enter contact number.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (mobile.length()<10)
            TastyToast.makeText(this, "Please enter valid contact number.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(email))
            TastyToast.makeText(this, "Please fill valid email.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (!email.matches(emailPattern))
            TastyToast.makeText(this, "Email not valid..", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(acname))
            TastyToast.makeText(this, "Please enter account holder name.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(acno))
            TastyToast.makeText(this, "Please enter account number.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(ifsc))
            TastyToast.makeText(this, "Please enter ifsc code.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(bankname))
            TastyToast.makeText(this, "Please enter bank name.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(branch))
            TastyToast.makeText(this, "Please enter branch.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(adharno))
            TastyToast.makeText(this, "Please enter aadhaar number.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (adharno.length()<12)
            TastyToast.makeText(this, "Please enter valid aadhaar number.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pass))
            TastyToast.makeText(this, "Please enter password.", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
//                    Toast.makeText(RegisterActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                            intent.putExtra("pid", getIntent().getStringExtra("pid"));
                            intent.putExtra("qty", getIntent().getIntExtra("qty", 0));
                            intent.putExtra("gst", getIntent().getIntExtra("gst", 0));
                            intent.putExtra("total", getIntent().getIntExtra("total", 0));
                            intent.putExtra("memberid", jsonObject.getString("memberid"));
                            intent.putExtra("password", pass);
                            intent.putExtra("phone", mobile);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            intent.putExtra("size", getIntent().getStringExtra("size"));
                            startActivity(intent);
                            finish();
                        } else {
                            comMethod.alertDialog("Error", jsonObject.getString("msg"), true, null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
//                    Toast.makeText(RegisterActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegisterActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", name);
                    params.put("refferal", ref);
                    params.put("address", address);
                    params.put("nloc", nloc);
                    params.put("mobile", mobile);
                    params.put("email", email);
                    params.put("acno", acno);
                    params.put("acname", acname);
                    params.put("ifsc", ifsc);
                    params.put("branch", branch);
                    params.put("bname", bankname);
                    params.put("adharno", adharno);
                    params.put("photo", adhar_image);
                    params.put("password", pass);
                    params.put("pincode", pincode);
                    params.put("district", district);
                    params.put("statename", state);

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(RegisterActivity.this).addRequestQueue(stringRequest);
        }

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
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
        startActivityForResult(intent, CAMERA_CARD);
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE_ID_CARD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE_ID_CARD && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(path, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                int rotateImage = comMethod.getCameraPhotoOrientation(RegisterActivity.this, path, filePath);
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
}
