package com.tathastushop.app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sdsmdg.tastytoast.TastyToast;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etMember;
    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setProgress();
        etMember = (EditText) findViewById(R.id.et_mid);


        findViewById(R.id.btn_go_login).setOnClickListener(this);
        findViewById(R.id.btn_forgot).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_forgot) {
            getPassword();
        }
        if (v.getId() == R.id.btn_go_login) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(ForgotPasswordActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void getPassword() {

        String mid = etMember.getText().toString().trim();


        if (TextUtils.isEmpty(mid))
            TastyToast.makeText(getApplicationContext(), "Please enter member ID", TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.PASSWORD_FORGOT + mid, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
//                    Toast.makeText(ForgotPasswordActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        sendSMS(jsonObject.getString("username"),
                                jsonObject.getString("memberid"),
                                jsonObject.getString("tpassword"),
                                jsonObject.getString("mobile"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ForgotPasswordActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(ForgotPasswordActivity.this).addRequestQueue(stringRequest);
        }
    }

    public void sendSMS(String name, String memberid, String password, final String mobile) {

        final String message = "Dear " + name +
                "\nPassword Retrieved." +
                "\nMember ID : " + memberid +
                "\nPassword : " + password +
                "\nRegards" +
                "\nwww.teamtathastu.com";
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.SMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                etMember.setText("");
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                TastyToast.makeText(getApplicationContext(), "Please check your sms", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPasswordActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("to", mobile);
                params.put("uname", "ABHIJITDAS");
                params.put("pwd", "abhijit@1234");
                params.put("senderid", "TTHSTU");
                params.put("route", "T");
                params.put("msg", message);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }
}
