package com.tathastushop.app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class TransRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private EditText etAmount;
    private Button btnAdd;
    private TextView tvAmount;
    private ComMethod comMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_request);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Withdrawal Request");
        setProgress();
        comMethod = new ComMethod(this);
        userManager = new UserManager(this);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvAmount.setText("₹" + userManager.getWallet());
        etAmount = (EditText) findViewById(R.id.et_amount);
        btnAdd = (Button) findViewById(R.id.btn_req);
        btnAdd.setOnClickListener(this);

//        Toast.makeText(this, ""+userManager.getMID(), Toast.LENGTH_SHORT).show();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(TransRequestActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_req)

            if (!TextUtils.isEmpty(etAmount.getText().toString().trim())) {
                int amt = Integer.parseInt(etAmount.getText().toString().trim());
                if (amt <= userManager.getWallet() && amt != 0) {
                    transReq();
                } else
                    comMethod.alertDialog("Withdrawal", "Insufficient Wallet Amount", true, null);
            } else comMethod.alertDialog("Withdrawal", "Amount id required", true, null);
    }

    public void transReq() {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.TRANS_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                kProgressHUD.dismiss();
//                Toast.makeText(TransRequestActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        comMethod.alertDialog("Withdrawal", "Request submitted successfully", false, null);
                        userManager.setWallet(jsonObject.getInt("balance"));
                        tvAmount.setText("₹" + userManager.getWallet());
                        etAmount.setText("");
                    } else
                        comMethod.alertDialog("Withdrawal", jsonObject.getString("errormsg"), true, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put("amount", etAmount.getText().toString().trim());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
