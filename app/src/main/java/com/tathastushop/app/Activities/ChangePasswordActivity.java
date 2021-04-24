package com.tathastushop.app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.tathastushop.app.PrefManager.UserManager;
import com.tathastushop.app.R;
import com.tathastushop.app.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPass, etNewPass, etRenewPass;
    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Change Password");

        userManager = new UserManager(this);
        comMethod = new ComMethod(this);
        setProgress();
        etPass = (EditText) findViewById(R.id.et_old_pass);
        etNewPass = (EditText) findViewById(R.id.et_new_pass);
        etRenewPass = (EditText) findViewById(R.id.et_renew_pass);
        findViewById(R.id.btn_change_pass).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_change_pass) {
            changePass();
        }

    }

    private void changePass() {

        String pass = etPass.getText().toString().trim();
        String newPass = etNewPass.getText().toString().trim();
        String renewPass = etRenewPass.getText().toString().trim();

        if (TextUtils.isEmpty(pass))
            Toast.makeText(this, "Old Password is required", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(newPass))
            Toast.makeText(this, "New Password is required", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(renewPass))
            Toast.makeText(this, "Renew Password is required", Toast.LENGTH_SHORT).show();
        else if (!pass.equals(userManager.getPass()))
            Toast.makeText(this, "Old Password not match", Toast.LENGTH_SHORT).show();
        else if (!newPass.equals(renewPass))
            Toast.makeText(this, "Renew Password not match", Toast.LENGTH_SHORT).show();
        else {
            password();
        }
    }

    public void password() {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.CHANGE_PASS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                kProgressHUD.dismiss();
//                Toast.makeText(ChangePasswordActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        comMethod.alertDialog("Change Password", "Changed Password successfully", false, intent);
                        userManager.setPass(etNewPass.getText().toString().trim());

                        etPass.setText("");
                        etNewPass.setText("");
                        etRenewPass.setText("");
                    } else
                        comMethod.alertDialog("Change Password", "Change Password failed", true, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangePasswordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("memberid", userManager.getMID());
                params.put("newpass", etNewPass.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(ChangePasswordActivity.this)
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
