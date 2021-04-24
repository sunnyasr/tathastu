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

public class RefundActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPName, etPAmount, etProductID, etMessage;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Replacement");
        setProgress();
        userManager = new UserManager(this);
        comMethod = new ComMethod(this);
        etPName = (EditText) findViewById(R.id.et_pname);

        etPAmount = (EditText) findViewById(R.id.et_pamount);
        etProductID = (EditText) findViewById(R.id.et_pid);
        etMessage = (EditText) findViewById(R.id.et_msg);
        etPAmount = (EditText) findViewById(R.id.et_pamount);

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(RefundActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.btn_submit) {
            refund();
        }

    }

    private void refund() {

        final String pname = etPName.getText().toString().trim();
        final String pamount = etPAmount.getText().toString().trim();
        final String pid = etProductID.getText().toString().trim();
        final String message = etMessage.getText().toString().trim();


        if (TextUtils.isEmpty(pname)) {
            Toast.makeText(this, "Please enter product Name", Toast.LENGTH_SHORT).show();
            etPName.setFocusable(true);
        } else if (TextUtils.isEmpty(pamount)) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            etPAmount.setFocusable(true);
        } else if (TextUtils.isEmpty(pid)) {
            Toast.makeText(this, "Please enter Product Code", Toast.LENGTH_SHORT).show();
            etProductID.setFocusable(true);
        } else if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show();
            etMessage.setFocusable(true);
        } else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.REFUND, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("error")) {
                            comMethod.alertDialog("Refund", "Request failed", true, null);
                        } else {
                            etPAmount.setText("");
                            etPName.setText("");
                            comMethod.alertDialog("Refund", "Request submit successfully", false, null);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    kProgressHUD.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RefundActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    params.put("pname", pname);
                    params.put("pamount", pamount);
                    params.put("productid", pid);
                    params.put("message", message);
                    return params;
                }
            };

            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
            finish();
            return true;
        }
        return false;
    }
}