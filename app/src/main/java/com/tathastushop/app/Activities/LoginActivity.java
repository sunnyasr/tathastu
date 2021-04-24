package com.tathastushop.app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.tathastushop.app.PrefManager.LoginManager;
import com.tathastushop.app.PrefManager.UserManager;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginManager loginManager;
    private UserManager userManager;
    private EditText etPhone, etPass;
    private Button btnLogin;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = findViewById(R.id.toolbar_layout);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
//        mTitle.setText("Login");
        setProgress();
        loginManager = new LoginManager(this);
        userManager = new UserManager(this);

        etPhone = (EditText) findViewById(R.id.et_phone);
        etPass = (EditText) findViewById(R.id.et_pass);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        findViewById(R.id.btn_lost_password).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            login();
//            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.btn_lost_password) {
            startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
        }
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void login() {

        final String phone = etPhone.getText().toString().trim();
        final String pass = etPass.getText().toString().trim();

        if (TextUtils.isEmpty(phone))
            TastyToast.makeText(getApplicationContext(), "Please enter phone", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if(phone.length()<10)
            TastyToast.makeText(getApplicationContext(), "Please enter valid phone", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pass))
            TastyToast.makeText(getApplicationContext(), "Please enter password", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            kProgressHUD.dismiss();
//                    Toast.makeText(LoginActivity.this, "" + response, Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("error"))
                                    TastyToast.makeText(getApplicationContext(), "Login failed check Phone/Password", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                else {
                                    loginManager.setFirstTimeLaunch(false);
                                    userManager.setToken(jsonObject.getString("access_token"));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    finish();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("mobile", phone);
                    params.put("password", pass);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(LoginActivity.this).addRequestQueue(stringRequest);
        }
    }
}